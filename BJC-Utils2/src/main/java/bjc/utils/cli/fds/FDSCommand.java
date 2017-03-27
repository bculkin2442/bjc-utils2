package bjc.utils.cli.fds;

import bjc.utils.ioutils.BlockReader;

/**
 * A command attached to an FDS interface.
 * 
 * @author bjculkin
 *
 * @param <S>
 *                The state type of the interface.
 */
public interface FDSCommand<S> {
	/**
	 * Run this command.
	 * 
	 * @param state
	 *                The current FDS state.
	 * 
	 * @param input
	 *                The source for data input.
	 * 
	 * @return The new state, after running the command.
	 */
	S run(S state, BlockReader input);
}
