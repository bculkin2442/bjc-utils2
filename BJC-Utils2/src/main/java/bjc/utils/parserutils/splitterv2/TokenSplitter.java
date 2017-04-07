package bjc.utils.parserutils.splitterv2;

import bjc.utils.funcdata.IList;

/**
 * Split a string into a list of pieces.
 * 
 * @author EVE
 *
 */
public interface TokenSplitter {
	/**
	 * Split a string into a list of pieces.
	 * 
	 * @param input
	 *                The string to split.
	 * 
	 * @return The pieces of the string.
	 */
	public IList<String> split(String input);
}
