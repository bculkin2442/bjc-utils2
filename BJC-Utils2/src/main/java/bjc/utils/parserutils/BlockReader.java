package bjc.utils.parserutils;

import java.io.LineNumberReader;
import java.io.Reader;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Implements reading numbered blocks from a source.
 * 
 * A block is a series of characters, seperated by some regular expression.
 * 
 * NOTE: The EOF marker is always treated as a delimiter. You are expected to
 * handle blocks that may be shorter than you expect.
 * 
 * @author EVE
 *
 */
public class BlockReader implements AutoCloseable {
	/**
	 * Represents a block of text read in from a source.
	 * 
	 * @author EVE
	 *
	 */
	public static class Block {
		/**
		 * The contents of this block.
		 */
		public final String contents;

		/**
		 * The line of the source this block started on.
		 */
		public final int startLine;

		/**
		 * The line of the source this block ended on.
		 */
		public final int endLine;

		/**
		 * The number of this block.
		 */
		public final int blockNo;

		/**
		 * Create a new block.
		 * 
		 * @param blockNo
		 *                The number of this block.
		 * @param contents
		 *                The contents of this block.
		 * @param startLine
		 *                The line this block started on.
		 * @param endLine
		 *                The line this block ended.
		 */
		public Block(int blockNo, String contents, int startLine, int endLine) {
			this.contents = contents;
			this.startLine = startLine;
			this.endLine = endLine;
			this.blockNo = blockNo;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;

			result = prime * result + blockNo;
			result = prime * result + ((contents == null) ? 0 : contents.hashCode());
			result = prime * result + endLine;
			result = prime * result + startLine;

			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if(this == obj) return true;
			if(obj == null) return false;
			if(!(obj instanceof Block)) return false;

			Block other = (Block) obj;

			if(blockNo != other.blockNo) return false;

			if(contents == null) {
				if(other.contents != null) return false;
			} else if(!contents.equals(other.contents)) return false;

			if(endLine != other.endLine) return false;
			if(startLine != other.startLine) return false;

			return true;
		}

		@Override
		public String toString() {
			return String.format("Block #%d (from lines %d to %d) length: %d characters", blockNo, endLine,
					startLine, contents.length());
		}
	}

	/*
	 * I/O source for blocks.
	 */
	private LineNumberReader	lnReader;
	private Scanner			blockReader;

	/*
	 * The current block.
	 */
	private Block	currBlock;
	private int	blockNo;

	/**
	 * Create a new block reader.
	 * 
	 * @param blockDelim
	 *                The pattern that separates blocks. Note that the end
	 *                of file is always considered to end a block.
	 * 
	 * @param source
	 *                The source to read blocks from.
	 */
	public BlockReader(String blockDelim, Reader source) {
		lnReader = new LineNumberReader(source);

		blockReader = new Scanner(lnReader);
		blockReader.useDelimiter(String.format("(?:%s)|\\Z", blockDelim));
	}

	/**
	 * Check if this reader has an available block.
	 * 
	 * @return Whether or not another block is available.
	 */
	public boolean hasNextBlock() {
		return blockReader.hasNext();
	}

	/**
	 * Get the current block.
	 * 
	 * @return The current block, or null if there is no current block.
	 */
	public Block getBlock() {
		return currBlock;
	}

	/**
	 * Move to the next block.
	 * 
	 * @return Whether or not the next block was succesfully read.
	 */
	public boolean nextBlock() {
		try {
			int blockStartLine = lnReader.getLineNumber();
			String blockContents = blockReader.next();
			int blockEndLine = lnReader.getLineNumber();
			blockNo += 1;

			currBlock = new Block(blockNo, blockContents, blockStartLine, blockEndLine);

			return true;
		} catch(NoSuchElementException nseex) {
			return false;
		}
	}

	/**
	 * Execute an action for each remaining block.
	 * 
	 * @param action
	 *                The action to execute for each block
	 */
	public void forEachBlock(Consumer<Block> action) {
		while(hasNextBlock()) {
			nextBlock();

			action.accept(currBlock);
		}
	}

	/**
	 * Retrieve the number of blocks that have been read so far.
	 * 
	 * @return The number of blocks read so far.
	 */
	public int getBlockCount() {
		return blockNo;
	}

	@Override
	public void close() throws Exception {
		blockReader.close();

		lnReader.close();
	}
}
