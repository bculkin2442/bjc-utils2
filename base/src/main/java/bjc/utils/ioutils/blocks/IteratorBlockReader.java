package bjc.utils.ioutils.blocks;

import java.io.IOException;
import java.util.Iterator;

/**
 * BlockReader which produces blocks from an iterator
 * 
 * @author bjcul
 */
public final class IteratorBlockReader implements BlockReader {
	private final Iterator<Block> iter;
	private Block currBlock;
	private int blockCount = 0;

	/**
	 * Create a new block reader from an iterator.
	 * 
	 * @param iter The iterator to get blocks from.
	 */
	public IteratorBlockReader(Iterator<Block> iter) {
		this.iter = iter;
	}

	@Override
	public boolean nextBlock() {
		if (!iter.hasNext()) return false;
		currBlock = iter.next();
		blockCount++;
		return true;
	}

	@Override
	public boolean hasNextBlock() {
		return iter.hasNext();
	}

	@Override
	public int getBlockCount() {
		return blockCount;
	}

	@Override
	public Block getBlock() {
		return currBlock;
	}

	@Override
	public void close() throws IOException {
		// Nothing for us to really do
	}
}