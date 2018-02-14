package bjc.utils.ioutils.blocks;

import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import bjc.utils.funcutils.StringUtils;

/**
 * Simple implementation of {@link BlockReader}.
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
	private final Scanner blockReader;

	/*
	 * The current block.
	 */
	private Block currBlock;

	/*
	 * Info about the current block.
	 */
	private int	blockNo;
	private int	lineNo;

	/**
	 * Create a new block reader.
	 *
	 * @param blockDelim
	 *        The pattern that separates blocks. Note that the end of file
	 *        is always considered to end a block.
	 *
	 * @param source
	 *        The source to read blocks from.
	 */
	public SimpleBlockReader(final String blockDelim, final Reader source) {
		blockReader = new Scanner(source);

		final String pattern = String.format("(?:%s)|\\Z", blockDelim);
		final Pattern pt = Pattern.compile(pattern, Pattern.MULTILINE);

		blockReader.useDelimiter(pt);

		lineNo = 1;
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
			/*
			 * Read in a new block, and keep the line numbers sane.
			 */
			final String blockContents = blockReader.next();

			final int blockStartLine = lineNo;
			final int blockEndLine = lineNo + StringUtils.countMatches(blockContents, "\\R");

			lineNo = blockEndLine;
			blockNo += 1;

			currBlock = new Block(blockNo, blockContents, blockStartLine, blockEndLine);

			return true;
		} catch(final NoSuchElementException nseex) {
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
	}

	/**
	 * Set the delimiter used to separate blocks.
	 *
	 * @param delim
	 *        The delimiter used to separate blocks.
	 */
	public void setDelimiter(final String delim) {
		blockReader.useDelimiter(delim);
	}

	@Override
	public String toString() {
		return String.format("SimpleBlockReader [currBlock=%s, blockNo=%s]", currBlock, blockNo);
	}
}
