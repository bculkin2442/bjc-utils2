package bjc.utils.parserutils.pratt.tokens;

import java.util.Iterator;

import bjc.utils.parserutils.pratt.Token;
import bjc.utils.parserutils.pratt.TokenStream;

/**
 * Simple implementation of token stream for strings.
 * 
 * The terminal token here is represented by a token with type '(end)' and null
 * value.
 * 
 * @author EVE
 *
 */
public class StringTokenStream extends TokenStream<String, String> {
	private Iterator<Token<String, String>> iter;

	private Token<String, String> curr;

	/**
	 * Create a new token stream from a iterator.
	 * 
	 * @param itr
	 *                The iterator to use.
	 * 
	 */
	public StringTokenStream(Iterator<Token<String, String>> itr) {
		iter = itr;

	}

	@Override
	public Token<String, String> current() {
		return curr;
	}

	@Override
	public Token<String, String> next() {
		if (iter.hasNext()) {
			curr = iter.next();
		} else {
			curr = new StringToken("(end)", null);
		}

		return curr;
	}

	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}
}
