package bjc.utils.parserutils;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Predicate;

import bjc.utils.data.GenHolder;
import bjc.utils.data.Pair;
import bjc.utils.funcdata.FunctionalList;

/**
 * Creates a parse tree from a postfix expression
 * 
 * @author ben
 *
 * @param <T>
 *            The elements of the parse tree
 */
public class TreeConstructor {
	/**
	 * Construct a tree from a list of tokens in postfix notation
	 * 
	 * Only binary operators are accepted.
	 * 
	 * @param toks
	 *            The list of tokens to build a tree from
	 * @param opPredicate
	 *            The predicate to use to determine if something is a
	 *            operator
	 * @return A AST from the expression
	 */
	public static <T> AST<T> constructTree(FunctionalList<T> toks,
			Predicate<T> opPredicate) {
		GenHolder<Pair<Deque<AST<T>>, AST<T>>> initState = new GenHolder<>(
				new Pair<>(new LinkedList<>(), null));

		toks.forEach((ele) -> {
			if (opPredicate.test(ele)) {
				initState.transform((par) -> {
					Deque<AST<T>> lft = par.merge((deq, ast) -> deq);

					AST<T> mergedAST = par.merge((deq, ast) -> {
						AST<T> right = deq.pop();
						AST<T> left = deq.pop();

						AST<T> newAST = new AST<T>(ele, left, right);

						deq.push(newAST);
						
						return newAST;
					});

					Pair<Deque<AST<T>>, AST<T>> newPair = new Pair<>(lft,
							mergedAST);

					return newPair;
				});
			} else {
				initState.doWith((par) -> par
						.doWith((deq, ast) -> deq.push(new AST<>(ele))));
			}
		});

		return initState.unwrap((par) -> par.merge((deq, ast) -> ast));
	}
}
