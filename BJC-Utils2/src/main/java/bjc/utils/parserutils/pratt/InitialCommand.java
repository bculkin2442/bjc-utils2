package bjc.utils.parserutils.pratt;

import bjc.utils.data.ITree;
import bjc.utils.parserutils.ParserException;

/**
 * Represents an initial command in parsing.
 * 
 * @author EVE
 * 
 * @param <K>
 *                The key type for the tokens.
 * 
 * @param <V>
 *                The value type for the tokens.
 * 
 * @param <C>
 *                The state type of the parser.
 * 
 *
 */
@FunctionalInterface
public interface InitialCommand<K, V, C> {
	/**
	 * Construct the null denotation of this command.
	 * 
	 * @param operator
	 *                The operator for this command.
	 * @param ctx
	 *                The context for the command.
	 * 
	 * @return The tree for this command.
	 * 
	 * @throws ParserException
	 *                 If something goes wrong during parsing.
	 */
	ITree<Token<K, V>> denote(Token<K, V> operator, ParserContext<K, V, C> ctx) throws ParserException;
}
