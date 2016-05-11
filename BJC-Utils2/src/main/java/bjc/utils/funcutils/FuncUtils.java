package bjc.utils.funcutils;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility things for functions
 * 
 * @author ben
 *
 */
public class FuncUtils {
	/**
	 * Convert a binary function into a unary function that returns a
	 * function
	 * 
	 * @param <A>
	 *            The initial type of the function
	 * @param <B>
	 *            The intermediate type of the function
	 * @param <C>
	 *            The terminal type of the function
	 * @param func
	 *            The function to transform
	 * @return The function transformed into a unary function returning a
	 *         function
	 */
	public static <A, B, C> Function<A, Function<B, C>> curry2(
			BiFunction<A, B, C> func) {
		return (arg1) -> (arg2) -> {
			return func.apply(arg1, arg2);
		};
	}

	/**
	 * Do the specified action the specified number of times
	 * 
	 * @param nTimes
	 *            The number of times to do the action
	 * @param cons
	 *            The action to perform
	 */
	public static void doTimes(int nTimes, Consumer<Integer> cons) {
		for (int i = 0; i < nTimes; i++) {
			cons.accept(i);
		}
	}
}
