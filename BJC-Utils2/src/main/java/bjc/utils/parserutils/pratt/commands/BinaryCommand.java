package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

public abstract class BinaryCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
	public BinaryCommand(int leftPower) {
		super(leftPower);
	}

	protected abstract int rightBinding();

	@Override
	public ITree<Token<K, V>> denote(ITree<Token<K, V>> operand, Token<K, V> operator,
			ParserContext<K, V, C> ctx) throws ParserException {
		ITree<Token<K, V>> opr = ctx.parse.parseExpression(rightBinding(), ctx.tokens, ctx.state, false);

		return new Tree<>(operator, operand, opr);
	}
}