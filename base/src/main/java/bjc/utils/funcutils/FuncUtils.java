package bjc.utils.funcutils;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Utility things for functions.
 *
 * @author ben
 */
public class FuncUtils {
	/**
	 * Convert a binary function into a unary function that returns a function.
	 *
	 * @param <A>
	 *             The initial type of the function.
	 *
	 * @param <B>
	 *             The intermediate type of the function.
	 *
	 * @param <C>
	 *             The terminal type of the function.
	 *
	 * @param func
	 *             The function to transform.
	 *
	 * @return The function transformed into a unary function returning a function.
	 */
	public static <A, B, C> Function<A, Function<B, C>>
			curry2(final BiFunction<A, B, C> func) {
		return arg1 -> arg2 -> func.apply(arg1, arg2);
	}

	/**
	 * Do the specified action the specified number of times.
	 *
	 * @param nTimes
	 *               The number of times to do the action.
	 *
	 * @param cons
	 *               The action to perform.
	 */
	public static void doTimes(final int nTimes, final Consumer<Integer> cons) {
		for (int i = 0; i < nTimes; i++) {
			cons.accept(i);
		}
	}

	/**
	 * Return an operator that executes until it converges.
	 *
	 * @param op
	 *                 The operator to execute.
	 *
	 * @param maxTries
	 *                 The maximum amount of times to apply the function in an
	 *                 attempt to cause it to converge.
	 *
	 * @return The requested operator.
	 */
	public static <T> UnaryOperator<T> converge(final UnaryOperator<T> op,
			final int maxTries) {
		return converge(op, Object::equals, maxTries);
	}

	/**
	 * Return an operator that executes until it converges.
	 *
	 * @param op
	 *                  The operator to execute.
	 * @param converged
	 *                  The predicate to execute to check if the function has
	 *                  converged.
	 *
	 * @param maxTries
	 *                  The maximum amount of times to apply the function in an
	 *                  attempt to cause it to converge.
	 *
	 * @return The requested operator.
	 */
	public static <T> UnaryOperator<T> converge(final UnaryOperator<T> op,
			final BiPredicate<T, T> converged, final int maxTries) {
		return val -> {
			T newVal = op.apply(val);
			T oldVal;

			int tries = 0;

			do {
				oldVal = newVal;
				newVal = op.apply(newVal);

				tries += 1;
			} while (!converged.test(newVal, oldVal) && tries < maxTries);

			return newVal;
		};
	}
}
