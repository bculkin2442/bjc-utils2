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
import bjc.utils.funcutils.StringUtils;

final class TokenTransformer<T> implements Consumer<T> {
	private final class OperatorHandler
			implements UnaryOperator<IPair<Deque<ITree<T>>, ITree<T>>> {
		private T element;

		public OperatorHandler(T element) {
			this.element = element;
		}

		@Override
		public IPair<Deque<ITree<T>>, ITree<T>>
				apply(IPair<Deque<ITree<T>>, ITree<T>> pair) {
			return pair.bind((queuedASTs, currentAST) -> {
				return handleOperator(queuedASTs);
			});
		}

		private IPair<Deque<ITree<T>>, ITree<T>>
				handleOperator(Deque<ITree<T>> queuedASTs) {
			ITree<T> newAST;

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

				ITree<T> rightAST = queuedASTs.pop();
				ITree<T> leftAST = queuedASTs.pop();

				newAST = new Tree<>(element, leftAST, rightAST);
			}

			queuedASTs.push(newAST);

			return new Pair<>(queuedASTs, newAST);
		}
	}

	private IHolder<IPair<Deque<ITree<T>>, ITree<T>>>			initialState;
	private Predicate<T>										operatorPredicate;
	private Predicate<T>										isSpecialOperator;
	private Function<T, Function<Deque<ITree<T>>, ITree<T>>>	handleSpecialOperator;

	public TokenTransformer(
			IHolder<IPair<Deque<ITree<T>>, ITree<T>>> initialState,
			Predicate<T> operatorPredicate, Predicate<T> isSpecialOperator,
			Function<T, Function<Deque<ITree<T>>, ITree<T>>> handleSpecialOperator) {
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
			ITree<T> newAST = new Tree<>(element);

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