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
	 * @param toks
	 *            The list of tokens to build a tree from
	 * @param opPredicate
	 *            The predicate to use to determine if something is a
	 *            operator
	 * @return A AST from the expression
	 * 
	 * @deprecated Use
	 *             {@link TreeConstructor#constructTree(FunctionalList, Predicate, Predicate, Function)}
	 *             instead
	 */
	public static <T> AST<T> constructTree(FunctionalList<T> toks,
			Predicate<T> opPredicate) {
		return constructTree(toks, opPredicate, (op) -> false, null);
	}

	/**
	 * Construct a tree from a list of tokens in postfix notation
	 * 
	 * Only binary operators are accepted.
	 * 
	 * @param <T>
	 *            The elements of the parse tree
	 * @param toks
	 *            The list of tokens to build a tree from
	 * @param opPredicate
	 *            The predicate to use to determine if something is a
	 *            operator
	 * @param isSpecialOp
	 *            The predicate to use to determine if an operator needs
	 *            special handling
	 * @param handleSpecialOp
	 *            The function to use to handle special case operators
	 * @return A AST from the expression
	 * 
	 *         FIXME The handleSpecialOp function seems like an ugly
	 *         interface. Maybe there's a better way to express how that
	 *         works
	 */
	public static <T> AST<T> constructTree(FunctionalList<T> toks,
			Predicate<T> opPredicate, Predicate<T> isSpecialOp,
			Function<Deque<AST<T>>, AST<T>> handleSpecialOp) {
		GenHolder<Pair<Deque<AST<T>>, AST<T>>> initState =
				new GenHolder<>(new Pair<>(new LinkedList<>(), null));

		toks.forEach((ele) -> {
			if (opPredicate.test(ele)) {
				initState.transform((par) -> {
					Deque<AST<T>> lft = par.merge((deq, ast) -> deq);

					AST<T> mergedAST = par.merge((deq, ast) -> {
						AST<T> newAST;

						if (isSpecialOp.test(ele)) {
							newAST = handleSpecialOp.apply(deq);
						} else {
							AST<T> right = deq.pop();
							AST<T> left = deq.pop();
							newAST = new AST<>(ele, left, right);
						}

						deq.push(newAST);
						return newAST;
					});

					Pair<Deque<AST<T>>, AST<T>> newPair =
							new Pair<>(lft, mergedAST);

					return newPair;
				});
			} else {
				AST<T> newAST = new AST<>(ele);

				initState.doWith((par) -> par.doWith((deq, ast) -> {
					deq.push(newAST);
				}));

				initState.transform((par) -> {
					return (Pair<Deque<AST<T>>, AST<T>>) par
							.apply((d) -> d, (a) -> newAST);
				});
			}
		});

		return initState.unwrap((par) -> par.merge((deq, ast) -> ast));
	}
}
