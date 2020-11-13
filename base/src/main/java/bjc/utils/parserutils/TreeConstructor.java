package bjc.utils.parserutils;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.*;

import bjc.data.*;
import bjc.funcdata.IList;
import bjc.utils.parserutils.TreeConstructor.*;

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
	 *                    The token type of the tree.
	 */
	public interface QueueFlattener<TokenType>
			extends Function<Deque<ITree<TokenType>>, ITree<TokenType>> {
		/*
		 * Alias
		 */
	}

	/* Alias for constructor state. */
	static final class ConstructorState<TokenType>
			extends Pair<Deque<ITree<TokenType>>, ITree<TokenType>> {
		public ConstructorState(final Deque<ITree<TokenType>> left,
				final ITree<TokenType> right) {
			super(left, right);
		}

		public ConstructorState(
				final IPair<Deque<ITree<TokenType>>, ITree<TokenType>> par) {
			super(par.getLeft(), par.getRight());
		}
	}

	/**
	 * Construct a tree from a list of tokens in postfix notation.
	 *
	 * Only binary operators are accepted.
	 *
	 * @param <TokenType>
	 *                    The elements of the parse tree.
	 *
	 * @param tokens
	 *                    The list of tokens to build a tree from.
	 *
	 * @param isOperator
	 *                    The predicate to use to determine if something is a
	 *                    operator.
	 *
	 * @return A AST from the expression.
	 */
	public static <TokenType> ITree<TokenType> constructTree(
			final IList<TokenType> tokens, final Predicate<TokenType> isOperator) {
		/* Construct a tree with no special operators */
		return constructTree(tokens, isOperator, op -> false, null);
	}

	/**
	 * Construct a tree from a list of tokens in postfix notation.
	 *
	 * Only binary operators are accepted by default. Use the last two parameters to
	 * handle non-binary operators.
	 *
	 * @param <TokenType>
	 *                              The elements of the parse tree.
	 *
	 * @param tokens
	 *                              The list of tokens to build a tree from.
	 *
	 * @param isOperator
	 *                              The predicate to use to determine if something
	 *                              is a operator.
	 *
	 * @param isSpecialOperator
	 *                              The predicate to use to determine if an operator
	 *                              needs special handling.
	 *
	 * @param handleSpecialOperator
	 *                              The function to use to handle special case
	 *                              operators.
	 *
	 * @return A AST from the expression.
	 *
	 */
	public static <TokenType> ITree<TokenType> constructTree(
			final IList<TokenType> tokens, final Predicate<TokenType> isOperator,
			final Predicate<TokenType> isSpecialOperator,
			final Function<TokenType, QueueFlattener<TokenType>> handleSpecialOperator) {
		/*
		 * Make sure our parameters are valid
		 */
		if (tokens == null) {
			throw new NullPointerException("Tokens must not be null");
		} else if (isOperator == null) {
			throw new NullPointerException("Operator predicate must not be null");
		} else if (isSpecialOperator == null) {
			throw new NullPointerException(
					"Special operator determiner must not be null");
		}

		final ConstructorState<TokenType> cstate
				= new ConstructorState<>(new LinkedList<>(), null);

		/* Here is the state for the tree construction */
		final IHolder<ConstructorState<TokenType>> initialState = new Identity<>(cstate);

		/* Transform each of the tokens */
		final TokenTransformer<TokenType> trans = new TokenTransformer<>(initialState,
				isOperator, isSpecialOperator, handleSpecialOperator);

		tokens.forEach(trans);

		/* Grab the tree from the state */
		return initialState.unwrap(ConstructorState::getRight);
	}
}

/*
 * Transform function on tokens
 */
class TokenTransformer<TokenType> implements Consumer<TokenType> {
	/*
	 * Handle operators
	 */
	private final class OperatorHandler
			implements UnaryOperator<ConstructorState<TokenType>> {
		/* The handled element. */
		private final TokenType element;

		/* Create a new operator handler. */
		public OperatorHandler(final TokenType element) {
			this.element = element;
		}

		@Override
		public ConstructorState<TokenType> apply(final ConstructorState<TokenType> pair) {
			/*
			 * Replace the current AST with the result of handling an operator
			 */
			return new ConstructorState<>(
					pair.bindLeft(queuedASTs -> handleOperator(queuedASTs)));
		}

		private ConstructorState<TokenType>
				handleOperator(final Deque<ITree<TokenType>> queuedASTs) {
			/*
			 * The AST we're going to hand back
			 */
			ITree<TokenType> newAST;

			/*
			 * Handle special operators
			 */
			if (isSpecialOperator.test(element)) {
				newAST = handleSpecialOperator.apply(element).apply(queuedASTs);
			} else {
				/*
				 * Error if we don't have enough for a binary operator
				 */
				if (queuedASTs.size() < 2) {
					final String msg = String.format(
							"Attempted to parse binary operator without enough operands\n\tProblem operator is: %s\n\tPossible operand is: %s",
							element.toString(), queuedASTs.peek().toString());

					throw new IllegalStateException(msg);
				}

				/*
				 * Grab the two operands
				 */
				final ITree<TokenType> right = queuedASTs.pop();
				final ITree<TokenType> left = queuedASTs.pop();

				/*
				 * Create a new AST
				 */
				newAST = new Tree<>(element, left, right);
			}

			/*
			 * Stick it onto the stack
			 */
			queuedASTs.push(newAST);

			/*
			 * Hand back the state
			 */
			return new ConstructorState<>(queuedASTs, newAST);
		}
	}

	/* The initial state of the transformer. */
	private final IHolder<ConstructorState<TokenType>> initialState;

	/* The predicate tot use to detect operators. */
	private final Predicate<TokenType> operatorPredicate;

	/* The predicate for detecting special operators. */
	private final Predicate<TokenType> isSpecialOperator;
	/* The function for handling special operators. */
	private final Function<TokenType, QueueFlattener<TokenType>> handleSpecialOperator;

	/**
	 * Create a new transformer
	 *
	 * @param initialState
	 *                              The initial state of the transformer.
	 *
	 * @param operatorPredicate
	 *                              The predicate to use to identify operators.
	 *
	 * @param isSpecialOperator
	 *                              The predicate used to identify special
	 *                              operators.
	 *
	 * @param handleSpecialOperator
	 *                              The function used for handling special
	 *                              operators.
	 */
	public TokenTransformer(final IHolder<ConstructorState<TokenType>> initialState,
			final Predicate<TokenType> operatorPredicate,
			final Predicate<TokenType> isSpecialOperator,
			final Function<TokenType, QueueFlattener<TokenType>> handleSpecialOperator) {
		this.initialState = initialState;
		this.operatorPredicate = operatorPredicate;
		this.isSpecialOperator = isSpecialOperator;
		this.handleSpecialOperator = handleSpecialOperator;
	}

	@Override
	public void accept(final TokenType element) {
		/*
		 * Handle operators
		 */
		if (operatorPredicate.test(element)) {
			initialState.transform(new OperatorHandler(element));
		} else {
			final ITree<TokenType> newAST = new Tree<>(element);

			/*
			 * Insert the new tree into the AST
			 */
			initialState.transform(pair -> new ConstructorState<>(
					pair.bindLeft(queue -> {
						queue.push(newAST);

						return new Pair<>(queue, newAST);
					})
				)
			);
		}
	}
}