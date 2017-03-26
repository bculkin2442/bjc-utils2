package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

import java.util.Set;

/**
 * Create a new chained operator.
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
public class ChainCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
	private Set<K> chainWith;

	private Token<K, V> chain;

	/**
	 * Create a new chained operator.
	 * 
	 * @param precedence
	 *                The precedence of this operator.
	 * 
	 * @param chainSet
	 *                The operators to chain with.
	 * 
	 * @param chainMarker
	 *                The token to use as the node in the AST.
	 */
	public ChainCommand(int precedence, Set<K> chainSet, Token<K, V> chainMarker) {
		super(precedence);

		chainWith = chainSet;
		chain = chainMarker;
	}

	@Override
	public ITree<Token<K, V>> denote(ITree<Token<K, V>> operand, Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		ITree<Token<K, V>> tree = ctx.parse.parseExpression(1 + leftBinding(), ctx.tokens, ctx.state, false);

		ITree<Token<K, V>> res = new Tree<>(operator, operand, tree);

		if (chainWith.contains(ctx.tokens.current().getKey())) {
			Token<K, V> tok = ctx.tokens.current();
			ctx.tokens.next();

			ITree<Token<K, V>> other = denote(tree, tok,
					new ParserContext<>(ctx.tokens, ctx.parse, ctx.state));

			return new Tree<>(chain, res, other);
		} else {
			return res;
		}
	}

	@Override
	public int nextBinding() {
		return leftBinding() - 1;
	}
}