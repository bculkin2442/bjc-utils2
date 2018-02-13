package bjc.utils.ioutils.blocks;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

/**
 * A block reader that supports pushing blocks onto the input queue so that they
 * are provided before blocks read from an input source.
 *
 * @author bjculkin
 *
 */
public class PushbackBlockReader implements BlockReader {
	private final BlockReader source;

	/*
	 * The queue of pushed-back blocks.
	 */
	private final Deque<Block> waiting;

	private Block curBlock;

	private int blockNo;

	/**
	 * Create a new pushback block reader.
	 *
	 * @param src
	 *        The block reader to use when no blocks are queued.
	 */
	public PushbackBlockReader(final BlockReader src) {
		source = src;

		waiting = new LinkedList<>();
	}

	@Override
	public boolean hasNextBlock() {
		return !waiting.isEmpty() || source.hasNextBlock();
	}

	@Override
	public Block getBlock() {
		return curBlock;
	}

	@Override
	public boolean nextBlock() {
		/*
		 * Drain pushed-back blocks first.
		 */
		if(!waiting.isEmpty()) {
			curBlock = waiting.pop();

			blockNo += 1;

			return true;
		}

		final boolean succ = source.nextBlock();
		curBlock = source.getBlock();

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
		source.close();
	}

	/**
	 * Insert a block at the back of the queue of pending blocks.
	 *
	 * @param blk
	 *        The block to put at the back.
	 */
	public void addBlock(final Block blk) {
		waiting.add(blk);
	}

	/**
	 * Insert a block at the front of the queue of pending blocks.
	 *
	 * @param blk
	 *        The block to put at the front.
	 */
	public void pushBlock(final Block blk) {
		waiting.push(blk);
	}

	@Override
	public String toString() {
		return String.format("PushbackBlockReader [waiting=%s, curBlock=%s, blockNo=%s]", waiting, curBlock,
				blockNo);
	}
}
