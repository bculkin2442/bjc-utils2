package bjc.utils.cli.fds;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import bjc.utils.ioutils.Block;
import bjc.utils.ioutils.BlockReader;

/**
 * Runs a FDS (FDiskScript) interface.
 * 
 * This is a rudimentary console interface inspired heavily by FDisk's interface
 * style.
 * 
 * Commands are denoted by a single character, but can invoke submodes.
 * 
 * @author bjculkin
 *
 */
public class FDS {
	/**
	 * Run a provided FDS mode until it is exited or there is no more input.
	 * 
	 * @param comin
	 *                The command input source for the FDS mode.
	 * 
	 * @param datain
	 *                The data input source for the FDS mode.
	 * 
	 * @param out
	 *                The output source for the FDS mode.
	 * 
	 * @param initialMode
	 *                The mode to start in.
	 * 
	 * @param initialState
	 *                The initial state for the mode.
	 * 
	 * @return The final state of the mode.
	 * 
	 * @throws FDSException
	 *                 If something went wrong during mode execution.
	 */
	public static <S> S runFDS(InputStream comin, InputStream datain, OutputStream out, FDSMode<S> initialMode,
			S initialState) throws FDSException {
		PrintStream printer = new PrintStream(out);

		try (BlockReader blockSource = new BlockReader("\\R", new InputStreamReader(comin))) {
			printer.print("Enter a command (m for help): ");

			while (blockSource.hasNext()) {
				Block comBlock = blockSource.next();

				String comString = comBlock.contents.trim();
				
				char comChar = comString.charAt(0);
				
				printer.println(String.format("\nRecieved command '%s'\n", comChar));

				printer.print("Enter a command (m for help): ");
			}
		} catch (Exception ex) {
			throw new FDSException("Unexpected I/O error", ex);
		}

		return initialState;
	}
}
