package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

public class PostCircumfixCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
	private int		insidePrec;
	private K		term;
	private Token<K, V>	mark;

	public PostCircumfixCommand(int leftPower, int insidePower, K terminator, Token<K, V> marker) {
		super(leftPower);

		insidePrec = insidePower;
		term = terminator;
		mark = marker;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ITree<Token<K, V>> denote(ITree<Token<K, V>> operand, Token<K, V> operator,
			ParserContext<K, V, C> ctx) throws ParserException {
		ITree<Token<K, V>> inside = ctx.parse.parseExpression(insidePrec, ctx.tokens, ctx.state, false);

		ctx.tokens.expect(term);

		return new Tree<>(mark, operand, inside);
	}
}