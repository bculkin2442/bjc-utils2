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
 * Creates a parse tree from a postfix expression.
 *
 * @author ben
 *
 */
public class TreeConstructor {
	/**
	 * Alias interface for special operator types.
	 *
	 * @param <TokenType>
	 * 	The token type of the tree.
	 */
	public interface QueueFlattener<TokenType> extends Function<Deque<ITree<TokenType>>, ITree<TokenType>> {
		/*
		 * Alias
		 */
	}

	/* Alias for constructor state. */
	static final class ConstructorState<TokenType> extends Pair<Deque<ITree<TokenType>>, ITree<TokenType>> {
		public ConstructorState(final Deque<ITree<TokenType>> left, final ITree<TokenType> right) {
			super(left, right);
		}

		public ConstructorState(final IPair<Deque<ITree<TokenType>>, ITree<TokenType>> par) {
			super(par.getLeft(), par.getRight());
		}
	}

	/**
	 * Construct a tree from a list of tokens in postfix notation.
	 *
	 * Only binary operators are accepted.
	 *
	 * @param <TokenType>
	 * 	The elements of the parse tree.
	 *
	 * @param tokens
	 * 	The list of tokens to build a tree from.
	 *
	 * @param isOperator
	 * 	The predicate to use to determine if something is a
	 * 	operator.
	 *
	 * @return A AST from the expression.
	 */
	public static <TokenType> ITree<TokenType> constructTree(final IList<TokenType> tokens,
			final Predicate<TokenType> isOperator) {
		/* Construct a tree with no special operators */
		return constructTree(tokens, isOperator, op -> false, null);
	}

	/**
	 * Construct a tree from a list of tokens in postfix notation.
	 *
	 * Only binary operators are accepted by default. Use the last two
	 * parameters to handle non-binary operators.
	 *
	 * @param <TokenType>
	 *        The elements of the parse tree.
	 *
	 * @param tokens
	 *        The list of tokens to build a tree from.
	 *
	 * @param isOperator
	 *        The predicate to use to determine if something is a operator.
	 *
	 * @param isSpecialOperator
	 *        The predicate to use to determine if an operator needs special
	 *        handling.
	 *
	 * @param handleSpecialOperator
	 *        The function to use to handle special case operators.
	 *
	 * @return A AST from the expression.
	 *
	 */
	public static <TokenType> ITree<TokenType> constructTree(final IList<TokenType> tokens,
			final Predicate<TokenType> isOperator, final Predicate<TokenType> isSpecialOperator,
			final Function<TokenType, QueueFlattener<TokenType>> handleSpecialOperator) {
		/*
		 * Make sure our parameters are valid
		 */
		if(tokens == null)
			throw new NullPointerException("Tokens must not be null");
		else if(isOperator == null)
			throw new NullPointerException("Operator predicate must not be null");
		else if(isSpecialOperator == null)
			throw new NullPointerException("Special operator determiner must not be null");

		final ConstructorState<TokenType> cstate = new ConstructorState<>(
				new LinkedList<>(), null);

		/* Here is the state for the tree construction */
		final IHolder<ConstructorState<TokenType>> initialState = new Identity<>(cstate);

		/* Transform each of the tokens */
		final TokenTransformer trans = new TokenTransformer<>(initialState,
				isOperator, isSpecialOperator, handleSpecialOperator);

		tokens.forEach(trans);

		/* Grab the tree from the state */
		return initialState.unwrap(pair -> pair.getRight());
	}
}
