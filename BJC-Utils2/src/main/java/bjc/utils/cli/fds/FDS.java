package bjc.utils.cli.fds;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collection;

import bjc.utils.cli.CommandHelp;
import bjc.utils.cli.GenericHelp;
import bjc.utils.cli.fds.FDSState.InputMode;
import bjc.utils.ioutils.Block;
import bjc.utils.ioutils.BlockReader;
import bjc.utils.ioutils.PushbackBlockReader;

import static bjc.utils.ioutils.BlockReaders.*;

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
	private static SimpleFDSMode<?> miscMode;

	static {
		miscMode = new SimpleFDSMode<>();

		miscMode.addCommand('X', (stat) -> {

		}, new GenericHelp("load-script\tLoad a script from a file", ""));
	}

	/**
	 * Run a provided FDS mode until it is exited or there is no more input.
	 * 
	 * @param state
	 *                The initial state for the mode.
	 * 
	 * @return The final state of the mode.
	 * 
	 * @throws FDSException
	 *                 If something went wrong during mode execution.
	 */
	public static <S> S runFDS(FDSState<S> state) throws FDSException {
		BlockReader blockSource = state.comin;

		while (blockSource.hasNext() && !state.modes.empty()) {
			Block comBlock = blockSource.next();

			handleCommandString(comBlock, state);
		}

		return state.state;
	}

	private static <S> void handleCommandString(Block comBlock, FDSState<S> state) throws FDSException {
		String comString = comBlock.contents.trim();

		switch (state.mode) {
		case CHORD:
			chordCommand(comBlock, state, comString);
		case NORMAL:
			handleCommand(comString.charAt(0), state);
			break;
		case INLINE:
			break;
		case CHARINLINE:
			break;
		default:
			throw new FDSException(String.format("Unknown input mode '%s'", state.mode));
		}
	}

	private static <S> void chordCommand(Block comBlock, FDSState<S> state, String comString) {
		PushbackBlockReader source = state.comin;

		for (int i = 1; i < comString.length(); i++) {
			char c = comString.charAt(i);

			Block newCom = new Block(comBlock.blockNo + 1, Character.toString(c), comBlock.startLine,
					comBlock.startLine);

			source.addBlock(newCom);
		}
	}

	@SuppressWarnings("unchecked")
	private static <S> void handleCommand(char com, FDSState<S> state) throws FDSException {
		if (state.modes.empty()) return;

		PrintStream printer = state.printer;

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
			loadScript(state);
			break;
		case 'q':
			state.modes.drop();
			break;
		case 'Q':
			state.modes.drop(state.modes.size());
			break;
		case 'm':
			helpSummary(printer, state);
			break;
		case 'M':
			/*
			 * We'll see if this cast actually causes any issues.
			 * 
			 * None of the commands in misc. mode should depend on
			 * the state type, so it shouldn't matter.
			 */
			state.modes.push((FDSMode<S>) miscMode);
			break;
		default:
			FDSMode<S> curMode = state.modes.top();

			if (curMode.hasSubmode(com)) {
				state.modes.push(curMode.getSubmode(com));
			} else if (curMode.hasCommand(com)) {
				curMode.getCommand(com).run(state);
			} else {
				printer.printf("? UBC '%s'\n", com);
			}
		}
	}

	private static <S> void loadScript(FDSState<S> state) {
		String fileName = state.datain.next().contents.split(" ")[0];

		PrintStream printer = state.printer;

		try {
			FileInputStream fis = new FileInputStream(fileName);

			BlockReader reader = simple("\\R", new InputStreamReader(fis));

			state.comin = pushback(serial(reader, state.comin));
			state.datain = pushback(serial(reader, state.datain));
		} catch (FileNotFoundException fnfex) {
			printer.printf("? FNF '%s'\n", fileName);
		}
	}

	private static <S> void helpSummary(PrintStream printer, FDSState<S> state) {
		FDSMode<S> mode = state.modes.top();

		printer.printf("Help for mode %s:\n", mode.getName());

		for (char bound : mode.registeredChars()) {
			Collection<CommandHelp> help = mode.getHelp(bound);

			if (help.size() > 1) {
				for (CommandHelp hlp : help) {
					printer.printf("%s\t-\t%s", hlp.getSummary());
				}
			} else {
				CommandHelp hlp = help.iterator().next();

				printer.printf("%s\t-\t%s", hlp.getSummary());
			}
		}
	}
}