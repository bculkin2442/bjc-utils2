package bjc.utils.parserutils;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import bjc.utils.data.GenHolder;
import bjc.utils.data.IHolder;
import bjc.utils.data.IPair;
import bjc.utils.data.Pair;
import bjc.utils.funcdata.IFunctionalList;

/**
 * Creates a parse tree from a postfix expression
 * 
 * @author ben
 *
 */
public class TreeConstructor {
	private static final class TokenTransformer<T> implements Consumer<T> {
		private final class OperatorHandler implements
				Function<IPair<Deque<AST<T>>, AST<T>>, IPair<Deque<AST<T>>, AST<T>>> {
			private T element;

			public OperatorHandler(T element) {
				this.element = element;
			}

			@Override
			public IPair<Deque<AST<T>>, AST<T>> apply(
					IPair<Deque<AST<T>>, AST<T>> pair) {
				return pair.bind((queuedASTs, currentAST) -> {
					return handleOperator(queuedASTs);
				});
			}

			private IPair<Deque<AST<T>>, AST<T>> handleOperator(
					Deque<AST<T>> queuedASTs) {
				AST<T> newAST;

				if (isSpecialOperator.test(element)) {
					newAST = handleSpecialOperator.apply(queuedASTs);
				} else {
					if (queuedASTs.size() < 2) {
						throw new IllegalStateException(
								"Attempted to parse binary operator without enough operands.\n"
										+ "Problem operator is " + element
										+ "\nPossible operand is: \n\t"
										+ queuedASTs.peek());
					}

					AST<T> rightAST = queuedASTs.pop();
					AST<T> leftAST = queuedASTs.pop();

					newAST = new AST<>(element, leftAST, rightAST);
				}

				queuedASTs.push(newAST);

				return new Pair<>(queuedASTs, newAST);
			}
		}

		private IHolder<IPair<Deque<AST<T>>, AST<T>>>	initialState;
		private Predicate<T>							operatorPredicate;
		private Predicate<T>							isSpecialOperator;
		private Function<Deque<AST<T>>, AST<T>>			handleSpecialOperator;

		public TokenTransformer(
				IHolder<IPair<Deque<AST<T>>, AST<T>>> initialState,
				Predicate<T> operatorPredicate,
				Predicate<T> isSpecialOperator,
				Function<Deque<AST<T>>, AST<T>> handleSpecialOperator) {
			this.initialState = initialState;
			this.operatorPredicate = operatorPredicate;
			this.isSpecialOperator = isSpecialOperator;
			this.handleSpecialOperator = handleSpecialOperator;
		}

		@Override
		public void accept(T element) {
			if (operatorPredicate.test(element)) {
				initialState.transform(new OperatorHandler(element));
			} else {
				AST<T> newAST = new AST<>(element);

				initialState.doWith((pair) -> {
					pair.doWith((queue, currentAST) -> {
						queue.push(newAST);
					});
				});

				initialState.transform((pair) -> {
					return pair.bind((queue, currentAST) -> {
						return new Pair<>(queue, newAST);
					});
				});
			}
		}
	}

	/**
	 * Construct a tree from a list of tokens in postfix notation
	 * 
	 * Only binary operators are accepted.
	 * 
	 * @param <T>
	 *            The elements of the parse tree
	 * @param tokens
	 *            The list of tokens to build a tree from
	 * @param operatorPredicate
	 *            The predicate to use to determine if something is a
	 *            operator
	 * @return A AST from the expression
	 */
	public static <T> AST<T> constructTree(IFunctionalList<T> tokens,
			Predicate<T> operatorPredicate) {
		return constructTree(tokens, operatorPredicate, (op) -> false,
				null);
	}

	/**
	 * Construct a tree from a list of tokens in postfix notation
	 * 
	 * Only binary operators are accepted by default. Use the last two
	 * parameters to handle non-binary operators
	 * 
	 * @param <T>
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
	public static <T> AST<T> constructTree(IFunctionalList<T> tokens,
			Predicate<T> operatorPredicate, Predicate<T> isSpecialOperator,
			Function<Deque<AST<T>>, AST<T>> handleSpecialOperator) {
		if (tokens == null) {
			throw new NullPointerException("Tokens must not be null");
		} else if (operatorPredicate == null) {
			throw new NullPointerException(
					"Operator predicate must not be null");
		} else if (isSpecialOperator == null) {
			throw new NullPointerException(
					"Special operator determiner must not be null");
		}

		IHolder<IPair<Deque<AST<T>>, AST<T>>> initialState = new GenHolder<>(
				new Pair<>(new LinkedList<>(), null));

		tokens.forEach(
				new TokenTransformer<>(initialState, operatorPredicate,
						isSpecialOperator, handleSpecialOperator));

		return initialState.unwrap((pair) -> {
			return pair.merge((queue, currentAST) -> currentAST);
		});
	}
}
