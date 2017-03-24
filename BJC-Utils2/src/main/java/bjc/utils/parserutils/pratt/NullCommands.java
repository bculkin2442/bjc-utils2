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
			// tokens.next();

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
			ITree<Token<K, V>> opr = ctx.parse.parseExpression(nullPwer, ctx.tokens, ctx.state, false);

			return new Tree<>(operator, opr);
		}
	}

	private static class GroupingCommand<K, V, C> extends AbstractNullCommand<K, V, C> {
		private K term;
		private Token<K, V> mark;
		private int inner;

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

	private static class LeafCommand<K, V, C> extends NullCommand<K, V, C> {
		@Override
		public ITree<Token<K, V>> nullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
				throws ParserException {

			return new Tree<>(operator);
		}
	}

	private static class ConstantCommand<K, V, C> extends NullCommand<K, V, C> {
		private ITree<Token<K, V>> val;

		public ConstantCommand(ITree<Token<K, V>> con) {
			val = con;
		}

		@Override
		public ITree<Token<K, V>> nullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
				throws ParserException {
			return val;
		}
	}

	private static class PreTernaryCommand<K, V, C> extends AbstractNullCommand<K, V, C> {
		private int cond1;
		private int block1;
		private int block2;

		private K mark1;
		private K mark2;

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

	/**
	 * Create a new unary operator.
	 * 
	 * @param precedence
	 *            The precedence of the operator.
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
	 *            The precedence of the expression in the operator.
	 * 
	 * @param term
	 *            The type that closes the group.
	 * 
	 * @param mark
	 *            The token for the AST node of the group.
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

	/**
	 * Create a new pre-ternary operator, like an if-then-else statement.
	 * 
	 * @param cond1
	 *            The priority of the first block.
	 * 
	 * @param block1
	 *            The priority of the second block.
	 * 
	 * @param block2
	 *            The priority of the third block.
	 * 
	 * @param mark1
	 *            The marker that ends the first block.
	 * 
	 * @param mark2
	 *            The marker that ends the second block.
	 * 
	 * @param term
	 *            The token for the AST node of the group.
	 * 
	 * @return A command implementing the operator.
	 */
	public static <K, V, C> NullCommand<K, V, C> preTernary(int cond1, int block1, int block2, K mark1, K mark2,
			Token<K, V> term) {
		return new PreTernaryCommand<>(cond1, block1, block2, mark1, mark2, term);
	}

	/**
	 * Create a new named constant.
	 * 
	 * @param val
	 *            The value of the constant.
	 * 
	 * @return A command implementing the constant.
	 */
	public static <K, V, C> NullCommand<K, V, C> constant(ITree<Token<K, V>> val) {
		return new ConstantCommand<>(val);
	}
}
