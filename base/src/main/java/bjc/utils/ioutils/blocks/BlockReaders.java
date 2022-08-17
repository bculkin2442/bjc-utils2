package bjc.utils.ioutils.blocks;

import java.io.*;
import java.util.Iterator;

/** Utility methods for constructing instances of {@link BlockReader}
 *
 * @author bjculkin */
public class BlockReaders {
	/** Create a new simple block reader that works off a regex.
	 *
	 * @param blockDelim The regex that separates blocks.
	 * @param source The reader to get blocks from.
	 *
	 * @return A configured simple reader. */
	public static SimpleBlockReader simple(
			final String blockDelim, final Reader source) 
	{
		return new SimpleBlockReader(blockDelim, source);
	}

	/** Create a new pushback block reader.
	 *
	 * @param src The block reader to read blocks from.
	 *
	 * @return A configured pushback reader. */
	public static PushbackBlockReader pushback(final BlockReader src) {
		return new PushbackBlockReader(src);
	}

	/** Create a new triggered block reader.
	 *
	 * @param source The block reader to read blocks from.
	 * @param action The action to execute before reading a block.
	 *
	 * @return A configured triggered block reader. */
	public static BlockReader trigger(
			final BlockReader source, final Runnable action)
	{
		return new TriggeredBlockReader(source, action);
	}

	/** Create a new layered block reader.
	 *
	 * @param primary The first source to read blocks from.
	 * @param secondary The second source to read blocks from.
	 *
	 * @return A configured layered block reader. */
	public static BlockReader layered(
			final BlockReader primary, final BlockReader secondary)
	{
		return new LayeredBlockReader(primary, secondary);
	}

	/** Create a new serial block reader.
	 *
	 * @param readers The readers to pull from, in the order to pull from them.
	 *
	 * @return A configured serial block reader. */
	public static BlockReader serial(final BlockReader... readers) {
		return new SerialBlockReader(readers);
	}
	
	/**
	 * Create a block reader from an iterator of blocks.
	 * 
	 * @param iter The iterator of blocks.
	 * 
	 * @return A block reader which returns blocks from the given iterator.
	 */
	public static BlockReader fromIterator(final Iterator<Block> iter) {
		return new IteratorBlockReader(iter);
	}
}
