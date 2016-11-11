package bjc.utils.parserutils;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Predicate;

import bjc.utils.data.IHolder;
import bjc.utils.data.IPair;
import bjc.utils.data.ITree;
import bjc.utils.data.Identity;
import bjc.utils.data.Pair;
import bjc.utils.funcdata.IList;

/**
 * Creates a parse tree from a postfix expression
 * 
 * @author ben
 *
 */
public class TreeConstructor {
	/**
	 * Construct a tree from a list of tokens in postfix notation
	 * 
	 * Only binary operators are accepted.
	 * 
	 * @param <TokenType>
	 *            The elements of the parse tree
	 * @param tokens
	 *            The list of tokens to build a tree from
	 * @param operatorPredicate
	 *            The predicate to use to determine if something is a
	 *            operator
	 * @return A AST from the expression
	 */
	public static <TokenType> ITree<TokenType> constructTree(
			IList<TokenType> tokens,
			Predicate<TokenType> operatorPredicate) {
		// Construct a tree with no special operators
		return constructTree(tokens, operatorPredicate, (op) -> false,
				null);
	}

	/**
	 * Construct a tree from a list of tokens in postfix notation
	 * 
	 * Only binary operators are accepted by default. Use the last two
	 * parameters to handle non-binary operators
	 * 
	 * @param <TokenType>
	 *            The elements of the parse tree
	 * @param tokens
	 *            The list of tokens to build a tree from
	 * @param operatorPredicate
	 *            The predicate to use to determine if something is a
	 *            operator
	 * @param isSpecialOperator
	 *            The predicate to use to determine if an operator needs
	 *            special handling
	 * @param handleSpecialOperator
	 *            The function to use to handle special case operators
	 * @return A AST from the expression
	 * 
	 *         FIXME The handleSpecialOp function seems like an ugly
	 *         interface. Maybe there's a better way to express how that
	 *         works
	 */
	public static <TokenType> ITree<TokenType> constructTree(
			IList<TokenType> tokens,
			Predicate<TokenType> operatorPredicate,
			Predicate<TokenType> isSpecialOperator,
			Function<TokenType, Function<Deque<ITree<TokenType>>,
					ITree<TokenType>>> handleSpecialOperator) {
		// Make sure our parameters are valid
		if (tokens == null) {
			throw new NullPointerException("Tokens must not be null");
		} else if (operatorPredicate == null) {
			throw new NullPointerException(
					"Operator predicate must not be null");
		} else if (isSpecialOperator == null) {
			throw new NullPointerException(
					"Special operator determiner must not be null");
		}

		// Here is the state for the tree construction
		IHolder<IPair<Deque<ITree<TokenType>>,
				ITree<TokenType>>> initialState = new Identity<>(
						new Pair<>(new LinkedList<>(), null));

		// Transform each of the tokens
		tokens.forEach(
				new TokenTransformer<>(initialState, operatorPredicate,
						isSpecialOperator, handleSpecialOperator));

		// Grab the tree from the state
		return initialState.unwrap((pair) -> {
			return pair.getRight();
		});
	}
}
