package bjc.utils.parserutils;

import java.util.Deque;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import bjc.utils.data.IHolder;
import bjc.utils.data.ITree;
import bjc.utils.data.Pair;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.TreeConstructor.ConstructorState;
import bjc.utils.parserutils.TreeConstructor.QueueFlattener;

/*
 * Handle creating ASTs from tokens.
 */
final class TokenTransformer<TokenType> implements Consumer<TokenType> {
	/*
	 * Handle operators
	 */
	private final class OperatorHandler implements UnaryOperator<ConstructorState<TokenType>> {
		private final TokenType element;

		public OperatorHandler(final TokenType element) {
			this.element = element;
		}

		@Override
		public ConstructorState<TokenType> apply(final ConstructorState<TokenType> pair) {
			/*
			 * Replace the current AST with the result of handling an operator
			 */
			return new ConstructorState<>(pair.bindLeft(queuedASTs -> {
				return handleOperator(queuedASTs);
			}));
		}

		private ConstructorState<TokenType> handleOperator(final Deque<ITree<TokenType>> queuedASTs) {
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

	private final IHolder<ConstructorState<TokenType>> initialState;

	private final Predicate<TokenType> operatorPredicate;

	private final Predicate<TokenType>				isSpecialOperator;
	private final Function<TokenType, QueueFlattener<TokenType>>	handleSpecialOperator;

	/*
	 * Create a new transformer
	 */
	public TokenTransformer(final IHolder<ConstructorState<TokenType>> initialState,
			final Predicate<TokenType> operatorPredicate, final Predicate<TokenType> isSpecialOperator,
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
			initialState.transform(pair -> {
				/*
				 * Transform the pair, ignoring the current AST in favor of the one consisting of the current element
				 */
				return new ConstructorState<>(pair.bindLeft(queue -> {
					queue.push(newAST);

					return new Pair<>(queue, newAST);
				}));
			});
		}
	}
}
