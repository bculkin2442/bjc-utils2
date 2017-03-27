package bjc.utils.ioutils;

import java.io.Reader;

/**
 * Utility methods for constructing instances of {@link BlockReader}
 * 
 * @author bjculkin
 *
 */
public class BlockReaders {
	/**
	 * Create a new simple block reader that works off a regex.
	 * 
	 * @param blockDelim
	 *            The regex that seperates blocks.
	 * 
	 * @param source
	 *            The reader to get blocks from.
	 * 
	 * @return A configured simple reader.
	 */
	public static SimpleBlockReader simple(String blockDelim, Reader source) {
		return new SimpleBlockReader(blockDelim, source);
	}

	/**
	 * Create a new pushback block reader.
	 * 
	 * @param src
	 *            The block reader to read blocks from.
	 * 
	 * @return A configured pushback reader.
	 */
	public static PushbackBlockReader pushback(BlockReader src) {
		return new PushbackBlockReader(src);
	}

	/**
	 * Create a new triggered block reader.
	 * 
	 * @param source
	 *            The block reader to read blocks from.
	 * 
	 * @param action
	 *            The action to execute before reading a block.
	 * 
	 * @return A configured triggered block reader.
	 */
	public static BlockReader trigger(BlockReader source, Runnable action) {
		return new TriggeredBlockReader(source, action);
	}
}
