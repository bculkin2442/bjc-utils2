package bjc.utils.ioutils.blocks;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * A source of blocks of characters, marked with line numbers as to block
 * start/block end.
 * 
 * @author bjculkin
 *
 */
public interface BlockReader extends AutoCloseable, Iterator<Block> {
	/**
	 * Check if this reader has an available block.
	 * 
	 * @return Whether or not another block is available.
	 */
	boolean hasNextBlock();

	/**
	 * Get the current block.
	 * 
	 * @return The current block, or null if there is no current block.
	 */
	Block getBlock();

	/**
	 * Move to the next block.
	 * 
	 * @return Whether or not the next block was successfully read.
	 */
	boolean nextBlock();

	/**
	 * Execute an action for each remaining block.
	 * 
	 * @param action
	 *                The action to execute for each block
	 */
	default void forEachBlock(Consumer<Block> action) {
		while (hasNext()) {
			action.accept(next());
		}
	}

	/**
	 * Retrieve the number of blocks that have been read so far.
	 * 
	 * @return The number of blocks read so far.
	 */
	int getBlockCount();

	void close() throws IOException;

	@Override
	default boolean hasNext() {
		return hasNextBlock();
	}

	@Override
	default Block next() {
		nextBlock();

		return getBlock();
	}
}