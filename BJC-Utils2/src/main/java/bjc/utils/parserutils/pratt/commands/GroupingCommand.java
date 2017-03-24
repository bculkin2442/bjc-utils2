package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

public class GroupingCommand<K, V, C> extends AbstractInitialCommand<K, V, C> {
	private K		term;
	private Token<K, V>	mark;
	private int		inner;

	public GroupingCommand(int innerPrec, K terminator, Token<K, V> marker) {
		inner = innerPrec;
		term = terminator;
		mark = marker;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ITree<Token<K, V>> intNullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		ITree<Token<K, V>> opr = ctx.parse.parseExpression(inner, ctx.tokens, ctx.state, false);

		ctx.tokens.expect(term);

		return new Tree<>(mark, opr);
	}
}