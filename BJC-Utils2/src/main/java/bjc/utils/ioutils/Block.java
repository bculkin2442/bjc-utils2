package bjc.utils.ioutils;

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
		return String.format("Block #%d (from lines %d to %d), length: %d characters", blockNo, startLine,
				endLine, contents.length());
	}
}