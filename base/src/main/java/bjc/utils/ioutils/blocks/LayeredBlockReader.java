package bjc.utils.ioutils.blocks;

import java.io.IOException;

/**
 * A block reader that supports draining all the blocks from one reading before
 * swapping to another.
 *
 * This is more a 'prioritize blocks from one over the other', than a 'read all
 * the blocks from one, then all the blocks from the other'. If you need that,
 * look at {@link SerialBlockReader}.
 *
 * @author bjculkin
 *
 */
public class LayeredBlockReader implements BlockReader {
	/*
	 * The readers to drain from.
	 */
	private final BlockReader first;
	private final BlockReader second;

	/*
	 * The current block number.
	 */
	private int blockNo;

	/**
	 * Create a new layered block reader.
	 *
	 * @param primary
	 *        The first source to read blocks from.
	 *
	 * @param secondary
	 *        The second source to read blocks from.
	 */
	public LayeredBlockReader(final BlockReader primary, final BlockReader secondary) {
		first = primary;
		second = secondary;
	}

	@Override
	public boolean hasNextBlock() {
		return first.hasNextBlock() || second.hasNextBlock();
	}

	@Override
	public Block getBlock() {
		final Block firstBlock = first.getBlock();

		/*
		 * Only drain a block from the second reader if none are
		 * available in the first reader.
		 */
		return firstBlock == null ? second.getBlock() : firstBlock;
	}

	@Override
	public boolean nextBlock() {
		final boolean gotFirst = first.nextBlock();
		final boolean succ = gotFirst ? gotFirst : second.nextBlock();

		if(succ) {
			blockNo += 1;
		}

		return succ;
	}

	@Override
	public int getBlockCount() {
		return blockNo;
	}

	@Override
	public void close() throws IOException {
		second.close();

		first.close();
	}
}
