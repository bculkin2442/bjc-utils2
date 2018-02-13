package bjc.utils.ioutils.blocks;

import java.io.IOException;

import java.util.function.UnaryOperator;

/**
 * A block reader that applies a transform to each block.
 * 
 * @author EVE
 *
 */
public class MappedBlockReader implements BlockReader {
	private BlockReader reader;

	private Block current;

	private UnaryOperator<Block> transform;

	private int blockNo;

	/**
	 * Create a new mapped block reader.
	 * 
	 * @param source
	 *        The source for blocks
	 * @param trans
	 *        The transform to apply.
	 */
	public MappedBlockReader(BlockReader source, UnaryOperator<Block> trans) {
		reader = source;
		transform = trans;

		blockNo = 0;
	}

	@Override
	public boolean hasNextBlock() {
		return reader.hasNextBlock();
	}

	@Override
	public Block getBlock() {
		return current;
	}

	@Override
	public boolean nextBlock() {
		if(hasNextBlock()) {
			current = transform.apply(reader.next());
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
		reader.close();
	}
}
