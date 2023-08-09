package bjc.utils.cli;

import static bjc.utils.cli.TerminalCodes.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.function.Consumer;

import bjc.data.Either;

/**
 * Implementation of {@link Terminal} using {@link Reader} and {@link Writer}
 * 
 * @author bjcul
 *
 */
public class StreamTerminal implements Terminal, Runnable {
	private SortedSet<Long> pendingRequests;
	private ConcurrentMap<Long, String> pendingReplies;

	private Lock replyLock;
	private Condition replyCondition;

	private Queue<String> pendingOutput;

	private boolean running;

	private Scanner inputScanner;
	private Writer output;

	private String prefix;
	private Consumer<String> mode;

	private long currentRequest = -1;

	/**
	 * Create a new stream terminal.
	 * 
	 * @param input  The input source
	 * @param output The output source
	 */
	public StreamTerminal(Reader input, Writer output) {
		this(input, output, null, null);
	}

	/**
	 * Create a new stream terminal which backs an application
	 * 
	 * @param input  The input source
	 * @param output The output source
	 * @param prefix The string any input must start with to be directed to the
	 *               terminal
	 * @param mode   The place where all input not directed to the terminal is
	 *               written.
	 */
	public StreamTerminal(Reader input, Writer output, String prefix, Consumer<String> mode) {
		this.inputScanner = new Scanner(input);
		this.output = output;

		this.pendingRequests = new TreeSet<>();
		this.pendingReplies = new ConcurrentHashMap<>();
		this.pendingOutput = new ArrayDeque<>();

		this.replyLock = new ReentrantLock();
		this.replyCondition = replyLock.newCondition();

		this.prefix = prefix;
		this.mode = mode;
	}

	@Override
	public void run() {
		running = true;
		try {
			output.write(INFO_STARTCOMPROC.toString() + "\n");
			while (!pendingOutput.isEmpty())
				output.write(pendingOutput.remove());
			output.flush();
		} catch (IOException e) {
			// TODO Consider if there is some better way to handle these
			throw new RuntimeException(e);
		}

		overall: while (running && inputScanner.hasNextLine()) {
			try {
				while (!pendingOutput.isEmpty())
					output.write(pendingOutput.remove());
				output.flush();

				String ln = inputScanner.nextLine();
				if (prefix != null && ln.startsWith(prefix)) {
					ln = ln.substring(prefix.length());
					String com = "";
					int spcIdx = ln.indexOf(' ');
					if (spcIdx == -1) {
						com = ln;
					} else {
						com = ln.substring(0, spcIdx);
						ln = ln.substring(spcIdx + 1);
					}

					comswt: switch (com) {
					case "r": {
						// General command format is 'r <request no.>,<reply>
						String subRep = ln.substring(2);
						// Process a reply
						int comIndex = subRep.indexOf(',');
						long repNo = 0;
						if (comIndex == -1) {
							// Reply to the oldest message by default
							repNo = pendingRequests.first();
						} else {
							String repStr = subRep.substring(0, comIndex);
							try {
								repNo = Long.parseLong(repStr);
							} catch (NumberFormatException nfex) {
								output.write(ERROR_INVREPNO.toString() + "\n");
								output.flush();
								continue overall;
							}
							// Skip over the comma
							subRep = subRep.substring(comIndex + 1);
						}

						if (!pendingRequests.contains(repNo)) {
							output.write(ERROR_UNKREPNO.toString() + "\n");
							output.flush();
							continue overall;
						}

						pendingRequests.remove(repNo);
						pendingReplies.put(repNo, subRep);

						replyLock.lock();
						replyCondition.signalAll();
						replyLock.unlock();
						break comswt;
					}
					case "q":
						running = false;
						break comswt;
					default:
						output.write(ERROR_UNRECCOM.toString() + "\n");
						output.flush();
					}
				} else {
					mode.accept(ln);
				}
				
				while (!pendingOutput.isEmpty())
					output.write(pendingOutput.remove());
				output.flush();
			} catch (IOException ioex) {
				throw new RuntimeException(ioex);
			}
		}

		running = false;
		try {
			while (!pendingOutput.isEmpty())
				output.write(pendingOutput.remove());
			output.write(INFO_ENDCOMPROC.toString() + "\n");
			output.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long submitRequest(String req) {
		long reqNo = currentRequest + 1;
		currentRequest += 1;

		pendingOutput.add(reqNo + " " + req + "\n");
		pendingRequests.add(reqNo);
		return reqNo;
	}

	@Override
	public String awaitReply(long id) throws InterruptedException {
		if (pendingReplies.containsKey(id))
			return pendingReplies.get(id);
		while (true) {
			replyLock.lock();
			replyCondition.await();
			replyLock.unlock();
			// Explanation: Since the reply map is add-only, the lock isn't actually
			// protecting anything. We just want to wait until a response is received.
			if (pendingReplies.containsKey(id))
				return pendingReplies.get(id);
		}
	}

	@Override
	public Optional<String> awaitReply(long id, TimeUnit unit, long delay) throws InterruptedException {
		if (pendingReplies.containsKey(id))
			return Optional.of(pendingReplies.get(id));
		while (true) {
			replyLock.lock();
			boolean stat = replyCondition.await(delay, unit);
			replyLock.unlock();

			// If we timed out, say so
			if (stat == false)
				return Optional.empty();
			// Explanation: Since the reply map is add-only, the lock isn't actually
			// protecting anything. We just want to wait until a response is received.
			if (pendingReplies.containsKey(id))
				return Optional.of(pendingReplies.get(id));
		}
	}

	@Override
	public Optional<String> checkReply(long id) {
		return Optional.ofNullable(pendingReplies.get(id));
	}

	@Override
	public String submitRequestSync(String req) throws InterruptedException {
		return awaitReply(submitRequest(req));
	}

	@Override
	public Either<String, Long> submitRequestSync(String req, TimeUnit unit, long delay) throws InterruptedException {
		long id = submitRequest(req);
		Optional<String> rep = awaitReply(id, unit, delay);
		return rep.isEmpty() ? Either.right(id) : Either.left(rep.get());
	}
	
	/**
	 * Add a string to be printed next time output is printed.
	 * 
	 * @param out The output
	 */
	public void addOutput(String out) {
		pendingOutput.add(out);
	}

	/**
	 * Set the mode for handling non-terminal input
	 * 
	 * @param mode The new mode for handling non-terminal input
	 */
	public void setMode(Consumer<String> mode) {
		this.mode = mode;
	}
}