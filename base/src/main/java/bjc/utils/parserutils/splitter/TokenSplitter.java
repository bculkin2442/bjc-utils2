package bjc.utils.parserutils.splitter;

import bjc.funcdata.IList;

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
	 *        The string to split.
	 *
	 * @return The pieces of the string.
	 */
	public IList<String> split(String input);
}
