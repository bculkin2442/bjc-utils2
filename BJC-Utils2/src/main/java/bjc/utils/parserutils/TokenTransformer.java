package bjc.utils.parserutils;

import java.util.Deque;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import bjc.utils.data.IHolder;
import bjc.utils.data.IPair;
import bjc.utils.data.ITree;
import bjc.utils.data.Pair;
import bjc.utils.data.Tree;

final class TokenTransformer<TokenType> implements Consumer<TokenType> {
	// Handle operators
	private final class OperatorHandler implements UnaryOperator<
			IPair<Deque<ITree<TokenType>>, ITree<TokenType>>> {
		private TokenType element;

		public OperatorHandler(TokenType element) {
			this.element = element;
		}

		@Override
		public IPair<Deque<ITree<TokenType>>, ITree<TokenType>> apply(
				IPair<Deque<ITree<TokenType>>, ITree<TokenType>> pair) {
			// Replace the current AST with the result of handling an operator
			return pair.bindLeft((queuedASTs) -> {
				return handleOperator(queuedASTs);
			});
		}

		private IPair<Deque<ITree<TokenType>>,
				ITree<TokenType>> handleOperator(
						Deque<ITree<TokenType>> queuedASTs) {
			// The AST we're going to hand back
			ITree<TokenType> newAST;

			// Handle special operators
			if (isSpecialOperator.test(element)) {
				newAST = handleSpecialOperator.apply(element)
						.apply(queuedASTs);
			} else {
				// Error if we don't have enough for a binary operator
				if (queuedASTs.size() < 2) {
					throw new IllegalStateException(
							"Attempted to parse binary operator without enough operands.\n"
									+ "Problem operator is " + element
									+ "\nPossible operand is: \n\t"
									+ queuedASTs.peek());
				}

				// Grab the two operands
				ITree<TokenType> right = queuedASTs.pop();
				ITree<TokenType> left = queuedASTs.pop();

				// Create a new AST
				newAST = new Tree<>(element, left, right);
			}

			// Stick it onto the stack
			queuedASTs.push(newAST);

			// Hand back the state
			return new Pair<>(queuedASTs, newAST);
		}
	}

	private IHolder<IPair<Deque<ITree<TokenType>>,
			ITree<TokenType>>>															initialState;

	private Predicate<
			TokenType>																	operatorPredicate;

	private Predicate<
			TokenType>																	isSpecialOperator;
	private Function<TokenType, Function<Deque<ITree<TokenType>>,
			ITree<TokenType>>>															handleSpecialOperator;

	// Create a new transformer
	public TokenTransformer(
			IHolder<IPair<Deque<ITree<TokenType>>,
					ITree<TokenType>>> initialState,
			Predicate<TokenType> operatorPredicate,
			Predicate<TokenType> isSpecialOperator,
			Function<TokenType, Function<Deque<ITree<TokenType>>,
					ITree<TokenType>>> handleSpecialOperator) {
		this.initialState = initialState;
		this.operatorPredicate = operatorPredicate;
		this.isSpecialOperator = isSpecialOperator;
		this.handleSpecialOperator = handleSpecialOperator;
	}

	@Override
	public void accept(TokenType element) {
		// Handle operators
		if (operatorPredicate.test(element)) {
			initialState.transform(new OperatorHandler(element));
		} else {
			ITree<TokenType> newAST = new Tree<>(element);

			// Insert the new tree into the AST
			initialState.transform((pair) -> {
				// Transform the pair, ignoring the current AST in favor of the
				// one consisting of the current element
				return pair.bindLeft((queue) -> {
					queue.push(newAST);

					return new Pair<>(queue, newAST);
				});
			});
		}
	}
}
