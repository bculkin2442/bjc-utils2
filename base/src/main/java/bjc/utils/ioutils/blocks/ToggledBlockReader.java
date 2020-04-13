package bjc.utils.ioutils.blocks;

import java.io.IOException;

import bjc.data.BooleanToggle;

/**
 * A block reader that toggles between two sources.
 *
 * @author EVE
 *
 */
public class ToggledBlockReader implements BlockReader {
	private BlockReader leftSource;
	private BlockReader rightSource;

	/* We choose the left source when this is true. */
	private BooleanToggle leftToggle;

	private int blockNo;

	/**
	 * Create a new toggling block reader.
	 *
	 * @param left
	 *              The first block reader to use.
	 * @param right
	 *              The second block reader to use.
	 */
	public ToggledBlockReader(BlockReader left, BlockReader right) {
		leftSource = left;
		rightSource = right;

		blockNo = 0;

		leftToggle = new BooleanToggle();
	}

	@Override
	public boolean hasNextBlock() {
		if (leftToggle.peek()) {
			return leftSource.hasNextBlock();
		}

		return rightSource.hasNextBlock();
	}

	@Override
	public Block getBlock() {
		if (leftToggle.peek()) {
			return leftSource.getBlock();
		}

		return rightSource.getBlock();
	}

	@Override
	public boolean nextBlock() {
		boolean succ;

		if (leftToggle.get()) {
			succ = leftSource.nextBlock();
		} else {
			succ = rightSource.nextBlock();
		}

		if (succ)
			blockNo += 1;
		return succ;
	}

	@Override
	public int getBlockCount() {
		return blockNo;
	}

	@Override
	public void close() throws IOException {
		leftSource.close();
		rightSource.close();
	}
}
