package bjc.utils.funcutils;

import java.util.Deque;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import bjc.utils.data.GenHolder;
import bjc.utils.data.Pair;
import bjc.utils.funcdata.FunctionalList;

/**
 * Utilities for manipulating FunctionalLists that don't belong in the
 * class itself
 * 
 * @author ben
 *
 */
public class ListUtils {
	private static final int MAX_NTRIESPART = 50;

	private static final class TokenDeaffixer
			implements BiFunction<String, String, FunctionalList<String>> {
		private String token;

		public TokenDeaffixer(String tok) {
			token = tok;
		}

		@Override
		public FunctionalList<String> apply(String opName,
				String opRegex) {
			if (StringUtils.containsOnly(token, opRegex)) {
				return new FunctionalList<>(token);
			} else if (token.startsWith(opName)) {
				return new FunctionalList<>(opName,
						token.split(opRegex)[1]);
			} else if (token.endsWith(opName)) {
				return new FunctionalList<>(token.split(opRegex)[0],
						opName);
			} else {
				return new FunctionalList<>(token);
			}
		}
	}

	private static final class TokenSplitter
			implements BiFunction<String, String, FunctionalList<String>> {
		private String tokenToSplit;

		public TokenSplitter(String tok) {
			this.tokenToSplit = tok;
		}

		@Override
		public FunctionalList<String> apply(String operatorName,
				String operatorRegex) {
			if (tokenToSplit.contains(operatorName)) {
				if (StringUtils.containsOnly(tokenToSplit,
						operatorRegex)) {
					return new FunctionalList<>(tokenToSplit);
				} else {
					FunctionalList<String> splitTokens = new FunctionalList<>(
							tokenToSplit.split(operatorRegex));

					FunctionalList<String> result = new FunctionalList<>();

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
			} else {
				return new FunctionalList<>(tokenToSplit);
			}
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
		private FunctionalList<FunctionalList<E>>	returnedList;
		private GenHolder<FunctionalList<E>>		currentPartition;
		private FunctionalList<E>					rejectedItems;
		private GenHolder<Integer>					numberInCurrentPartition;
		private int									numberPerPartition;
		private Function<E, Integer>				elementCounter;

		public GroupPartIteration(
				FunctionalList<FunctionalList<E>> returned,
				GenHolder<FunctionalList<E>> currPart,
				FunctionalList<E> rejects,
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
	public static <E> FunctionalList<FunctionalList<E>> groupPartition(
			FunctionalList<E> input, Function<E, Integer> elementCounter,
			int numberPerPartition) {
		/*
		 * List that holds our results
		 */
		FunctionalList<FunctionalList<E>> returnedList = new FunctionalList<>();

		/*
		 * List that holds current partition
		 */
		GenHolder<FunctionalList<E>> currentPartition = new GenHolder<>(
				new FunctionalList<>());
		/*
		 * List that holds elements rejected during current pass
		 */
		FunctionalList<E> rejectedElements = new FunctionalList<>();

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
	public static FunctionalList<String> splitTokens(
			FunctionalList<String> input,
			Deque<Pair<String, String>> operators) {
		GenHolder<FunctionalList<String>> ret = new GenHolder<>(input);

		operators.forEach(
				(op) -> ret.transform((oldRet) -> oldRet.flatMap((tok) -> {
					return op.merge(new TokenSplitter(tok));
				})));

		return ret.unwrap((list) -> list);
	}

	/**
	 * Split off affixes from tokens
	 * 
	 * @param input
	 *            The tokens to deaffix
	 * @param ops
	 *            The affixes to remove
	 * @return The tokens that have been deaffixed
	 * 
	 */
	public static FunctionalList<String> deAffixTokens(
			FunctionalList<String> input,
			Deque<Pair<String, String>> ops) {
		GenHolder<FunctionalList<String>> returnedList = new GenHolder<>(
				input);

		ops.forEach((op) -> returnedList
				.transform((oldRet) -> oldRet.flatMap((tok) -> {
					return op.merge(new TokenDeaffixer(tok));
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
	public static String collapseTokens(FunctionalList<String> input) {
		return collapseTokens(input, "");
	}

	/**
	 * Collapse a string of tokens into a single string, adding the desired
	 * seperator after each token
	 * 
	 * @param input
	 *            The list of tokens to collapse
	 * @param sep
	 *            The seperator to use for seperating tokens
	 * @return The collapsed string of tokens
	 */
	public static String collapseTokens(FunctionalList<String> input,
			String sep) {
		if (input.getSize() < 1) {
			return "";
		} else if (input.getSize() == 1) {
			return input.first();
		} else {
			return input.reduceAux("",
					(currentString, state) -> state + currentString + sep,
					(strang) -> {
						return strang.substring(0,
								strang.length() - sep.length());
					});
		}
	}
}