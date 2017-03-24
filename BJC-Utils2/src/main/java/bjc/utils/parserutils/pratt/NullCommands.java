package bjc.utils.parserutils.pratt;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * * Contains factory methods for producing common implementations of
 * {@link NullCommand}
 * 
 * @author EVE
 *
 */
public class NullCommands {
	private static abstract class AbstractNullCommand<K, V, C> extends NullCommand<K, V, C> {
		@Override
		public ITree<Token<K, V>> nullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
				throws ParserException {
			//tokens.next();

			return intNullDenotation(operator, ctx);
		}

		protected abstract ITree<Token<K, V>> intNullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
				throws ParserException;

	}

	private static class UnaryCommand<K, V, C> extends AbstractNullCommand<K, V, C> {
		private final int nullPwer;

		public UnaryCommand(int nullPower) {
			nullPwer = nullPower;
		}

		@Override
		protected ITree<Token<K, V>> intNullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
				throws ParserException {
			ITree<Token<K, V>> opr = ctx.parse.parseExpression(nullPwer, ctx.tokens, ctx.state);

			return new Tree<>(operator, opr);
		}
	}

	private static class GroupingCommand<K, V, C> extends AbstractNullCommand<K, V, C> {
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
			ITree<Token<K, V>> opr = ctx.parse.parseExpression(inner, ctx.tokens, ctx.state);

			ctx.tokens.expect(term);

			return new Tree<>(mark, opr);
		}
	}

	private static class LeafCommand<K, V, C> extends NullCommand<K, V, C> {
		@Override
		public ITree<Token<K, V>> nullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
				throws ParserException {

			return new Tree<>(operator);
		}
	}

	/**
	 * Create a new unary operator.
	 * 
	 * @param precedence
	 *                The precedence of the operator.
	 * 
	 * @return A command implementing that operator.
	 */
	public static <K, V, C> NullCommand<K, V, C> unary(int precedence) {
		return new UnaryCommand<>(precedence);
	}

	/**
	 * Create a new grouping operator.
	 * 
	 * @param precedence
	 *                The precedence of the expression in the operator.
	 * 
	 * @param term
	 *                The type that closes the group.
	 * 
	 * @param mark
	 *                The token for the AST node of the group.
	 * 
	 * @return A command implementing the operator.
	 */
	public static <K, V, C> NullCommand<K, V, C> grouping(int precedence, K term, Token<K, V> mark) {
		return new GroupingCommand<>(precedence, term, mark);
	}

	/**
	 * Create a new leaf operator.
	 * 
	 * @return A command implementing the operator.
	 */
	public static <K, V, C> NullCommand<K, V, C> leaf() {
		return new LeafCommand<>();
	}
}
