package bjc.utils.ioutils.blocks;

import java.io.*;
import java.util.Scanner;

/**
 * A block reader that uses bash-style multi-line prompts to read blocks.
 * 
 * The format of the blocks returned by this is indented blocks <code> x y z a b
 * 
 * c -- block split here d <code>
 * 
 * @author bjcul
 *
 */
public class MLPromptBlockReader implements BlockReader {
	private String firstPrompt;
	private String secondPrompt;

	private Scanner input;
	private Writer output;

	private Block currBlock;
	
	/* Info about the current block. */
	private int blockNo = 0;
	private int lineNo  = 1;
	
	private String nextInitLine;
	/**
	 * Create a new multi-line prompt block reader.
	 * 
	 * @param firstPrompt  The initial prompt. Is treated as a format string, with
	 *                     %1 being the block number and %2 being the line number
	 * @param secondPrompt The secondary prompt. Is also treated as a format string
	 *                     with %1 and %2 as firstPrompt, and %3 as the continuation line
	 *                     number
	 * 
	 * @param input        The input source
	 * @param output       The output destination
	 */
	public MLPromptBlockReader(String firstPrompt, String secondPrompt, Reader input, Writer output) {
		this.firstPrompt = firstPrompt;
		this.secondPrompt = secondPrompt;
		this.input = new Scanner(input);
		this.output = output;
	}

	@Override
	public boolean hasNextBlock() {
		return input.hasNextLine();
	}

	@Override
	public Block getBlock() {
		return currBlock;
	}

	@Override
	public boolean nextBlock() {
		StringBuilder blockContents = new StringBuilder();
		
		String currLn;
		if (nextInitLine == null) {
			try {
				output.write(String.format(firstPrompt, blockNo, lineNo));
			} catch (IOException ioex) {
				throw new RuntimeException(ioex);
			}
			currLn = input.nextLine();
		} else {
			currLn = nextInitLine;
		}
		
		blockContents.append(currLn);
		
		int startLn = lineNo;
		int endLn = lineNo++;
		
		boolean inBlock = true;
		while(input.hasNextLine() && inBlock) {
			currLn = input.nextLine();
			
			int initCh = currLn.codePointAt(0);
			inBlock = Character.isSpaceChar(initCh);
		}
		
		currBlock = new Block(blockNo++, blockContents.toString(), startLn, endLn);
		return true;
	}

	@Override
	public int getBlockCount() {
		return blockNo;
	}

	@Override
	public void close() throws IOException {
		input.close();
	}

}
