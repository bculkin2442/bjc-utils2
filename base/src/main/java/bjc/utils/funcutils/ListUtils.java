package bjc.utils.funcutils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import bjc.funcdata.FunctionalList;
import bjc.funcdata.IList;

/**
 * Utilities for manipulating FunctionalLists and regular Collections lists that
 * don't belong in the class itself.
 *
 * @author ben
 */
public class ListUtils {
	/* The max amount of times to try to partition things. */
	private static final int MAX_NTRIESPART = 50;

	/**
	 * Collapse a string of tokens into a single string without adding any
	 * spaces.
	 *
	 * @param input
	 *                The list of tokens to collapse.
	 *
	 * @return The collapsed string of tokens.
	 */
	public static String collapseTokens(final IList<String> input) {
		if (input == null) throw new NullPointerException("Input must not be null");

		return collapseTokens(input, "");
	}

	/**
	 * Collapse a string of tokens into a single string, adding the desired
	 * separator after each token.
	 *
	 * @param input
	 *                The list of tokens to collapse.
	 *
	 * @param seperator
	 *                The separator to use for separating tokens.
	 *
	 * @return The collapsed string of tokens.
	 */
	public static String collapseTokens(final IList<String> input, final String seperator) {
		if (input == null) {
			throw new NullPointerException("Input must not be null");
		} else if (seperator == null) {
			throw new NullPointerException("Seperator must not be null");
		}

		if (input.getSize() < 1) {
			return "";
		} else if (input.getSize() == 1) {
			return input.first();
		} else {
			final StringBuilder state = new StringBuilder();

			int i = 1;
			for (final String itm : input.toIterable()) {
				state.append(itm);

				if (i != input.getSize()) {
					state.append(seperator);
				}

				i += 1;
			}

			return state.toString();
		}
	}

	/**
	 * Select a number of random items from the list without replacement.
	 *
	 * @param <E>
	 *                The type of items to select.
	 *
	 * @param list
	 *                The list to select from.
	 *
	 * @param number
	 *                The number of items to selet.
	 *
	 * @param rng
	 *                A function that creates a random number from 0 to the
	 *                desired number.
	 *
	 * @return A new list containing the desired number of items randomly
	 *         selected from the specified list without replacement.
	 */
	public static <E> IList<E> drawWithoutReplacement(final IList<E> list, final int number,
			final Function<Integer, Integer> rng) {
		final IList<E> selected = new FunctionalList<>(new ArrayList<>(number));

		final int total = list.getSize();

		final Iterator<E> itr = list.toIterable().iterator();
		E element = null;

		for (final int index = 0; itr.hasNext(); element = itr.next()) {
			/* n - m */
			final int winningChance = number - selected.getSize();

			/* N - t */
			final int totalChance = total - (index - 1);

			/* Probability of selecting the t+1'th element */
			if (NumberUtils.isProbable(winningChance, totalChance, rng)) {
				selected.add(element);
			}
		}

		return selected;
	}

	/**
	 * Select a number of random items from the list, with replacement.
	 *
	 * @param <E>
	 *                The type of items to select.
	 *
	 * @param list
	 *                The list to select from.
	 *
	 * @param number
	 *                The number of items to select.
	 *
	 * @param rng
	 *                A function that creates a random number from 0 to the
	 *                desired number.
	 *
	 * @return A new list containing the desired number of items randomly
	 *         selected from the specified list.
	 */
	public static <E> IList<E> drawWithReplacement(final IList<E> list, final int number,
			final Function<Integer, Integer> rng) {
		final IList<E> selected = new FunctionalList<>(new ArrayList<>(number));

		for (int i = 0; i < number; i++) {
			selected.add(list.randItem(rng));
		}

		return selected;
	}

	/**
	 * Partition a list into a list of lists, where each element can count
	 * for more than one element in a partition.
	 *
	 * @param <E>
	 *                The type of elements in the list to partition.
	 *
	 * @param input
	 *                The list to partition.
	 *
	 * @param counter
	 *                The function to determine the count for each element
	 *                for.
	 *
	 * @param partitionSize
	 *                The number of elements to put in each partition.
	 *
	 * @return A list partitioned according to the above rules.
	 */
	public static <E> IList<IList<E>> groupPartition(final IList<E> input, final Function<E, Integer> counter,
			final int partitionSize) {
		if (input == null) {
			throw new NullPointerException("Input list must not be null");
		} else if (counter == null) {
			throw new NullPointerException("Counter must not be null");
		} else if (partitionSize < 1 || partitionSize > input.getSize()) {
			final String fmt = "%d is not a valid partition size. Must be between 1 and %d";
			final String msg = String.format(fmt, partitionSize, input.getSize());

			throw new IllegalArgumentException(msg);
		}

		/* List that holds our results. */
		final IList<IList<E>> returned = new FunctionalList<>();

		/* List that holds elements rejected during current pass. */
		final IList<E> rejected = new FunctionalList<>();

		final GroupPartIteration<E> it = new GroupPartIteration<>(returned, rejected, partitionSize, counter);

		/* Run up to a certain number of passes. */
		for (int numberOfIterations = 0; numberOfIterations < MAX_NTRIESPART
				&& !rejected.isEmpty(); numberOfIterations++) {
			input.forEach(it);

			if (rejected.isEmpty()) {
				/* Nothing was rejected, so we're done. */
				return returned;
			}
		}

		final String fmt = "Heuristic (more than %d iterations of partitioning) detected an unpartitionable list. (%s)\nThe following elements were not partitioned: %s\nCurrent group in formation: %s\nPreviously formed groups: %s\n";

		final String msg = String.format(fmt, MAX_NTRIESPART, input.toString(), rejected.toString(),
				it.currentPartition.toString(), returned.toString());

		throw new IllegalArgumentException(msg);
	}

	/**
	 * Merge the contents of a bunch of lists together into a single list.
	 *
	 * @param <E>
	 *                The type of value in this lists.
	 *
	 * @param lists
	 *                The values in the lists to merge.
	 *
	 * @return A list containing all the elements of the lists.
	 */
	@SafeVarargs
	public static <E> IList<E> mergeLists(final IList<E>... lists) {
		final IList<E> returned = new FunctionalList<>();

		for (final IList<E> list : lists) {
			for (final E itm : list.toIterable()) {
				returned.add(itm);
			}
		}

		return returned;
	}

	/**
	 * Pad the provided list out to the desired size.
	 *
	 * @param <E>
	 *                The type of elements in the list.
	 *
	 * @param list
	 *                The list to pad out.
	 *
	 * @param counter
	 *                The function to count elements with.
	 *
	 * @param size
	 *                The desired size of the list.
	 *
	 * @param padder
	 *                The function to get elements to pad with.
	 *
	 * @return The list, padded to the desired size.
	 *
	 * @throws IllegalArgumentException
	 *                 If the list couldn't be padded to the desired size.
	 */
	public static <E> IList<E> padList(final IList<E> list, final Function<E, Integer> counter, final int size,
			final Supplier<E> padder) {
		int count = 0;

		final IList<E> returned = new FunctionalList<>();

		for (final E itm : list.toIterable()) {
			count += counter.apply(itm);

			returned.add(itm);
		}

		if (count % size != 0) {
			/* We need to pad */
			int needed = count % size;
			int threshold = 0;

			while (needed > 0 && threshold <= MAX_NTRIESPART) {
				final E val = padder.get();
				final int newCount = counter.apply(val);

				if (newCount <= needed) {
					returned.add(val);

					threshold = 0;

					needed -= newCount;
				} else {
					threshold += 1;
				}
			}

			if (threshold > MAX_NTRIESPART) {
				final String fmt = "Heuristic (more than %d iterations of attempting to pad) detected an unpaddable list. (%s)\nPartially padded list: %S";

				final String msg = String.format(fmt, MAX_NTRIESPART, list.toString(),
						returned.toString());

				throw new IllegalArgumentException(msg);
			}
		}

		return returned;
	}

	/**
	 * Convert a list of longs into an array of longs.
	 * 
	 * @param list
	 *                The list to convert.
	 * @return The list as an array.
	 */
	public static long[] toPrimitive(List<Long> list) {
		long[] res = new long[list.size()];

		int idx = 0;
		for (long val : list) {
			res[idx] = val;

			idx += 1;
		}

		return res;
	}

	/**
	 * Generate all of the permuations of a list.
	 *
	 * This is a version of Algorith P (Plain Changes) from Knuth (vol 4A,
	 * pg 322)
	 *
	 * @param list
	 *                The list to generate permutations from.
	 * @return The list of permutations of the list.
	 */
	public static <T> List<List<T>> permuteList(List<T> list) {
		List<List<T>> permutes = new LinkedList<>();

		/*
		 * Special-case small usages.
		 */
		if (list.size() == 0) {
			return permutes;
		}

		if (list.size() == 1) {
			permutes.add(list);

			return permutes;
		}

		if (list.size() == 2) {
			T elm1 = list.get(0);
			T elm2 = list.get(1);

			List<T> currPerm = new ArrayList<>(2);

			currPerm.add(elm1);
			currPerm.add(elm2);

			permutes.add(currPerm);

			currPerm = new ArrayList<>(2);

			currPerm.add(elm2);
			currPerm.add(elm1);

			permutes.add(currPerm);

			return permutes;
		}

		int[] auxC = new int[list.size()];
		int[] auxO = new int[list.size()];

		for (int i = 0; i < list.size(); i++) {
			auxC[i] = 0;
			auxO[i] = 1;
		}

		List<T> currentPermute = new ArrayList<>(list.size());
		for (T elm : list) {
			currentPermute.add(elm);
		}
		permutes.add(currentPermute);

		int j = list.size() - 1;
		int s = 0;

		while (true) {
			int q = auxC[j] + auxO[j];

			if (q < 0) {
				auxO[j] = -auxO[j];
				j -= 1;

				continue;
			}

			if (q == j) {
				if (j == 0) break;

				s += 1;

				auxO[j] = -auxO[j];
				j -= 1;

				continue;
			}

			int idx1 = j - auxC[j] + s;
			int idx2 = j - q - s;

			swapList(list, idx1, idx2);

			auxC[j] = q;

			currentPermute = new ArrayList<>(list.size());
			for (T elm : list) {
				currentPermute.add(elm);
			}
			permutes.add(currentPermute);

			j = list.size() - 1;
			s = 0;
		}

		return permutes;
	}

	private static <T> void swapList(List<T> list, int a, int b) {
		T tmp = list.get(a);

		list.set(a, list.get(b));
		list.set(b, tmp);
	}
}
