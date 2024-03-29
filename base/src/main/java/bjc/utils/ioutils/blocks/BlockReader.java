package bjc.utils.ioutils.blocks;

import java.io.*;
import java.util.*;
import java.util.function.*;

// @NOTE Ben Culkin 12/16/2020 :IterableIterator
// Having this class implement both Iterator and Iterable is somewhat suspect.
// I understand why it was done, because that allows easy use of the 'for-each'
// loop semantics, but there is a question of whether it is a good idea to have
// instances of Iterable that will always give the same Iterator without
// explicitly saying that is what they are doing.

/** A source of blocks of characters, marked with line numbers as to block
 * start/block end.
 *
 * @author bjculkin */
public interface
BlockReader extends AutoCloseable, Iterator<Block>, Iterable<Block> {
	/** Check if this reader has an available block.
	 *
	 * @return Whether or not another block is available. */
	boolean hasNextBlock();

	/** Get the current block.
	 *
	 * @return The current block, or null if there is no current block. */
	Block getBlock();

	/** Move to the next block.
	 *
	 * @return Whether or not the next block was successfully read. */
	boolean nextBlock();

	/** Retrieve the number of blocks that have been read so far.
	 *
	 * @return The number of blocks read so far. */
	int getBlockCount();

	@Override
	void close() throws IOException;

	/* Methods with default impls. */

	/** Execute an action for each remaining block.
	 *
	 * @param action The action to execute for each block. */
	default void forEachBlock(final Consumer<Block> action) {
		while (hasNext()) {
			action.accept(next());
		}
	}

	@Override
	default Iterator<Block> iterator() {
		return this;
	}

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
