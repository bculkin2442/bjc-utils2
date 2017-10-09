package bjc.utils.ioutils.blocks;

import java.io.IOException;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class BoundBlockReader implements BlockReader {
	@FunctionalInterface
	public interface Closer {
		public void close() throws IOException;
	}

	private BooleanSupplier checker;
	private Supplier<Block> getter;
	private Closer          closer;

	private Block current;

	private int blockNo;

	public BoundBlockReader(BooleanSupplier blockChecker, Supplier<Block> blockGetter, Closer blockCloser) {
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
		if(checker.getAsBoolean()) {
			current  = getter.get();
			blockNo += 1;

			return true;
		} 

		return false;
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
