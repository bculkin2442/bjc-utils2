package bjc.utils.ioutils.blocks;

import java.io.IOException;

/**
 * A block reader that fires an action before a block is actually read.
 *
 * @author bjculkin
 *
 */
public class TriggeredBlockReader implements BlockReader {
	private final BlockReader source;

	private int blockNo;

	/*
	 * The action to fire.
	 */
	private final Runnable action;

	/**
	 * Create a new triggered reader with the specified source/action.
	 *
	 * @param source
	 *                The block reader to read blocks from.
	 *
	 * @param action
	 *                The action to execute before reading a block.
	 */
	public TriggeredBlockReader(final BlockReader source, final Runnable action) {
		this.source = source;
		this.action = action;

		blockNo = 0;
	}

	@Override
	public boolean hasNextBlock() {
		action.run();

		return source.hasNextBlock();
	}

	@Override
	public Block getBlock() {
		return source.getBlock();
	}

	@Override
	public boolean nextBlock() {
		blockNo += 1;

		return source.nextBlock();
	}

	@Override
	public int getBlockCount() {
		return blockNo;
	}

	@Override
	public void close() throws IOException {
		source.close();
	}

	@Override
	public String toString() {
		return String.format("TriggeredBlockReader [source=%s]", source);
	}
}
