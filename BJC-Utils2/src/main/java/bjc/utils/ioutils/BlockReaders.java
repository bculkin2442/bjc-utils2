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
	 *                The regex that seperates blocks.
	 * 
	 * @param source
	 *                The reader to get blocks from.
	 * 
	 * @return A configured simple reader.
	 */
	public static BlockReader simple(String blockDelim, Reader source) {
		return new SimpleBlockReader(blockDelim, source);
	}
}
