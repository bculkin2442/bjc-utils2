package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

public class UnaryCommand<K, V, C> extends AbstractInitialCommand<K, V, C> {
	private final int nullPwer;

	public UnaryCommand(int nullPower) {
		nullPwer = nullPower;
	}

	@Override
	protected ITree<Token<K, V>> intNullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		ITree<Token<K, V>> opr = ctx.parse.parseExpression(nullPwer, ctx.tokens, ctx.state, false);

		return new Tree<>(operator, opr);
	}
}