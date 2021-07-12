package bjc.utils.ioutils.blocks;

import java.io.*;
import java.util.function.*;

/** A block reader composed from functions.
 *
 * @author EVE */
public class BoundBlockReader implements BlockReader {
	/** A function that serves to close an I/O source.
	 *
	 * @author EVE */
	@FunctionalInterface
	public interface Closer {
		/** Close the I/O source this is attached to.
		 *
		 * @throws IOException If something goes wrong */
		public void close() throws IOException;
	}

	private BooleanSupplier checker;
	private Supplier<Block> getter;
	private Closer closer;

	private Block current;

	private int blockNo;

	/** Create a new bound block reader.
	 *
	 * @param blockChecker Predicate for checking if a block is available
	 * @param blockGetter Function to retrieve a block
	 * @param blockCloser Function to close a block source */
	public BoundBlockReader(
			BooleanSupplier blockChecker,
			Supplier<Block> blockGetter,
			Closer blockCloser)
	{
		checker = blockChecker;
		getter  = blockGetter;
		closer  = blockCloser;

		blockNo = 0;
	}

	@Override
	public boolean hasNextBlock() {
		return checker.getAsBoolean();
	}

	@Override
	public Block getBlock() {
		return current;
	}

	@Override
	public boolean nextBlock() {
		if (checker.getAsBoolean()) {
			current = getter.get();
			blockNo += 1;

			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getBlockCount() {
		return blockNo;
	}

	@Override
	public void close() throws IOException {
		closer.close();
	}
}
