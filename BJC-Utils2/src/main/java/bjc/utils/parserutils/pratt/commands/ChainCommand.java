package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

import java.util.Set;

public class ChainCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
	private Set<K> chainWith;

	private Token<K, V> chain;

	public ChainCommand(int leftPower, Set<K> chainSet, Token<K, V> chainMarker) {
		super(leftPower);

		chainWith = chainSet;
		chain = chainMarker;
	}

	@Override
	public ITree<Token<K, V>> denote(ITree<Token<K, V>> operand, Token<K, V> operator,
			ParserContext<K, V, C> ctx) throws ParserException {
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