package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.InitialCommand;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

/**
 * Default implementation of an initial command.
 * 
 * @author EVE
 *
 * @param <K>
 *                The key type of the token.
 * 
 * @param <V>
 *                The value type of the token.
 * 
 * @param <C>
 *                The state type of the parser.
 */
public class DefaultInitialCommand<K, V, C> implements InitialCommand<K, V, C> {
	@Override
	public ITree<Token<K, V>> denote(Token<K, V> operator, ParserContext<K, V, C> ctx) throws ParserException {
		throw new ParserException("Unexpected token " + operator);
	}
}
