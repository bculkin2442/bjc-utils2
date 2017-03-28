package bjc.utils.ioutils;

import java.io.IOException;
import java.util.Deque;

/**
 * Provides a means of concatenating two block readers.
 * 
 * @author bjculkin
 *
 */
public class SerialBlockReader implements BlockReader {
	private Deque<BlockReader> readerQueue;

	private int blockNo;

	/**
	 * Create a new serial block reader.
	 * 
	 * @param readers
	 *                The readers to pull from, in the order to pull from
	 *                them.
	 */
	public SerialBlockReader(BlockReader... readers) {
		for (BlockReader reader : readers) {
			readerQueue.add(reader);
		}
	}

	@Override
	public boolean hasNextBlock() {
		if (readerQueue.isEmpty()) return false;

		boolean hasBlock = readerQueue.peek().hasNextBlock();

		boolean cont = hasBlock || readerQueue.isEmpty();

		while (!cont) {
			try {
				readerQueue.pop().close();
			} catch (IOException ioex) {
				throw new IllegalStateException("Exception thrown by discarded reader", ioex);
			}

			hasBlock = readerQueue.peek().hasNextBlock();

			cont = hasBlock || readerQueue.isEmpty();
		}

		return hasBlock;
	}

	@Override
	public Block getBlock() {
		if (readerQueue.isEmpty())
			return null;
		else return readerQueue.peek().getBlock();
	}

	@Override
	public boolean nextBlock() {
		if (readerQueue.isEmpty()) return false;

		boolean gotBlock = readerQueue.peek().nextBlock();

		boolean cont = gotBlock || readerQueue.isEmpty();

		while (!cont) {
			try {
				readerQueue.pop().close();
			} catch (IOException ioex) {
				throw new IllegalStateException("Exception thrown by discarded reader", ioex);
			}

			gotBlock = readerQueue.peek().nextBlock();

			cont = gotBlock || readerQueue.isEmpty();
		}

		if (cont) blockNo += 1;

		return cont;
	}

	@Override
	public int getBlockCount() {
		return blockNo;
	}

	@Override
	public void close() throws IOException {
		while (!readerQueue.isEmpty()) {
			BlockReader reader = readerQueue.pop();

			reader.close();
		}
	}
}