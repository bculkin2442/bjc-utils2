package bjc.utils.parserutils.splitter;

import java.util.regex.Pattern;

import bjc.utils.funcdata.IList;
import bjc.utils.functypes.ID;
import bjc.utils.ioutils.RegexStringEditor;

/**
 * Splits a string into pieces around a regular expression.
 *
 * @author EVE
 *
 */
public class SimpleTokenSplitter implements TokenSplitter {
	protected Pattern spliter;

	private final boolean keepDelim;

	/**
	 * Create a new simple token splitter.
	 *
	 * @param splitter
	 *        The pattern to split around.
	 *
	 * @param keepDelims
	 *        Whether or not delimiters should be kept.
	 */
	public SimpleTokenSplitter(final Pattern splitter, final boolean keepDelims) {
		spliter = splitter;

		keepDelim = keepDelims;
	}

	@Override
	public IList<String> split(final String input) {
		if(keepDelim)
			return RegexStringEditor.mapOccurances(input, spliter, ID.id(), ID.id());
		else
			return RegexStringEditor.mapOccurances(input, spliter, ID.id(), strang -> "");
	}

	@Override
	public String toString() {
		return String.format("SimpleTokenSplitter [spliter=%s, keepDelim=%s]", spliter, keepDelim);
	}
}
