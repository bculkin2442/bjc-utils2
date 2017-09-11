package bjc.utils.ioutils.blocks;

import java.io.IOException;

import bjc.utils.date.BooleanToggle;

public class ToggledBlockReader {
	private BlockReader leftSource;
	private BlockReader rightSource;

	/*
	 * We choose the left source when this is true.
	 */
	private BooleanToggle leftToggle;

	private int blockNo;

	public ToggledBlockReader(BlockReader left, BlockReader right) {
		leftSource  = left;
		rightSource = right;

		blockNo = 0;

		leftToggle = new BooleanToggle();
	}

	@Override
	public boolean hasNextBlock() {
		if(leftToggle.peek()) return left.hasNextBlock();
		else                  return right.hasNextBlock();
	}

	@Override
	public Block getBlock() {
		if(leftToggle.peek()) return left.getBlock();
		else                  return right.getBlock();
	}

	@Override
	public boolean nextBlock() {
		boolean succ;

		if(leftToggle.get()) {
			succ = leftSource.nextBlock();
		} else {
			succ = rightSource.nextBlock();
		}

		if(succ) blockNo += 1;
		return succ;
	}

	@Override
	public int getBlockCount() {
		return blockNo;
	}
}
