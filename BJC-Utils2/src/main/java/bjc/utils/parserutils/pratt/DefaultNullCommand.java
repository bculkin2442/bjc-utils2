package bjc.utils.parserutils.pratt;

import bjc.utils.data.ITree;
import bjc.utils.parserutils.ParserException;

/**
 * Default implementation of null command.
 * 
 * @author EVE
 *
 * @param <K>
 *                The key type of the token.
 * @param <V>
 *                The value type of the token.
 * 
 * @param <C>
 *                The state type of the parser.
 */
public class DefaultNullCommand<K, V, C> extends NullCommand<K, V, C> {
	@Override
	public ITree<Token<K, V>> nullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		throw new ParserException("Unexpected token " + operator);
	}
}
