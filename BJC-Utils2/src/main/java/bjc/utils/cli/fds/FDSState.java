package bjc.utils.cli.fds;

import java.io.PrintStream;

import bjc.utils.esodata.SimpleStack;
import bjc.utils.esodata.Stack;
import bjc.utils.ioutils.PushbackBlockReader;

/**
 * Internal state for an FDS interface.
 * 
 * @author bjculkin
 *
 * @param <S>
 *                The state type of the interface.
 */
public class FDSState<S> {
	/**
	 * The input mode for the interface.
	 * 
	 * @author bjculkin
	 *
	 */
	public static enum InputMode {
		/**
		 * Normal mode.
		 * 
		 * Reads only the first character in the block as a command.
		 */
		NORMAL,
		/**
		 * Reads every character in the block as a command.
		 */
		CHORD,
		/**
		 * Reads every character in the block, but after a terminal
		 * command, data will be read in-line separated by spaces until
		 * a semicolon is read.
		 * 
		 * The semicolon can be escaped with a backslash.
		 */
		INLINE,
		/**
		 * Reads every character in the block, but after a terminal
		 * command, data will be read in-line with each character being
		 * a separate item until a semicolon is read.
		 * 
		 * The semicolon can be escaped with a backslash.
		 */
		CHARINLINE,
	}

	/**
	 * The state of the interface
	 */
	public S		state;
	/**
	 * The input mode for the interface.
	 */
	public InputMode	mode;

	/**
	 * The modes being used.
	 */
	public Stack<FDSMode<S>> modes;

	/**
	 * The source to read command blocks from.
	 */
	public PushbackBlockReader comin;

	/**
	 * The source to read data blocks from.
	 */
	public PushbackBlockReader datain;

	/**
	 * The destination for output.
	 */
	public PrintStream printer;

	/**
	 * Create a new interface state.
	 * 
	 * @param stat
	 *                The initial state for the interface.
	 * 
	 * @param inputMode
	 *                The input mode for the interface.
	 * @param cmin
	 *                The source of command blocks.
	 * 
	 * @param datin
	 *                The source of data blocks.
	 * 
	 * @param print
	 *                The destination for output.
	 */
	public FDSState(S stat, InputMode inputMode, PushbackBlockReader cmin, PushbackBlockReader datin,
			PrintStream print) {
		state = stat;
		mode = inputMode;

		comin = cmin;
		datain = datin;
		printer = print;

		modes = new SimpleStack<>();
	}
}