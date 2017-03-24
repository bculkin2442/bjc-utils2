package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

import java.util.function.UnaryOperator;

public class DelimitedCommand<K, V, C> extends AbstractInitialCommand<K, V, C> {
	private int inner;

	private K	delim;
	private K	mark;

	private Token<K, V> term;

	private UnaryOperator<C>	onEnter;
	private UnaryOperator<C>	onDelim;
	private UnaryOperator<C>	onExit;

	private boolean statement;

	public DelimitedCommand(int inner, K delim, K mark, Token<K, V> term, UnaryOperator<C> onEnter,
			UnaryOperator<C> onDelim, UnaryOperator<C> onExit, boolean statement) {
		this.inner = inner;
		this.delim = delim;
		this.mark = mark;
		this.term = term;
		this.onEnter = onEnter;
		this.onDelim = onDelim;
		this.onExit = onExit;
		this.statement = statement;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ITree<Token<K, V>> intNullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		C newState = onEnter.apply(ctx.state);

		IList<ITree<Token<K, V>>> kids = new FunctionalList<>();

		while(true) {
			ITree<Token<K, V>> kid = ctx.parse.parseExpression(inner, ctx.tokens, newState,
					statement);
			kids.add(kid);

			Token<K, V> tok = ctx.tokens.current();

			ctx.tokens.expect(delim, mark);

			if(tok.getKey().equals(mark)) break;

			newState = onDelim.apply(newState);
		}

		ctx.state = onExit.apply(newState);

		return new Tree<>(term, kids);
	}
}