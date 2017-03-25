package bjc.utils.parserutils.pratt;

import bjc.utils.data.ITree;
import bjc.utils.parserutils.ParserException;

/**
 * Represents a embedded block in an expression.
 * 
 * @author bjculkin
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
@FunctionalInterface
public interface ParseBlock<K, V, C> {

	/**
	 * Parse the block this represents.
	 * 
	 * @param ctx
	 *                The context for parsing.
	 * 
	 * @return A AST for this block.
	 * 
	 * @throws ParserException
	 *                 If something goes wrong during parsing, or the block
	 *                 fails validation.
	 */
	ITree<Token<K, V>> parse(ParserContext<K, V, C> ctx) throws ParserException;

}