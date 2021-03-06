package bjc.utils.parserutils.splitter;

import java.util.regex.Pattern;

import bjc.funcdata.ListEx;
import bjc.functypes.ID;
import bjc.utils.ioutils.RegexStringEditor;

/**
 * Splits a string into pieces around a regular expression.
 *
 * @author EVE
 *
 */
public class SimpleTokenSplitter implements TokenSplitter {
	/**
	 * The pattern to split on.
	 */
	protected Pattern spliter;

	/**
	 * Whether or not to keep the delimiters.
	 */
	protected final boolean keepDelim;

	/**
	 * Create a new simple token splitter.
	 *
	 * @param splitter
	 *                   The pattern to split around.
	 *
	 * @param keepDelims
	 *                   Whether or not delimiters should be kept.
	 */
	public SimpleTokenSplitter(final Pattern splitter, final boolean keepDelims) {
		spliter = splitter;

		keepDelim = keepDelims;
	}

	@Override
	public ListEx<String> split(final String input) {
		if (keepDelim) {
			return RegexStringEditor.mapOccurances(input, spliter, ID.id(), ID.id());
		}

		return RegexStringEditor.mapOccurances(input, spliter, ID.id(), strang -> "");
	}

	@Override
	public String toString() {
		return String.format("SimpleTokenSplitter [spliter=%s, keepDelim=%s]", spliter,
				keepDelim);
	}
}
