package bjc.utils.parserutils.pratt;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;

import java.util.Set;

/**
 * Contains factory methods for producing common implementations of
 * {@link LeftCommand}
 * 
 * @author EVE
 *
 */
public class LeftCommands {
	/*
	 * A command with constant binding power.
	 */
	private static abstract class BinaryPostCommand<K, V, C> extends LeftCommand<K, V, C> {
		private final int leftPower;

		public BinaryPostCommand(int power) {
			leftPower = power;
		}

		@Override
		public int leftBinding() {
			return leftPower;
		}
	}

	private static abstract class BinaryCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
		public BinaryCommand(int leftPower) {
			super(leftPower);
		}

		protected abstract int rightBinding();

		@Override
		public ITree<Token<K, V>> leftDenote(ITree<Token<K, V>> operand, Token<K, V> operator,
				ParserContext<K, V, C> ctx) throws ParserException {
			ITree<Token<K, V>> opr = ctx.parse.parseExpression(rightBinding(), ctx.tokens, ctx.state, false);

			return new Tree<>(operator, operand, opr);
		}
	}

	private static class LeftBinaryCommand<K, V, C> extends BinaryCommand<K, V, C> {
		public LeftBinaryCommand(int leftPower) {
			super(leftPower);
		}

		@Override
		protected int rightBinding() {
			return 1 + leftBinding();
		}
	}

	private static class RightBinaryCommand<K, V, C> extends BinaryCommand<K, V, C> {
		public RightBinaryCommand(int leftPower) {
			super(leftPower);
		}

		@Override
		protected int rightBinding() {
			return leftBinding();
		}
	}

	private static class NonBinaryCommand<K, V, C> extends BinaryCommand<K, V, C> {
		public NonBinaryCommand(int leftPower) {
			super(leftPower);
		}

		@Override
		protected int rightBinding() {
			return 1 + leftBinding();
		}

		@Override
		public int nextBinding() {
			return leftBinding() - 1;
		}
	}

	private static class PostCircumfixCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
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
		public ITree<Token<K, V>> leftDenote(ITree<Token<K, V>> operand, Token<K, V> operator,
				ParserContext<K, V, C> ctx) throws ParserException {
			ITree<Token<K, V>> inside = ctx.parse.parseExpression(insidePrec, ctx.tokens, ctx.state, false);

			ctx.tokens.expect(term);

			return new Tree<>(mark, operand, inside);
		}
	}

	private static class PostfixCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
		public PostfixCommand(int leftPower) {
			super(leftPower);
		}

		@Override
		public ITree<Token<K, V>> leftDenote(ITree<Token<K, V>> operand, Token<K, V> operator,
				ParserContext<K, V, C> ctx) throws ParserException {
			return new Tree<>(operator, operand);
		}
	}

	private static class TernaryCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
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
		public ITree<Token<K, V>> leftDenote(ITree<Token<K, V>> operand, Token<K, V> operator,
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

	private static class ChainCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
		private Set<K> chainWith;

		private Token<K, V> chain;

		public ChainCommand(int leftPower, Set<K> chainSet, Token<K, V> chainMarker) {
			super(leftPower);

			chainWith = chainSet;
			chain = chainMarker;
		}

		@Override
		public ITree<Token<K, V>> leftDenote(ITree<Token<K, V>> operand, Token<K, V> operator,
				ParserContext<K, V, C> ctx) throws ParserException {
			ITree<Token<K, V>> tree = ctx.parse.parseExpression(1 + leftBinding(), ctx.tokens, ctx.state, false);

			ITree<Token<K, V>> res = new Tree<>(operator, operand, tree);

			if (chainWith.contains(ctx.tokens.current().getKey())) {
				Token<K, V> tok = ctx.tokens.current();
				ctx.tokens.next();

				ITree<Token<K, V>> other = leftDenote(tree, tok,
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

	/**
	 * Create a left-associative infix operator.
	 * 
	 * @param precedence
	 *                The precedence of the operator.
	 * 
	 * @return A command implementing that operator.
	 */
	public static <K, V, C> LeftCommand<K, V, C> infixLeft(int precedence) {
		return new LeftBinaryCommand<>(precedence);
	}

	/**
	 * Create a right-associative infix operator.
	 * 
	 * @param precedence
	 *                The precedence of the operator.
	 * 
	 * @return A command implementing that operator.
	 */
	public static <K, V, C> LeftCommand<K, V, C> infixRight(int precedence) {
		return new RightBinaryCommand<>(precedence);
	}

	/**
	 * Create a non-associative infix operator.
	 * 
	 * @param precedence
	 *                The precedence of the operator.
	 * 
	 * @return A command implementing that operator.
	 */
	public static <K, V, C> LeftCommand<K, V, C> infixNon(int precedence) {
		return new NonBinaryCommand<>(precedence);
	}

	/**
	 * Create a chained operator.
	 * 
	 * @param precedence
	 *                The precedence of the operator.
	 * 
	 * @param chainSet
	 *                The operators it forms a chain with.
	 * 
	 * @param marker
	 *                The token to use as the AST node for the chained
	 *                operators.
	 * 
	 * @return A command implementing that operator.
	 */
	public static <K, V, C> LeftCommand<K, V, C> chain(int precedence, Set<K> chainSet, Token<K, V> marker) {
		return new ChainCommand<>(precedence, chainSet, marker);
	}

	/**
	 * Create a postfix operator.
	 * 
	 * @param precedence
	 *                The precedence of the operator.
	 * 
	 * @return A command implementing that operator.
	 */
	public static <K, V, C> LeftCommand<K, V, C> postfix(int precedence) {
		return new PostfixCommand<>(precedence);
	}

	/**
	 * Create a post-circumfix operator.
	 * 
	 * This is an operator in form similar to array indexing.
	 * 
	 * @param precedence
	 *                The precedence of this operator
	 * 
	 * @param insidePrecedence
	 *                The precedence of the expression inside the operator
	 * 
	 * @param closer
	 *                The token that closes the circumfix.
	 * 
	 * @param marker
	 *                The token to use as the AST node for the operator.
	 * 
	 * @return A command implementing that operator.
	 */
	public static <K, V, C> LeftCommand<K, V, C> postCircumfix(int precedence, int insidePrecedence, K closer,
			Token<K, V> marker) {
		return new PostCircumfixCommand<>(precedence, insidePrecedence, closer, marker);
	}

	/**
	 * Create a ternary operator.
	 * 
	 * This is like C's ?: operator.
	 * 
	 * @param precedence
	 *                The precedence of the operator.
	 * 
	 * @param insidePrecedence
	 *                The precedence of the inner section of the operator.
	 * 
	 * @param closer
	 *                The token that marks the end of the inner section.
	 * 
	 * @param marker
	 *                The token to use as the AST node for the operator.
	 * 
	 * @param nonassoc
	 *                True if the command is non-associative, false
	 *                otherwise.
	 * 
	 * @return A command implementing this operator.
	 */
	public static <K, V, C> LeftCommand<K, V, C> ternary(int precedence, int insidePrecedence, K closer,
			Token<K, V> marker, boolean nonassoc) {
		return new TernaryCommand<>(insidePrecedence, closer, marker, nonassoc);
	}
}
