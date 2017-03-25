package bjc.utils.parserutils.pratt.commands;

import bjc.utils.parserutils.pratt.NonInitialCommand;
import bjc.utils.parserutils.pratt.ParseBlock;
import bjc.utils.parserutils.pratt.Token;
import bjc.utils.parserutils.pratt.blocks.SimpleParseBlock;

import java.util.Set;

/**
 * Contains factory methods for producing common implementations of
 * {@link NonInitialCommand}
 * 
 * @author EVE
 *
 */
public class NonInitialCommands {
	/**
	 * Create a left-associative infix operator.
	 * 
	 * @param precedence
	 *                The precedence of the operator.
	 * 
	 * @return A command implementing that operator.
	 */
	public static <K, V, C> NonInitialCommand<K, V, C> infixLeft(int precedence) {
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
	public static <K, V, C> NonInitialCommand<K, V, C> infixRight(int precedence) {
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
	public static <K, V, C> NonInitialCommand<K, V, C> infixNon(int precedence) {
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
	public static <K, V, C> NonInitialCommand<K, V, C> chain(int precedence, Set<K> chainSet, Token<K, V> marker) {
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
	public static <K, V, C> NonInitialCommand<K, V, C> postfix(int precedence) {
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
	public static <K, V, C> NonInitialCommand<K, V, C> postCircumfix(int precedence, int insidePrecedence, K closer,
			Token<K, V> marker) {
		ParseBlock<K, V, C> innerBlock = new SimpleParseBlock<>(insidePrecedence, closer, null);

		return new PostCircumfixCommand<>(precedence, innerBlock, marker);
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
	public static <K, V, C> NonInitialCommand<K, V, C> ternary(int precedence, int insidePrecedence, K closer,
			Token<K, V> marker, boolean nonassoc) {
		ParseBlock<K, V, C> innerBlock = new SimpleParseBlock<>(insidePrecedence, closer, null);

		return new TernaryCommand<>(precedence, innerBlock, marker, nonassoc);
	}
}
