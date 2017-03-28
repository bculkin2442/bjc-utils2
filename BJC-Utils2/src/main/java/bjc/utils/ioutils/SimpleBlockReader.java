package bjc.utils.ioutils;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Simple implementation of {@link BlockReader}
 * 
 * NOTE: The EOF marker is always treated as a delimiter. You are expected to
 * handle blocks that may be shorter than you expect.
 * 
 * @author EVE
 *
 */
public class SimpleBlockReader implements BlockReader {
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
	public SimpleBlockReader(String blockDelim, Reader source) {
		lnReader = new LineNumberReader(source);

		blockReader = new Scanner(lnReader);

		String pattern = String.format("(?:%s)|\\Z", blockDelim);
		Pattern pt = Pattern.compile(pattern, Pattern.MULTILINE);

		blockReader.useDelimiter(pt);
	}

	@Override
	public boolean hasNextBlock() {
		return blockReader.hasNext();
	}

	@Override
	public Block getBlock() {
		return currBlock;
	}

	@Override
	public boolean nextBlock() {
		try {
			int blockStartLine = lnReader.getLineNumber();
			String blockContents = blockReader.next();
			int blockEndLine = lnReader.getLineNumber();
			blockNo += 1;

			currBlock = new Block(blockNo, blockContents, blockStartLine, blockEndLine);

			return true;
		} catch (NoSuchElementException nseex) {
			currBlock = null;

			return false;
		}
	}

	@Override
	public int getBlockCount() {
		return blockNo;
	}

	@Override
	public void close() throws IOException {
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

	@Override
	public String toString() {
		return String.format("SimpleBlockReader [currBlock=%s, blockNo=%s]", currBlock, blockNo);
	}
}
