package bjc.utils.parserutils.pratt;

import bjc.utils.data.ITree;
import bjc.utils.parserutils.ParserException;

/**
 * Represents a non-initial command in parsing.
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
 */
public abstract class NonInitialCommand<K, V, C> {
	/**
	 * Construct the left denotation of this command.
	 * 
	 * @param operand
	 *                The left-hand operand of this command.
	 * @param operator
	 *                The operator for this command.
	 * 
	 * @param ctx
	 *                The state needed for commands.
	 * 
	 * @return The tree this command forms.
	 * 
	 * @throws ParserException
	 *                 If something went wrong during parsing.
	 */
	public abstract ITree<Token<K, V>> denote(ITree<Token<K, V>> operand, Token<K, V> operator,
			ParserContext<K, V, C> ctx) throws ParserException;

	/**
	 * Get the left-binding power of this command.
	 * 
	 * This represents the general precedence of this command.
	 * 
	 * @return The left-binding power of this command.
	 */
	public abstract int leftBinding();

	/**
	 * Get the next-binding power of this command.
	 * 
	 * This represents the highest precedence of command this command can be
	 * the left operand of.
	 * 
	 * This is the same as the left-binding power by default.
	 * 
	 * @return The next-binding power of this command.
	 */
	public int nextBinding() {
		return leftBinding();
	}
}
