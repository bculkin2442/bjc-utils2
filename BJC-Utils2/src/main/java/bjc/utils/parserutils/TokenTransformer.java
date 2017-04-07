package bjc.utils.parserutils;

import bjc.utils.data.IHolder;
import bjc.utils.data.ITree;
import bjc.utils.data.Pair;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.TreeConstructor.ConstructorState;
import bjc.utils.parserutils.TreeConstructor.QueueFlattener;

import java.util.Deque;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

final class TokenTransformer<TokenType> implements Consumer<TokenType> {
	// Handle operators
	private final class OperatorHandler implements UnaryOperator<ConstructorState<TokenType>> {
		private TokenType element;

		public OperatorHandler(TokenType element) {
			this.element = element;
		}

		@Override
		public ConstructorState<TokenType> apply(ConstructorState<TokenType> pair) {
			// Replace the current AST with the result of handling
			// an operator
			return new ConstructorState<>(pair.bindLeft(queuedASTs -> {
				return handleOperator(queuedASTs);
			}));
		}

		private ConstructorState<TokenType> handleOperator(Deque<ITree<TokenType>> queuedASTs) {
			// The AST we're going to hand back
			ITree<TokenType> newAST;

			// Handle special operators
			if(isSpecialOperator.test(element)) {
				newAST = handleSpecialOperator.apply(element).apply(queuedASTs);
			} else {
				// Error if we don't have enough for a binary
				// operator
				if(queuedASTs.size() < 2) {
					String msg = String.format(
							"Attempted to parse binary operator without enough operands\n\tProblem operator is: %s\n\tPossible operand is: %s",
							element.toString(), queuedASTs.peek().toString());

					throw new IllegalStateException(msg);
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
			return new ConstructorState<>(queuedASTs, newAST);
		}
	}

	private IHolder<ConstructorState<TokenType>> initialState;

	private Predicate<TokenType> operatorPredicate;

	private Predicate<TokenType>				isSpecialOperator;
	private Function<TokenType, QueueFlattener<TokenType>>	handleSpecialOperator;

	// Create a new transformer
	public TokenTransformer(IHolder<ConstructorState<TokenType>> initialState,
			Predicate<TokenType> operatorPredicate, Predicate<TokenType> isSpecialOperator,
			Function<TokenType, QueueFlattener<TokenType>> handleSpecialOperator) {
		this.initialState = initialState;
		this.operatorPredicate = operatorPredicate;
		this.isSpecialOperator = isSpecialOperator;
		this.handleSpecialOperator = handleSpecialOperator;
	}

	@Override
	public void accept(TokenType element) {
		// Handle operators
		if(operatorPredicate.test(element)) {
			initialState.transform(new OperatorHandler(element));
		} else {
			ITree<TokenType> newAST = new Tree<>(element);

			// Insert the new tree into the AST
			initialState.transform(pair -> {
				// Transform the pair, ignoring the current AST
				// in favor of the
				// one consisting of the current element
				return new ConstructorState<>(pair.bindLeft(queue -> {
					queue.push(newAST);

					return new Pair<>(queue, newAST);
				}));
			});
		}
	}
}
