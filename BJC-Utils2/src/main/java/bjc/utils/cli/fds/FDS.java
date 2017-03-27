package bjc.utils.cli.fds;

import java.io.PrintStream;

import bjc.utils.cli.fds.FDSState.InputMode;
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
	 *            The command input source for the FDS mode.
	 * 
	 * @param datain
	 *            The data input source for the FDS mode.
	 * 
	 * @param printer
	 *            The output source for the FDS mode.
	 * 
	 * @param mode
	 *            The mode to start in.
	 * 
	 * @param state
	 *            The initial state for the mode.
	 * 
	 * @return The final state of the mode.
	 * 
	 * @throws FDSException
	 *             If something went wrong during mode execution.
	 */
	public static <S> S runFDS(BlockReader blockSource, BlockReader datain, PrintStream printer, FDSState<S> state)
			throws FDSException {
		while (blockSource.hasNext()) {
			Block comBlock = blockSource.next();

			handleCommandString(comBlock, blockSource, datain, printer, state);
		}

		return state.state;
	}

	private static <S> void handleCommandString(Block comBlock, BlockReader blockSource, BlockReader datain,
			PrintStream printer, FDSState<S> state) throws FDSException {
		String comString = comBlock.contents.trim();

		switch (state.mode) {
		case CHORD:
			chordCommand(comBlock, state, comString);
		case NORMAL:
			handleCommand(comString.charAt(0), blockSource, datain, printer, state);
			break;
		default:
			throw new FDSException(String.format("Unknown input mode '%s'", state.mode));
		}
	}

	private static <S> void chordCommand(Block comBlock, FDSState<S> state, String comString) {
		for (int i = 1; i < comString.length(); i++) {
			char c = comString.charAt(i);

			Block newCom = new Block(comBlock.blockNo + 1, Character.toString(c), comBlock.startLine,
					comBlock.startLine);

			state.enqueCommand.accept(newCom);
		}
	}

	private static <S> void handleCommand(char com, BlockReader blockSource, BlockReader datain, PrintStream printer,
			FDSState<S> state) throws FDSException {
		/*
		 * Handle built-in commands over user commands.
		 */
		switch (com) {
		case 'x':
			if (state.mode == InputMode.CHORD) {
				state.mode = InputMode.NORMAL;
			} else if (state.mode == InputMode.NORMAL) {
				state.mode = InputMode.CHORD;
			} else {
				printer.println("? CNV\n");
			}
			break;
		case 'X':
			/*
			 * TODO implement loading scripts from file.
			 */
			break;
		default:
			FDSMode<S> curMode = state.modes.top();

			if (curMode.hasSubmode(com)) {
				curMode.getCommand(com).run(state.state, datain);
			} else if (curMode.hasCommand(com)) {
				state.modes.push(curMode.getSubmode(com));
			} else {
				printer.printf("? UBC '%s'", com);
			}
		}
	}
}
