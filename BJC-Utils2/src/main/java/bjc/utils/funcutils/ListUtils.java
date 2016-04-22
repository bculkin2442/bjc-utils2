package bjc.utils.funcutils;

import java.util.ArrayList;
import java.util.Deque;
import java.util.function.Function;

import bjc.utils.data.IHolder;
import bjc.utils.data.IPair;
import bjc.utils.data.Identity;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IFunctionalList;

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
	public static String collapseTokens(IFunctionalList<String> input) {
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
	public static String collapseTokens(IFunctionalList<String> input,
			String seperator) {
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
			return input.reduceAux("", (currentString, state) -> {
				return state + currentString + seperator;
			}, (strang) -> {
				return strang.substring(0,
						strang.length() - seperator.length());
			});
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
	public static IFunctionalList<String> deAffixTokens(
			IFunctionalList<String> input,
			Deque<IPair<String, String>> operators) {
		if (input == null) {
			throw new NullPointerException("Input must not be null");
		} else if (operators == null) {
			throw new NullPointerException(
					"Set of operators must not be null");
		}

		IHolder<IFunctionalList<String>> returnedList = new Identity<>(
				input);

		operators.forEach((operator) -> returnedList
				.transform((oldReturn) -> oldReturn.flatMap((token) -> {
					return operator.merge(new TokenDeaffixer(token));
				})));

		return returnedList.unwrap((list) -> list);
	}

	/**
	 * Select a number of random items from the list without replacement
	 * 
	 * @param <E>
	 *            The type of items to select
	 * @param list
	 *            The list to select from
	 * @param numberOfItems
	 *            The number of items to selet
	 * @param rng
	 *            A function that creates a random number from 0 to the
	 *            desired number
	 * @return A new list containing the desired number of items randomly
	 *         selected from the specified list without replacement
	 */

	public static <E> IFunctionalList<E> drawWithoutReplacement(
			IFunctionalList<E> list, int numberOfItems,
			Function<Integer, Integer> rng) {
		IFunctionalList<E> selectedItems = new FunctionalList<>(
				new ArrayList<>(numberOfItems));

		int totalItems = list.getSize();

		list.forEachIndexed((index, element) -> {
			int winningChance = numberOfItems - selectedItems.getSize();
			// n - m
			int totalChance = totalItems - (index - 1);
			// N - t

			// Probability of selecting the t+1'th element
			if (NumberUtils.isProbable(winningChance, totalChance, rng)) {
				selectedItems.add(element);
			}
		});

		return selectedItems;
	}

	/**
	 * Select a number of random items from the list, with replacement
	 * 
	 * @param <E>
	 *            The type of items to select
	 * @param list
	 *            The list to select from
	 * @param numberOfItems
	 *            The number of items to selet
	 * @param rng
	 *            A function that creates a random number from 0 to the
	 *            desired number
	 * @return A new list containing the desired number of items randomly
	 *         selected from the specified list
	 */
	public static <E> IFunctionalList<E> drawWithReplacement(
			IFunctionalList<E> list, int numberOfItems,
			Function<Integer, Integer> rng) {
		IFunctionalList<E> selectedItems = new FunctionalList<>(
				new ArrayList<>(numberOfItems));

		for (int i = 0; i < numberOfItems; i++) {
			selectedItems.add(list.randItem(rng));
		}

		return selectedItems;
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
	 * @param elementCounter
	 *            The function to determine the count for each element for
	 * @param numberPerPartition
	 *            The number of elements to put in each partition
	 * @return A list partitioned according to the above rules
	 */
	public static <E> IFunctionalList<IFunctionalList<E>> groupPartition(
			IFunctionalList<E> input, Function<E, Integer> elementCounter,
			int numberPerPartition) {
		if (input == null) {
			throw new NullPointerException("Input list must not be null");
		} else if (elementCounter == null) {
			throw new NullPointerException("Counter must not be null");
		} else if (numberPerPartition < 1
				|| numberPerPartition > input.getSize()) {
			throw new IllegalArgumentException(
					"" + numberPerPartition + " is not a valid"
							+ " partition size. Must be between 1 and "
							+ input.getSize());
		}

		/*
		 * List that holds our results
		 */
		IFunctionalList<IFunctionalList<E>> returnedList = new FunctionalList<>();

		/*
		 * List that holds current partition
		 */
		IHolder<IFunctionalList<E>> currentPartition = new Identity<>(
				new FunctionalList<>());
		/*
		 * List that holds elements rejected during current pass
		 */
		IFunctionalList<E> rejectedElements = new FunctionalList<>();

		/*
		 * The effective number of elements in the current partitition
		 */
		IHolder<Integer> numberInCurrentPartition = new Identity<>(0);

		/*
		 * Run up to a certain number of passes
		 */
		for (int numberOfIterations = 0; numberOfIterations < MAX_NTRIESPART
				&& !rejectedElements.isEmpty(); numberOfIterations++) {
			input.forEach(new GroupPartIteration<>(returnedList,
					currentPartition, rejectedElements,
					numberInCurrentPartition, numberPerPartition,
					elementCounter));

			if (rejectedElements.isEmpty()) {
				// Nothing was rejected, so we're done
				return returnedList;
			}
		}

		throw new IllegalArgumentException(
				"Heuristic (more than " + MAX_NTRIESPART
						+ " iterations of partitioning) detected unpartitionable list "
						+ input.toString()
						+ "\nThe following elements were not partitioned: "
						+ rejectedElements.toString()
						+ "\nCurrent group in formation: "
						+ currentPartition.unwrap((vl) -> vl.toString())
						+ "\nPreviously formed groups: "
						+ returnedList.toString());
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
	public static <E> IFunctionalList<E> mergeLists(
			IFunctionalList<E>... lists) {
		IFunctionalList<E> returnedList = new FunctionalList<>();

		for (IFunctionalList<E> list : lists) {
			list.forEach(returnedList::add);
		}

		return returnedList;
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
	public static IFunctionalList<String> splitTokens(
			IFunctionalList<String> input,
			Deque<IPair<String, String>> operators) {
		if (input == null) {
			throw new NullPointerException("Input must not be null");
		} else if (operators == null) {
			throw new NullPointerException(
					"Set of operators must not be null");
		}

		IHolder<IFunctionalList<String>> returnedList = new Identity<>(
				input);

		operators.forEach((operator) -> {
			returnedList.transform((oldReturn) -> {
				return oldReturn.flatMap((token) -> {
					return operator.merge(new TokenSplitter(token));
				});
			});
		});

		return returnedList.getValue();
	}
}