package bjc.utils.parserutils;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Predicate;

import bjc.utils.data.GenHolder;
import bjc.utils.data.Pair;
import bjc.utils.funcdata.FunctionalList;

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
	 * @param <T>
	 *            The elements of the parse tree
	 * @param tokens
	 *            The list of tokens to build a tree from
	 * @param operatorPredicate
	 *            The predicate to use to determine if something is a
	 *            operator
	 * @return A AST from the expression
	 * 
	 * @deprecated Use
	 *             {@link TreeConstructor#constructTree(FunctionalList, Predicate, Predicate, Function)}
	 *             instead
	 */
	public static <T> AST<T> constructTree(FunctionalList<T> tokens,
			Predicate<T> operatorPredicate) {
		return constructTree(tokens, operatorPredicate, (op) -> false,
				null);
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
	public static <T> AST<T> constructTree(FunctionalList<T> tokens,
			Predicate<T> operatorPredicate, Predicate<T> isSpecialOperator,
			Function<Deque<AST<T>>, AST<T>> handleSpecialOperator) {
		GenHolder<Pair<Deque<AST<T>>, AST<T>>> initialState =
				new GenHolder<>(new Pair<>(new LinkedList<>(), null));

		tokens.forEach((element) -> {
			if (operatorPredicate.test(element)) {
				initialState.transform((pair) -> {
					Deque<AST<T>> queuedASTs =
							pair.merge((queue, currentAST) -> queue);

					AST<T> mergedAST = pair.merge((queue, currentAST) -> {
						AST<T> newAST;

						if (isSpecialOperator.test(element)) {
							newAST = handleSpecialOperator.apply(queue);
						} else {
							AST<T> rightAST = queue.pop();
							AST<T> leftAST = queue.pop();

							newAST = new AST<>(element, leftAST, rightAST);
						}

						queue.push(newAST);
						return newAST;
					});

					Pair<Deque<AST<T>>, AST<T>> newPair =
							new Pair<>(queuedASTs, mergedAST);

					return newPair;
				});
			} else {
				AST<T> newAST = new AST<>(element);

				initialState.doWith(
						(pair) -> pair.doWith((queue, currentAST) -> {
							queue.push(newAST);
						}));

				initialState.transform((pair) -> {
					return (Pair<Deque<AST<T>>, AST<T>>) pair.apply(
							(queue) -> queue, (currentAST) -> newAST);
				});
			}
		});

		return initialState.unwrap(
				(pair) -> pair.merge((queue, currentAST) -> currentAST));
	}
}
