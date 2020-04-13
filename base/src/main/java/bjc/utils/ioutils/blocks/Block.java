package bjc.utils.ioutils.blocks;

/**
 * Represents a block of text read in from a source.
 *
 * @author EVE
 *
 */
public class Block {
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
	 * The line offset number for this block.
	 *
	 * Essentially, this is the absolute number of lines that this block is into
	 * whatever source it came from, where as startLine and endLine are relative to
	 * the BlockReader this Block is from.
	 */
	public int lineOffset;

	/**
	 * Create a new block.
	 *
	 * @param blockNo
	 *                  The number of this block.
	 * @param contents
	 *                  The contents of this block.
	 * @param startLine
	 *                  The line this block started on.
	 * @param endLine
	 *                  The line this block ended.
	 */
	public Block(final int blockNo, final String contents, final int startLine,
			final int endLine) {
		this.contents = contents;
		this.startLine = startLine;
		this.endLine = endLine;
		this.blockNo = blockNo;
	}

	@Override
	public String toString() {
		if (lineOffset != -1) {
			String fmt
					= "Block #%d (from lines %d (%d) to %d (%d)), length: %d characters";

			return String.format(fmt, blockNo, startLine + lineOffset, startLine,
					endLine + lineOffset, endLine + lineOffset, contents.length());
		}

		String fmt = "Block #%d (from lines %d to %d), length: %d characters";

		return String.format(fmt, blockNo, startLine, endLine, contents.length());
	}
}
