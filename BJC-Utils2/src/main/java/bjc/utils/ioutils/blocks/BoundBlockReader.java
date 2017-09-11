package bjc.utils.ioutils.blocks;

import java.io.IOException;

import java.util.function.Supplier;

public class BoundBlockReader implements BlockReader {
	private BooleanSupplier checker;
	private Supplier<Block> getter;

	private Block current;

	private int blockNo;

	public BoundBlockReader(BooleanSupplier blockChecker, Supplier<Block> blockGetter) {
		checker = blockChecker;
		getter  = blockGetter;

		blockNo += 1;
	}

	@Override
	public boolean hasNextBlock() {
		return blockChecker.getAsBoolean();
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
}
