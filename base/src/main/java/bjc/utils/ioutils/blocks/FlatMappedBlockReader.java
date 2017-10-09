package bjc.utils.ioutils.blocks;

import java.io.IOException;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A block reader that supports applying a flatmap operation to blocks.
 *
 * The use-case in mind for this was tokenizing blocks.
 *
 * @author Benjamin Culkin
 */
public class FlatMappedBlockReader implements BlockReader {
	/*
	 * The source reader.
	 */
	private BlockReader reader;

	/*
	 * The current block, and any blocks pending from the last source block.
	 */
	private Iterator<Block> pending;
	private Block           current;

	/*
	 * The operator to open blocks with.
	 */
	private Function<Block, List<Block>> transform;

	/*
	 * The current block number.
	 */
	private int blockNo;

	public FlatMappedBlockReader(BlockReader source, Function<Block, List<Block>> trans) {
		reader    = source;
		transform = trans;

		blockNo = 0;
	}

	@Override
	public boolean hasNextBlock() {
		return pending.hasNext() || reader.hasNextBlock();
	}

	@Override
	public Block getBlock() {
		return current;
	}

	@Override	
	public boolean nextBlock() {
		/*
		 * Attempt to get a new pending list if the one we have isn't
		 * valid.
		 */
		while(pending == null || !pending.hasNext()) {
			if(!reader.hasNext()) return false;

			pending = transform.apply(reader.next()).iterator();
		}

		/*
		 * Advance the iterator.
		 */
		current  = pending.next();
		blockNo += 1;

		return true;
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
