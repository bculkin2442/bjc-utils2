package bjc.utils.parserutils.splitter;

import bjc.funcdata.*;

/**
 * The token splitter that doesn't actually perform any splitting.
 * 
 * @author Ben Culkin
 *
 */
public class IdentityTokenSplitter implements TokenSplitter {
	@Override
	public ListEx<String> split(String input) {
		return new FunctionalList<>(input);
	}
}
