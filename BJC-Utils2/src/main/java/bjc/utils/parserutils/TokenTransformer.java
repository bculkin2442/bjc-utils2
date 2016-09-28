package bjc.utils.parserutils;

import java.util.Deque;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import bjc.utils.data.IHolder;
import bjc.utils.data.IPair;
import bjc.utils.data.Pair;
import bjc.utils.funcdata.ITree;
import bjc.utils.funcdata.Tree;

final class TokenTransformer<TokenType> implements Consumer<TokenType> {
	private final class OperatorHandler implements UnaryOperator<
			IPair<Deque<ITree<TokenType>>, ITree<TokenType>>> {
		private TokenType element;

		public OperatorHandler(TokenType element) {
			this.element = element;
		}

		@Override
		public IPair<Deque<ITree<TokenType>>, ITree<TokenType>> apply(
				IPair<Deque<ITree<TokenType>>, ITree<TokenType>> pair) {
			return pair.bindLeft((queuedASTs) -> {
				return handleOperator(queuedASTs);
			});
		}

		private IPair<Deque<ITree<TokenType>>,
				ITree<TokenType>> handleOperator(
						Deque<ITree<TokenType>> queuedASTs) {
			ITree<TokenType> newAST;

			if (isSpecialOperator.test(element)) {
				newAST = handleSpecialOperator.apply(element)
						.apply(queuedASTs);
			} else {
				if (queuedASTs.size() < 2) {
					throw new IllegalStateException(
							"Attempted to parse binary operator without enough operands.\n"
									+ "Problem operator is " + element
									+ "\nPossible operand is: \n\t"
									+ queuedASTs.peek());
				}

				ITree<TokenType> rightAST = queuedASTs.pop();
				ITree<TokenType> leftAST = queuedASTs.pop();

				newAST = new Tree<>(element, leftAST, rightAST);
			}

			queuedASTs.push(newAST);

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
		if (operatorPredicate.test(element)) {
			initialState.transform(new OperatorHandler(element));
		} else {
			ITree<TokenType> newAST = new Tree<>(element);

			initialState.transform((pair) -> {
				return pair.bindLeft((queue) -> {
					queue.push(newAST);

					return new Pair<>(queue, newAST);
				});
			});
		}
	}
}