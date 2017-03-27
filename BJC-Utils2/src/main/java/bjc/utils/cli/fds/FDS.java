package bjc.utils.cli.fds;

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
	 * @param blockSource
	 *                The command input source for the FDS mode.
	 * 
	 * @param datain
	 *                The data input source for the FDS mode.
	 * 
	 * @param printer
	 *                The output source for the FDS mode.
	 * 
	 * @param mode
	 *                The mode to start in.
	 * 
	 * @param state
	 *                The initial state for the mode.
	 * 
	 * @return The final state of the mode.
	 * 
	 * @throws FDSException
	 *                 If something went wrong during mode execution.
	 */
	public static <S> S runFDS(BlockReader blockSource, BlockReader datain, PrintStream printer, FDSMode<S> mode,
			FDSState<S> state) throws FDSException {
		//printer.print("Enter a command (m for help): ");

		while (blockSource.hasNext()) {
			Block comBlock = blockSource.next();

			handleCommandString(comBlock, blockSource, datain, printer, mode, state);

			//printer.print("Enter a command (m for help): ");
		}

		return state.state;
	}

	private static <S> void handleCommandString(Block comBlock, BlockReader blockSource, BlockReader datain,
			PrintStream printer, FDSMode<S> mode, FDSState<S> state) throws FDSException {
		String comString = comBlock.contents.trim();

		switch (state.mode) {
		case CHORD:
			if (comString.length() > 1) {
				for (char c : comString.substring(1).toCharArray()) {
					Block newCom = new Block(comBlock.blockNo + 1, Character.toString(c),
							comBlock.startLine, comBlock.startLine);

					state.enqueCommand.accept(newCom);
				}
			}
		case NORMAL:
			handleCommand(comString.charAt(0), blockSource, datain, printer, mode, state);
			break;

		default:
			throw new FDSException(String.format("Unknown input mode '%s'", state.mode));
		}
	}

	private static <S> void handleCommand(char charAt, BlockReader blockSource, BlockReader datain,
			PrintStream printer, FDSMode<S> mode, FDSState<S> state) {
		printer.printf("Recieved command '%s'\n", charAt);
	}
}
