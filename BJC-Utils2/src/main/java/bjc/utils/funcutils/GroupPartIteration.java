package bjc.utils.funcutils;

import java.util.function.Consumer;
import java.util.function.Function;

import bjc.utils.data.IHolder;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IFunctionalList;

/**
 * Implements a single group partitioning pass on a list
 * 
 * @author ben
 *
 * @param <E>
 *            The type of element in the list being partitioned
 */
final class GroupPartIteration<E> implements Consumer<E> {
	private IFunctionalList<IFunctionalList<E>>	returnedList;
	private IHolder<IFunctionalList<E>>			currentPartition;
	private IFunctionalList<E>					rejectedItems;
	private IHolder<Integer>					numberInCurrentPartition;
	private int									numberPerPartition;
	private Function<E, Integer>				elementCounter;

	public GroupPartIteration(IFunctionalList<IFunctionalList<E>> returned,
			IHolder<IFunctionalList<E>> currPart,
			IFunctionalList<E> rejects, IHolder<Integer> numInCurrPart,
			int nPerPart, Function<E, Integer> eleCount) {
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