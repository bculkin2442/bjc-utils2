package bjc.utils.cli.objects;

import bjc.utils.ioutils.blocks.BlockReader;

import java.io.Reader;
import java.util.Map;

/**
 * The state of the block reader.
 *
 * @author Ben Culkin
 */
public class BlockReaderState {
	/**
	 * All of the configured block readers.
	 */
	public final Map<String, BlockReader> readers;
	/**
	 * All of the configured I/O sources.
	 */
	public final Map<String, Reader> sources;

	/**
	 * Create a new set of state for the block reader.
	 *
	 * @param readers
	 *        The set of configured block readers.
	 *
	 * @param sources
	 *        The set of configured I/O sources.
	 */
	public BlockReaderState(Map<String, BlockReader> readers, Map<String, Reader> sources) {
		this.readers = readers;
		this.sources = sources;
	}
}