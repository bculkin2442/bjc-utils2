package bjc.utils.test.cli;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

import org.junit.Test;

import bjc.utils.cli.StreamTerminal;

/**
 * Test {@link StreamTerminal}
 * 
 * @author bjcul
 *
 */
public class StreamTerminalTest {

	/**
	 * Single-threaded test of stream-terminal
	 */
	@SuppressWarnings("resource")
	@Test
	public void stTest() {
		try {
			PipedInputStream inPipeIn = new PipedInputStream();
			PipedOutputStream inPipeOut = new PipedOutputStream(inPipeIn);

			InputStreamReader inPipeReader = new InputStreamReader(inPipeIn);
			OutputStreamWriter inPipeWriter = new OutputStreamWriter(inPipeOut);

			PipedInputStream outPipeIn = new PipedInputStream();
			PipedOutputStream outPipeOut = new PipedOutputStream(outPipeIn);

			InputStreamReader outPipeReader = new InputStreamReader(outPipeIn);
			OutputStreamWriter outPipeWriter = new OutputStreamWriter(outPipeOut);

			List<String> repList = new ArrayList<>();
			Consumer<String> repAction = repList::add;
			
			StreamTerminal terminal = new StreamTerminal(inPipeReader, outPipeWriter, "/", repAction);

			long reqID1 = terminal.submitRequest("Request 1");
			long reqID2 = terminal.submitRequest("Request 2");

			assertEquals(0, reqID1);
			assertEquals(1, reqID2);

			inPipeWriter.write("r 0,A\n");
			inPipeWriter.write("/r 0,A\n");
			inPipeWriter.write("/r 1,B\n");
			inPipeWriter.write("/q\n");
			inPipeWriter.flush();
			inPipeWriter.close();

			terminal.run();

			outPipeWriter.flush();
			outPipeWriter.close();

			try (Scanner outPipeScanner = new Scanner(outPipeReader)) {
				assertTrue(outPipeScanner.hasNextLine());
				String out1 = outPipeScanner.nextLine();
				assertEquals("IOLPI00001 STARTING PROCESSING", out1);

				assertTrue(outPipeScanner.hasNextLine());
				String out2 = outPipeScanner.nextLine();
				assertEquals("0 Request 1", out2);

				assertTrue(outPipeScanner.hasNextLine());
				String out3 = outPipeScanner.nextLine();
				assertEquals("1 Request 2", out3);

				assertTrue(outPipeScanner.hasNextLine());
				String out4 = outPipeScanner.nextLine();
				assertEquals("IOLPI00002 ENDING PROCESSING", out4);
			}
			
			assertEquals("A", terminal.awaitReply(reqID1));
			assertEquals("B", terminal.awaitReply(reqID2));
			
			assertEquals(1, repList.size());
			assertEquals("r 0,A", repList.get(0));
		} catch (IOException ioex) {
			throw new RuntimeException(ioex);
		} catch (InterruptedException iex) {
			throw new RuntimeException(iex);
		}
	}
	
	// TODO write a test that ensures that the multi-threading/reply-wait functionality works
}
