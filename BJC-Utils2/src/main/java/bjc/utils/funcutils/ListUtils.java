package bjc.utils.funcutils;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;

import bjc.utils.data.IPair;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

/**
 * Utilities for manipulating FunctionalLists that don't belong in the
 * class itself
 * 
 * @author ben
 *
 */
public class ListUtils {
	private static final int MAX_NTRIESPART = 50;

	/**
	 * Collapse a string of tokens into a single string without adding any
	 * spaces
	 * 
	 * @param input
	 *            The list of tokens to collapse
	 * @return The collapsed string of tokens
	 */
	public static String collapseTokens(IList<String> input) {
		if (input == null) {
			throw new NullPointerException("Input must not be null");
		}

		return collapseTokens(input, "");
	}

	/**
	 * Collapse a string of tokens into a single string, adding the desired
	 * seperator after each token
	 * 
	 * @param input
	 *            The list of tokens to collapse
	 * @param seperator
	 *            The seperator to use for seperating tokens
	 * @return The collapsed string of tokens
	 */
	public static String collapseTokens(IList<String> input, String seperator) {
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
			StringBuilder state = new StringBuilder();

			int i = 1;
			for(String itm : input.toIterable()) {
				state.append(itm);

				if(i != input.getSize()) {
					state.append(seperator);
				}

				i += 1;
			}

			return state.toString();
		}
	}

	/**
	 * Split off affixes from tokens
	 * 
	 * @param input
	 *            The tokens to deaffix
	 * @param operators
	 *            The affixes to remove
	 * @return The tokens that have been deaffixed
	 * 
	 */
	public static IList<String> deAffixTokens(IList<String> input,
			Deque<IPair<String, String>> operators) {
		if (input == null) {
			throw new NullPointerException("Input must not be null");
		} else if (operators == null) {
			throw new NullPointerException("Set of operators must not be null");
		}

		IList<String> returned = input;

		for(IPair<String, String> op : operators) {
			returned = returned.flatMap(token -> {
				return op.merge(new TokenDeaffixer(token));
			});
		}

		return returned;
	}

	/**
	 * Select a number of random items from the list without replacement
	 * 
	 * @param <E>
	 *            The type of items to select
	 * @param list
	 *            The list to select from
	 * @param number
	 *            The number of items to selet
	 * @param rng
	 *            A function that creates a random number from 0 to the
	 *            desired number
	 * @return A new list containing the desired number of items randomly
	 *         selected from the specified list without replacement
	 */

	public static <E> IList<E> drawWithoutReplacement(
			IList<E> list, int number, Function<Integer, Integer> rng) {
		IList<E> selected = new FunctionalList<>(new ArrayList<>(number));

		int total = list.getSize();
		
		Iterator<E> itr = list.toIterable().iterator();
		E element = null;

		for(int index = 0; itr.hasNext(); element = itr.next()) {
			int winningChance = number - selected.getSize();
			// n - m

			int totalChance = total - (index - 1);
			// N - t

			// Probability of selecting the t+1'th element
			if (NumberUtils.isProbable(winningChance, totalChance, rng)) {
				selected.add(element);
			}
		}

		return selected;
	}

	/**
	 * Select a number of random items from the list, with replacement
	 * 
	 * @param <E>
	 *            The type of items to select
	 * @param list
	 *            The list to select from
	 * @param number
	 *            The number of items to selet
	 * @param rng
	 *            A function that creates a random number from 0 to the
	 *            desired number
	 * @return A new list containing the desired number of items randomly
	 *         selected from the specified list
	 */
	public static <E> IList<E> drawWithReplacement(IList<E> list,
			int number, Function<Integer, Integer> rng) {
		IList<E> selected = new FunctionalList<>(new ArrayList<>(number));

		for (int i = 0; i < number; i++) {
			selected.add(list.randItem(rng));
		}

		return selected;
	}

	/**
	 * Partition a list into a list of lists, where each element can count
	 * for more than one element in a partition
	 * 
	 * @param <E>
	 *            The type of elements in the list to partition
	 * 
	 * @param input
	 *            The list to partition
	 * @param counter
	 *            The function to determine the count for each element for
	 * @param partitionSize
	 *            The number of elements to put in each partition
	 * 
	 * @return A list partitioned according to the above rules
	 */
	public static <E> IList<IList<E>> groupPartition(
			IList<E> input, Function<E, Integer> counter, int partitionSize) {
		if (input == null) {
			throw new NullPointerException("Input list must not be null");
		} else if (counter == null) {
			throw new NullPointerException("Counter must not be null");
		} else if (partitionSize < 1 || partitionSize > input.getSize()) {
			throw new IllegalArgumentException(
					"" + partitionSize + " is not a valid"
							+ " partition size. Must be between 1 and "
							+ input.getSize());
		}

		/*
		 * List that holds our results
		 */
		IList<IList<E>> returned = new FunctionalList<>();

		/*
		 * List that holds elements rejected during current pass
		 */
		IList<E> rejected = new FunctionalList<>();

		GroupPartIteration<E> it = new GroupPartIteration<>(returned, rejected, partitionSize, counter);

		/*
		 * Run up to a certain number of passes
		 */
		for (int numberOfIterations = 0; numberOfIterations < MAX_NTRIESPART
				&& !rejected.isEmpty(); numberOfIterations++) {
			input.forEach(it);

			if (rejected.isEmpty()) {
				// Nothing was rejected, so we're done
				return returned;
			}
		}

		throw new IllegalArgumentException(
				"Heuristic (more than " + MAX_NTRIESPART
						+ " iterations of partitioning) detected unpartitionable list "
						+ input.toString()
						+ "\nThe following elements were not partitioned: "
						+ rejected.toString()
						+ "\nCurrent group in formation: "
						+ it.currentPartition.toString()
						+ "\nPreviously formed groups: "
						+ returned.toString());
	}

	/**
	 * Merge the contents of a bunch of lists together into a single list
	 * 
	 * @param <E>
	 *            The type of value in this lists
	 * @param lists
	 *            The values in the lists to merge
	 * @return A list containing all the elements of the lists
	 */
	@SafeVarargs
	public static <E> IList<E> mergeLists(IList<E>... lists) {
		IList<E> returned = new FunctionalList<>();

		for (IList<E> list : lists) {
			for(E itm : list.toIterable()) {
				returned.add(itm);
			}
		}

		return returned;
	}

	/**
	 * Pad the provided list out to the desired size
	 * 
	 * @param <E>
	 *            The type of elements in the list
	 * @param list
	 *            The list to pad out
	 * @param counter
	 *            The function to count elements with
	 * @param size
	 *            The desired size of the list
	 * @param padder
	 *            The function to get elements to pad with
	 * @return The list, padded to the desired size
	 * @throws IllegalArgumentException
	 *             if the list couldn't be padded to the desired size
	 */
	public static <E> IList<E> padList(IList<E> list,
			Function<E, Integer> counter, int size,
			Supplier<E> padder) {
		int count = 0;

		IList<E> returned = new FunctionalList<>();
		
		for(E itm : list.toIterable()) {
			count += counter.apply(itm);

			returned.add(itm);
		}

		if ((count % size) != 0) {
			// We need to pad
			int needed = count % size;
			int threshold = 0;

			while (needed > 0 && threshold <= MAX_NTRIESPART) {
				E val = padder.get();
				int newCount = counter.apply(val);

				if (newCount <= needed) {
					returned.add(val);

					threshold = 0;

					needed -= newCount;
				} else {
					threshold += 1;
				}
			}

			if (threshold > MAX_NTRIESPART) {
				throw new IllegalArgumentException("Heuristic (more than "
						+ MAX_NTRIESPART
						+ " iterations of attempting to pad) detected unpaddable list ");
			}
		}

		return returned;
	}

	/**
	 * Split tokens in a list of tokens into multiple tokens.
	 * 
	 * The intended use is for expression parsers so that you can enter
	 * something like 1+1 instead of 1 + 1.
	 * 
	 * @param input
	 *            The tokens to split
	 * @param operators
	 *            Pairs of operators to split on and regexes that match
	 *            those operators
	 * @return A list of tokens split on all the operators
	 * 
	 */
	public static IList<String> splitTokens(IList<String> input,
			Deque<IPair<String, String>> operators) {
		if (input == null) {
			throw new NullPointerException("Input must not be null");
		} else if (operators == null) {
			throw new NullPointerException("Set of operators must not be null");
		}

		IList<String> returned = input;

		for(IPair<String, String> op : operators) {
			returned = returned.flatMap(token -> {
				return op.merge(new TokenSplitter(token));
			});
		}

		return returned;
	}
}
