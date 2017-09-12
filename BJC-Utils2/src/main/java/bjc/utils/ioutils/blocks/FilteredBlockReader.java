package bjc.utils.ioutils.blocks;

import java.io.IOException;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class FilteredBlockReader implements BlockReader {
	/*
	 * The source of blocks.
	 */
	private BlockReader source;

	/*
	 * The current and next block.
	 *
	 * Both have already been checked for the predicate.
	 */
	private Block current;
	private Block pending;

	/*
	 * Number of blocks that passed the predicate.
	 */
	private int blockNo;

	/*
	 * The predicate blocks must pass.
	 */
	private Predicate<Block> pred;

	/*
	 * The action to call on failure, if there is one.
	 */
	private Consumer<Block> failAction;

	public FilteredBlockReader(BlockReader src, Predicate<Block> predic) {
		this(src, predic, null);
	}

	public FilteredBlockReader(BlockReader src, Predicate<Block> predic, Consumer<Block> failAct) {
		source     = src;
		pred       = predic;
		failAction = failAct;

		blockNo = 0;
	}

	@Override
	public boolean hasNextBlock() {
		if(pending != null) return true;

		while(source.hasNextBlock()) {
			/*
			 * Only say we have a next block if the next block would
			 * pass the predicate.
			 */
			pending = source.next();

			if(pred.test(pending)) {
				blockNo += 1;
				return true;
			} else {
				failAction.accept(pending);
			}
		}
		
		return false;
	}

	@Override
	public Block getBlock() {
		return current;
	}

	@Override
	public boolean nextBlock() {
		if(pending != null || hasNextBlock()) {
			current = pending;
			pending = null;

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
		source.close();
	}
}
