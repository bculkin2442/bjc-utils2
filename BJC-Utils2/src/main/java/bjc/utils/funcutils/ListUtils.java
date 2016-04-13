package bjc.utils.funcutils;

import java.util.ArrayList;
import java.util.Deque;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import bjc.utils.data.GenHolder;
import bjc.utils.data.IPair;
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

	private static final class TokenDeaffixer implements
			BiFunction<String, String, IFunctionalList<String>> {
		private String token;

		public TokenDeaffixer(String tok) {
			token = tok;
		}

		@Override
		public IFunctionalList<String> apply(String operatorName,
				String operatorRegex) {
			if (operatorName == null) {
				throw new NullPointerException(
						"Operator name must not be null");
			} else if (operatorRegex == null) {
				throw new NullPointerException(
						"Operator regex must not be null");
			}

			if (StringUtils.containsOnly(token, operatorRegex)) {
				return new FunctionalList<>(token);
			} else if (token.startsWith(operatorName)) {
				return new FunctionalList<>(operatorName,
						token.split(operatorRegex)[1]);
			} else if (token.endsWith(operatorName)) {
				return new FunctionalList<>(token.split(operatorRegex)[0],
						operatorName);
			} else {
				return new FunctionalList<>(token);
			}
		}
	}

	private static final class TokenSplitter implements
			BiFunction<String, String, IFunctionalList<String>> {
		private String tokenToSplit;

		public TokenSplitter(String tok) {
			this.tokenToSplit = tok;
		}

		@Override
		public IFunctionalList<String> apply(String operatorName,
				String operatorRegex) {
			if (operatorName == null) {
				throw new NullPointerException(
						"Operator name must not be null");
			} else if (operatorRegex == null) {
				throw new NullPointerException(
						"Operator regex must not be null");
			}

			if (tokenToSplit.contains(operatorName)) {
				if (StringUtils.containsOnly(tokenToSplit,
						operatorRegex)) {
					return new FunctionalList<>(tokenToSplit);
				}

				IFunctionalList<String> splitTokens = new FunctionalList<>(
						tokenToSplit.split(operatorRegex));

				IFunctionalList<String> result = new FunctionalList<>();

				int tokenExpansionSize = splitTokens.getSize();

				splitTokens.forEachIndexed((tokenIndex, token) -> {

					if (tokenIndex != tokenExpansionSize
							&& tokenIndex != 0) {
						result.add(operatorName);
						result.add(token);
					} else {
						result.add(token);
					}
				});

				return result;
			}

			return new FunctionalList<>(tokenToSplit);
		}
	}

	/**
	 * Implements a single group partitioning pass on a list
	 * 
	 * @author ben
	 *
	 * @param <E>
	 *            The type of element in the list being partitioned
	 */
	private static final class GroupPartIteration<E>
			implements Consumer<E> {
		private IFunctionalList<IFunctionalList<E>>	returnedList;
		private GenHolder<IFunctionalList<E>>		currentPartition;
		private IFunctionalList<E>					rejectedItems;
		private GenHolder<Integer>					numberInCurrentPartition;
		private int									numberPerPartition;
		private Function<E, Integer>				elementCounter;

		public GroupPartIteration(
				IFunctionalList<IFunctionalList<E>> returned,
				GenHolder<IFunctionalList<E>> currPart,
				IFunctionalList<E> rejects,
				GenHolder<Integer> numInCurrPart, int nPerPart,
				Function<E, Integer> eleCount) {
			this.returnedList = returned;
			this.currentPartition = currPart;
			this.rejectedItems = rejects;
			this.numberInCurrentPartition = numInCurrPart;
			this.numberPerPartition = nPerPart;
			this.elementCounter = eleCount;
		}

		@Override
		public void accept(E value) {
			if (numberInCurrentPartition
					.unwrap((number) -> number >= numberPerPartition)) {
				returnedList.add(
						currentPartition.unwrap((partition) -> partition));

				currentPartition
						.transform((partition) -> new FunctionalList<>());
				numberInCurrentPartition.transform((number) -> 0);
			} else {
				int currentElementCount = elementCounter.apply(value);

				if (numberInCurrentPartition.unwrap((number) -> (number
						+ currentElementCount) >= numberPerPartition)) {
					rejectedItems.add(value);
				} else {
					currentPartition
							.unwrap((partition) -> partition.add(value));
					numberInCurrentPartition.transform(
							(number) -> number + currentElementCount);
				}
			}
		}
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
		GenHolder<IFunctionalList<E>> currentPartition = new GenHolder<>(
				new FunctionalList<>());
		/*
		 * List that holds elements rejected during current pass
		 */
		IFunctionalList<E> rejectedElements = new FunctionalList<>();

		/*
		 * The effective number of elements in the current partitition
		 */
		GenHolder<Integer> numberInCurrentPartition = new GenHolder<>(0);

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

		GenHolder<IFunctionalList<String>> returnedList = new GenHolder<>(
				input);

		operators.forEach((operator) -> returnedList
				.transform((oldReturn) -> oldReturn.flatMap((token) -> {
					return operator.merge(new TokenSplitter(token));
				})));

		return returnedList.unwrap((list) -> list);
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

		GenHolder<IFunctionalList<String>> returnedList = new GenHolder<>(
				input);

		operators.forEach((operator) -> returnedList
				.transform((oldReturn) -> oldReturn.flatMap((token) -> {
					return operator.merge(new TokenDeaffixer(token));
				})));

		return returnedList.unwrap((list) -> list);
	}

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
}