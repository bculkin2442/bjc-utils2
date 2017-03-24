package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

public class PreTernaryCommand<K, V, C> extends AbstractInitialCommand<K, V, C> {
	private int	cond1;
	private int	block1;
	private int	block2;

	private K	mark1;
	private K	mark2;

	private Token<K, V> term;

	public PreTernaryCommand(int cond1, int block1, int block2, K mark1, K mark2, Token<K, V> term) {
		super();
		this.cond1 = cond1;
		this.block1 = block1;
		this.block2 = block2;
		this.mark1 = mark1;
		this.mark2 = mark2;
		this.term = term;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ITree<Token<K, V>> intNullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		ITree<Token<K, V>> cond = ctx.parse.parseExpression(cond1, ctx.tokens, ctx.state, false);

		ctx.tokens.expect(mark1);

		ITree<Token<K, V>> fstBlock = ctx.parse.parseExpression(block1, ctx.tokens, ctx.state, false);

		ctx.tokens.expect(mark2);

		ITree<Token<K, V>> sndBlock = ctx.parse.parseExpression(block2, ctx.tokens, ctx.state, false);

		return new Tree<>(term, cond, fstBlock, sndBlock);
	}
}