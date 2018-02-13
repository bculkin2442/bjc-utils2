package bjc.utils.funcutils;

import java.util.function.Consumer;
import java.util.function.Function;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

/**
 * Implements a single group partitioning pass on a list.
 *
 * @author ben
 *
 * @param <E>
 *        The type of element in the list being partitioned
 */
final class GroupPartIteration<E> implements Consumer<E> {
	/* The list we're returning. */
	private final IList<IList<E>> returnedList;

	/* The current partition of the list. */
	public IList<E> currentPartition;
	/* The items rejected from the current partition. */
	private final IList<E> rejectedItems;

	/* The number of items in the current partition. */
	private int numberInCurrentPartition;
	/* The number of items in each partition. */
	private final int numberPerPartition;

	/* The function to use to count an item. */
	private final Function<E, Integer> elementCounter;

	/**
	 * Create a new group partitioning iteration.
	 *
	 * @param returned
	 *        The list containing all of the existing partitions.
	 *
	 * @param rejects
	 *        The items that have been rejected from a partition for being
	 *        too large.
	 *
	 * @param nPerPart
	 *        The combined value of items that should go into each
	 *        partition.
	 *
	 * @param eleCount
	 *        The function to use to determine the value of an item.
	 */
	public GroupPartIteration(final IList<IList<E>> returned, final IList<E> rejects, final int nPerPart,
			final Function<E, Integer> eleCount) {
		this.returnedList = returned;
		this.rejectedItems = rejects;
		this.numberPerPartition = nPerPart;
		this.elementCounter = eleCount;

		this.currentPartition = new FunctionalList<>();
		this.numberInCurrentPartition = 0;
	}

	@Override
	public void accept(final E value) {
		final boolean shouldStartPartition = numberInCurrentPartition >= numberPerPartition;

		if(shouldStartPartition) {
			returnedList.add(currentPartition);

			currentPartition = new FunctionalList<>();
			numberInCurrentPartition = 0;
		} else {
			final int currentElementCount = elementCounter.apply(value);

			final boolean shouldReject = (numberInCurrentPartition
					+ currentElementCount) >= numberPerPartition;

			if(shouldReject) {
				rejectedItems.add(value);
			} else {
				currentPartition.add(value);
				numberInCurrentPartition += currentElementCount;
			}
		}
	}
}
