package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

/**
 * A postfix operator.
 * 
 * @author bjculkin
 *
 * @param <K>
 *                The key type of the tokens.
 * 
 * @param <V>
 *                The value type of the tokens.
 * 
 * @param <C>
 *                The state type of the parser.
 */
public class PostfixCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
	/**
	 * Create a new postfix operator.
	 * 
	 * @param precedence
	 *                The precedence of the operator.
	 */
	public PostfixCommand(int precedence) {
		super(precedence);
	}

	@Override
	public ITree<Token<K, V>> denote(ITree<Token<K, V>> operand, Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		return new Tree<>(operator, operand);
	}
}