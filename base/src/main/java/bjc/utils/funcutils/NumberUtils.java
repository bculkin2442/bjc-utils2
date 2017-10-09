package bjc.utils.funcutils;

import java.util.function.Function;

/**
 * Utility functions for dealing with numbers
 *
 * @author ben
 *
 */
public class NumberUtils {
	/**
	 * Compute the falling factorial of a number
	 *
	 * @param value
	 *                The number to compute
	 * @param power
	 *                The power to do the falling factorial for
	 * @return The falling factorial of the number to the power
	 */
	public static int fallingFactorial(final int value, final int power) {
		if (power == 0)
			return 1;
		else if (power == 1)
			return value;
		else {
			int result = 1;

			for (int currentSub = 0; currentSub < power + 1; currentSub++) {
				result *= value - currentSub;
			}

			return result;
		}
	}

	/**
	 * Evaluates a linear probability distribution
	 *
	 * @param winning
	 *                The number of winning possibilities
	 * @param total
	 *                The number of total possibilities
	 * @param rng
	 *                The function to use to generate a random possibility
	 * @return Whether or not a random possibility was a winning one
	 */
	public static boolean isProbable(final int winning, final int total, final Function<Integer, Integer> rng) {
		return rng.apply(total) < winning;
	}

	/**
	 * Check if a number is in an inclusive range.
	 *
	 * @param min
	 *                The minimum value of the range.
	 *
	 * @param max
	 *                The maximum value of the range.
	 *
	 * @param i
	 *                The number to check.
	 *
	 * @return Whether the number is in the range.
	 */
	public static boolean between(final int min, final int max, final int i) {
		return i >= min && i <= max;
	}
}
