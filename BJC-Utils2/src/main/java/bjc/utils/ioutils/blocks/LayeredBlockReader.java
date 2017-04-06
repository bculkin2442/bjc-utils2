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
	private BlockReader	first;
	private BlockReader	second;

	private int blockNo;

	/**
	 * Create a new layered block reader.
	 * 
	 * @param primary
	 *                The first source to read blocks from.
	 * 
	 * @param secondary
	 *                The second source to read blocks from.
	 */
	public LayeredBlockReader(BlockReader primary, BlockReader secondary) {
		first = primary;
		second = secondary;
	}

	@Override
	public boolean hasNextBlock() {
		return first.hasNextBlock() || second.hasNextBlock();
	}

	@Override
	public Block getBlock() {
		Block firstBlock = first.getBlock();

		return firstBlock == null ? second.getBlock() : firstBlock;
	}

	@Override
	public boolean nextBlock() {
		boolean gotFirst = first.nextBlock();

		boolean succ = gotFirst ? gotFirst : second.nextBlock();

		if (succ) blockNo += 1;

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