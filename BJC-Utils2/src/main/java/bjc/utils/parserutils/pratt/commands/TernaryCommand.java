package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

public class TernaryCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
	private K term;

	private int innerExp;

	private Token<K, V> mark;

	private boolean nonassoc;

	public TernaryCommand(int leftPower, K terminator, Token<K, V> marker, boolean isNonassoc) {
		super(leftPower);

		term = terminator;
		mark = marker;
		nonassoc = isNonassoc;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ITree<Token<K, V>> denote(ITree<Token<K, V>> operand, Token<K, V> operator,
			ParserContext<K, V, C> ctx) throws ParserException {
		ITree<Token<K, V>> inner = ctx.parse.parseExpression(innerExp, ctx.tokens, ctx.state, false);

		ctx.tokens.expect(term);

		ITree<Token<K, V>> outer = ctx.parse.parseExpression(1 + leftBinding(), ctx.tokens, ctx.state, false);

		return new Tree<>(mark, inner, operand, outer);
	}

	@Override
	public int nextBinding() {
		if (nonassoc) {
			return leftBinding() - 1;
		} else {
			return leftBinding();
		}
	}
}