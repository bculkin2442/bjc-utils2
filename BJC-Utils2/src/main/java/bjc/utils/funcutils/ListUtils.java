package bjc.utils.funcutils;

import java.util.Deque;
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
		private FunctionalList<FunctionalList<E>>	ret;
		private GenHolder<FunctionalList<E>>		currPart;
		private FunctionalList<E>					rejects;
		private GenHolder<Integer>					numInCurrPart;
		private int									nPerPart;
		private Function<E, Integer>				eleCount;

		public GroupPartIteration(FunctionalList<FunctionalList<E>> ret,
				GenHolder<FunctionalList<E>> currPart,
				FunctionalList<E> rejects,
				GenHolder<Integer> numInCurrPart, int nPerPart,
				Function<E, Integer> eleCount) {
			this.ret = ret;
			this.currPart = currPart;
			this.rejects = rejects;
			this.numInCurrPart = numInCurrPart;
			this.nPerPart = nPerPart;
			this.eleCount = eleCount;
		}

		@Override
		public void accept(E val) {
			if (numInCurrPart.unwrap((vl) -> vl >= nPerPart)) {
				ret.add(currPart.unwrap((vl) -> vl));

				currPart.transform((vl) -> new FunctionalList<>());
				numInCurrPart.transform((vl) -> 0);
			} else {
				int vCount = eleCount.apply(val);

				if (numInCurrPart
						.unwrap((vl) -> (vl + vCount) >= nPerPart)) {
					rejects.add(val);
				} else {
					currPart.unwrap((vl) -> vl.add(val));
					numInCurrPart.transform((vl) -> vl + vCount);
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
	 * @param list
	 *            The list to partition
	 * @param eleCount
	 *            The function to determine the count for each element for
	 * @param nPerPart
	 *            The number of elements to put in each partition
	 * @return A list partitioned according to the above rules
	 */
	public static <E> FunctionalList<FunctionalList<E>> groupPartition(
			FunctionalList<E> list, Function<E, Integer> eleCount,
			int nPerPart) {
		/*
		 * List that holds our results
		 */
		FunctionalList<FunctionalList<E>> ret = new FunctionalList<>();

		/*
		 * List that holds current partition
		 */
		GenHolder<FunctionalList<E>> currPart =
				new GenHolder<>(new FunctionalList<>());
		/*
		 * List that holds elements rejected during current pass
		 */
		FunctionalList<E> rejects = new FunctionalList<>();

		/*
		 * The effective number of elements in the current partitition
		 */
		GenHolder<Integer> numInCurrPart = new GenHolder<>(0);

		/*
		 * Run up to a certain number of passes
		 */
		for (int nIterations = 0; nIterations < MAX_NTRIESPART
				&& !rejects.isEmpty(); nIterations++) {
			list.forEach(new GroupPartIteration<>(ret, currPart, rejects,
					numInCurrPart, nPerPart, eleCount));

			if (rejects.isEmpty()) {
				// Nothing was rejected, so we're done
				return ret;
			}
		}

		throw new IllegalArgumentException("Heuristic (more than "
				+ MAX_NTRIESPART
				+ " iterations of partitioning) detected unpartitionable list "
				+ list.toString()
				+ "\nThe following elements were not partitioned: "
				+ rejects.toString() + "\nCurrent group in formation: "
				+ currPart.unwrap((vl) -> vl.toString())
				+ "\nPreviously formed groups: " + ret.toString());
	}

	/**
	 * Split tokens in a list of tokens into multiple tokens.
	 * 
	 * The intended use is for expression parsers so that you can enter
	 * something like 1+1 instead of 1 + 1.
	 * 
	 * @param input
	 *            The tokens to split
	 * @param ops
	 *            The operators to split on.
	 * @return A list of tokens split on all the operators
	 */
	public static FunctionalList<String> splitTokens(
			FunctionalList<String> input,
			Deque<Pair<String, String>> ops) {
		GenHolder<FunctionalList<String>> ret = new GenHolder<>(input);

		ops.forEach(
				(op) -> ret.transform((oldRet) -> oldRet.flatMap((tok) -> {
					return op.merge((opName, opRegex) -> {
						if (tok.contains(opName)) {
							FunctionalList<String> splitTokens =
									new FunctionalList<>(
											tok.split(opRegex));

							FunctionalList<String> rt =
									new FunctionalList<>();

							int tkSize = splitTokens.getSize();
							splitTokens.forEachIndexed((idx, tk) -> {

								if (idx != tkSize && idx != 0) {
									rt.add(opName);
									rt.add(tk);

								} else {
									rt.add(tk);

								}
							});

							return rt;
						} else {
							return new FunctionalList<>(tok);
						}
					});
				})));

		return ret.unwrap((l) -> l);
	}

	/**
	 * Split off affixes from tokens
	 * 
	 * @param input
	 *            The tokens to deaffix
	 * @param ops
	 *            The affixes to remove
	 * @return The tokens that have been deaffixed
	 */
	@SuppressWarnings("unchecked")
	public static FunctionalList<String> deAffixTokens(
			FunctionalList<String> input,
			Deque<Pair<String, String>> ops) {
		GenHolder<FunctionalList<String>> ret = new GenHolder<>(input);

		ops.forEach(
				(op) -> ret.transform((oldRet) -> oldRet.flatMap((tok) -> {
					return (FunctionalList<String>) op
							.merge((opName, opRegex) -> {
						if (tok.startsWith(opName)) {
							return new FunctionalList<>(op,
									tok.split(opRegex)[1]);
						} else if (tok.endsWith(opName)) {
							return new FunctionalList<>(
									tok.split(opRegex)[0], op);
						} else {
							return new FunctionalList<>(tok);
						}
					});
				})));

		return ret.unwrap((l) -> l);
	}
}
