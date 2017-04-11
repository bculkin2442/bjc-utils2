package bjc.utils.parserutils.splitter;

import java.util.function.Predicate;

import bjc.utils.funcdata.IList;

/**
 * A token splitter that removes tokens that match a predicate from the stream
 * of tokens.
 * 
 * @author bjculkin
 *
 */
public class FilteredTokenSplitter implements TokenSplitter {
	private TokenSplitter source;

	private Predicate<String> filter;

	/**
	 * Create a new filtered token splitter.
	 * 
	 * @param source
	 *                The splitter to get tokens from.
	 * 
	 * @param filter
	 *                The filter to pass tokens through.
	 */
	public FilteredTokenSplitter(TokenSplitter source, Predicate<String> filter) {
		this.source = source;
		this.filter = filter;
	}

	@Override
	public IList<String> split(String input) {
		return source.split(input).getMatching(filter);
	}
}
