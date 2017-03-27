package bjc.utils.cli.fds;

import java.util.function.Consumer;

import bjc.utils.ioutils.Block;

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
		 * command, data will be read inline separated by spaces until a
		 * semicolon is read.
		 * 
		 * The semicolon can be escaped with a backslash.
		 */
		INLINE,
		/**
		 * Reads every character in the block, but after a terminal
		 * command, data will be read inline with each character being a
		 * separate item until a semicolon is read.
		 * 
		 * The semicolon can be escaped with a backslash.
		 */
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
	 * Function to add a command block to be processed.
	 */
	public Consumer<Block> enqueCommand;

	/**
	 * Function to add a data block to be processed.
	 */
	public Consumer<Block> enqueData;

	/**
	 * Create a new interface state.
	 * 
	 * @param stat
	 *                The initial state for the interface.
	 * 
	 * @param inputMode
	 *                The input mode for the interface.
	 */
	public FDSState(S stat, InputMode inputMode, Consumer<Block> comQueue, Consumer<Block> dataQueue) {
		state = stat;
		mode = inputMode;

		enqueCommand = comQueue;
		enqueData = dataQueue;
	}
}