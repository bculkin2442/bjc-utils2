package bjc.utils.ioutils.blocks;

import java.io.IOException;

import java.util.function.UnaryOperator;

public class MappedBlockReader implements BlockReader {
	private BlockReader reader;

	private Block current;

	private UnaryOperator<Block> transform;

	public MappedBlockReader(BlockReader source, UnaryOperator<Block> trans) {
		reader    = source;
		transform = trans;
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

			return true;
		}

		return false;
	}

	@Override
	public int getBlockCount() {
		return reader.getBlockCount();
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}
}
