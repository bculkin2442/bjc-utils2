package bjc.utils.funcutils;

import java.util.Random;
import java.util.function.Consumer;

import bjc.funcdata.FunctionalList;
import bjc.funcdata.ListEx;

/**
 * Utility methods on enums.
 *
 * @author ben
 */
public class EnumUtils {
	/**
	 * Do an action for a random number of enum values.
	 *
	 * @param <E>
	 *                The type of the enum.
	 *
	 * @param clasz
	 *                The enum class.
	 *
	 * @param nValues
	 *                The number of values to execute the action on.
	 *
	 * @param action
	 *                The action to perform on random values.
	 *
	 * @param rnd
	 *                The source of randomness to use.
	 */
	public static <E extends Enum<E>> void doForValues(final Class<E> clasz,
			final int nValues, final Consumer<E> action, final Random rnd) {
		final E[] enumValues = clasz.getEnumConstants();

		final ListEx<E> valueList = new FunctionalList<>(enumValues);

		final int randomValueCount = enumValues.length - nValues;

		for (int i = 0; i <= randomValueCount; i++) {
			final E rDir = valueList.randItem(rnd::nextInt);

			valueList.removeMatching(rDir);
		}

		valueList.forEach(action);
	}

	/**
	 * Get a random value from an enum.
	 *
	 * @param <E>
	 *              The type of the enum.
	 *
	 * @param clasz
	 *              The class of the enum.
	 *
	 * @param rnd
	 *              The random source to use.
	 *
	 * @return A random value from the specified enum.
	 */
	public static <E extends Enum<E>> E getRandomValue(final Class<E> clasz,
			final Random rnd) {
		final E[] enumValues = clasz.getEnumConstants();

		return new FunctionalList<>(enumValues).randItem(rnd::nextInt);
	}
}
