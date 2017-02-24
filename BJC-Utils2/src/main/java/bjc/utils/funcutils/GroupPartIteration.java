package bjc.utils.funcutils;

import java.util.function.Consumer;
import java.util.function.Function;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

/**
 * Implements a single group partitioning pass on a list
 * 
 * @author ben
 *
 * @param <E>
 *            The type of element in the list being partitioned
 */
final class GroupPartIteration<E> implements Consumer<E> {
	private IList<IList<E>>			returnedList;

	public IList<E>		currentPartition;
	private IList<E>				rejectedItems;

	private int						numberInCurrentPartition;
	private int						numberPerPartition;

	private Function<E, Integer>	elementCounter;

	public GroupPartIteration(IList<IList<E>> returned, IList<E> rejects,
			int nPerPart, Function<E, Integer> eleCount) {
		this.returnedList = returned;
		this.rejectedItems = rejects;
		this.numberPerPartition = nPerPart;
		this.elementCounter = eleCount;

		this.currentPartition = new FunctionalList<>();
		this.numberInCurrentPartition = 0;
	}

	@Override
	public void accept(E value) {
		boolean shouldStartPartition = numberInCurrentPartition >= numberPerPartition;

		if (shouldStartPartition) {
			returnedList.add(currentPartition);

			currentPartition = new FunctionalList<>();
			numberInCurrentPartition = 0;
		} else {
			int currentElementCount = elementCounter.apply(value);

			boolean shouldReject = (numberInCurrentPartition + currentElementCount) >= numberPerPartition;
			
			if (shouldReject) {
				rejectedItems.add(value);
			} else {
				currentPartition.add(value);
				numberInCurrentPartition += currentElementCount;
			}
		}
	}
}
