package bjc.utils.ioutils;

import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Implements reading numbered blocks from a source.
 * 
 * A block is a series of characters, separated by some regular expression.
 * 
 * NOTE: The EOF marker is always treated as a delimiter. You are expected to
 * handle blocks that may be shorter than you expect.
 * 
 * @author EVE
 *
 */
public class BlockReader implements AutoCloseable, Iterator<Block> {
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

		String pattern = String.format("(?:%s)|\\Z", blockDelim);
		Pattern pt = Pattern.compile(pattern, Pattern.MULTILINE);

		blockReader.useDelimiter(pt);
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
	 * @return Whether or not the next block was successfully read.
	 */
	public boolean nextBlock() {
		try {
			int blockStartLine = lnReader.getLineNumber();
			String blockContents = blockReader.next();
			int blockEndLine = lnReader.getLineNumber();
			blockNo += 1;

			currBlock = new Block(blockNo, blockContents, blockStartLine, blockEndLine);

			return true;
		} catch (NoSuchElementException nseex) {
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
		while (hasNextBlock()) {
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

	/**
	 * Set the delimiter used to separate blocks.
	 * 
	 * @param delim
	 *                The delimiter used to separate blocks.
	 */
	public void setDelimiter(String delim) {
		blockReader.useDelimiter(delim);
	}

	/*
	 * Iterator implementation.
	 */

	@Override
	public boolean hasNext() {
		return blockReader.hasNext();
	}

	@Override
	public Block next() {
		nextBlock();

		return getBlock();
	}
}
