package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

/**
 * Represents a configurable initial command.
 * 
 * @author EVE
 *
 * @param <K>
 *                The token key type.
 * 
 * @param <V>
 *                The token value type.
 * 
 * @param <C>
 *                The parser state type.
 */
public class InitialInterleaveCommand<K, V, C> extends AbstractInitialCommand<K, V, C> {
	@Override
	protected ITree<Token<K, V>> intNullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		return null;
	}

}
