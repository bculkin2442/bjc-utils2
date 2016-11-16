package bjc.utils.funcdata;

import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

import bjc.utils.data.IPair;

/**
 * A wrapper over another list that provides functional operations over it.
 * 
 * @author ben
 *
 * @param <ContainedType>
 *            The type in this list
 */
public interface IList<ContainedType> {
	/**
	 * Add an item to this list
	 * 
	 * @param item
	 *            The item to add to this list.
	 * @return Whether the item was added to the list succesfully.
	 */
	boolean add(ContainedType item);

	default boolean addAll(IList<ContainedType> items) {
		return items.map(this::add).anyMatch((bl) -> bl == false);
	}

	/**
	 * Check if all of the elements of this list match the specified
	 * predicate.
	 * 
	 * @param matchPredicate
	 *            The predicate to use for checking.
	 * @return Whether all of the elements of the list match the specified
	 *         predicate.
	 */
	boolean allMatch(Predicate<ContainedType> matchPredicate);

	/**
	 * Check if any of the elements in this list match the specified list.
	 * 
	 * @param matchPredicate
	 *            The predicate to use for checking.
	 * @return Whether any element in the list matches the provided
	 *         predicate.
	 */
	boolean anyMatch(Predicate<ContainedType> matchPredicate);

	/**
	 * Reduce the contents of this list using a collector
	 * 
	 * @param <StateType>
	 *            The intermediate accumulation type
	 * @param <ReducedType>
	 *            The final, reduced type
	 * @param collector
	 *            The collector to use for reduction
	 * @return The reduced list
	 */
	public default <StateType, ReducedType> ReducedType collect(
			Collector<ContainedType, StateType, ReducedType> collector) {
		BiConsumer<StateType, ContainedType> accumulator = collector
				.accumulator();

		return reduceAux(collector.supplier().get(), (value, state) -> {
			accumulator.accept(state, value);

			return state;
		}, collector.finisher());
	}

	/**
	 * Combine this list with another one into a new list and merge the
	 * results. Works sort of like a combined zip/map over resulting pairs.
	 * Does not change the underlying list.
	 * 
	 * NOTE: The returned list will have the length of the shorter of this
	 * list and the combined one.
	 * 
	 * @param <OtherType>
	 *            The type of the second list
	 * @param <CombinedType>
	 *            The type of the combined list
	 * 
	 * @param rightList
	 *            The list to combine with
	 * @param itemCombiner
	 *            The function to use for combining element pairs.
	 * @return A new list containing the merged pairs of lists.
	 */
	<OtherType, CombinedType> IList<CombinedType> combineWith(
			IList<OtherType> rightList,
			BiFunction<ContainedType, OtherType, CombinedType> itemCombiner);

	/**
	 * Check if the list contains the specified item
	 * 
	 * @param item
	 *            The item to see if it is contained
	 * @return Whether or not the specified item is in the list
	 */
	boolean contains(ContainedType item);

	/**
	 * Get the first element in the list
	 * 
	 * @return The first element in this list.
	 */
	ContainedType first();

	/**
	 * Apply a function to each member of the list, then flatten the
	 * results. Does not change the underlying list.
	 * 
	 * @param <MappedType>
	 *            The type of the flattened list
	 * 
	 * @param elementExpander
	 *            The function to apply to each member of the list.
	 * @return A new list containing the flattened results of applying the
	 *         provided function.
	 */
	<MappedType> IList<MappedType> flatMap(
			Function<ContainedType, IList<MappedType>> elementExpander);

	/**
	 * Apply a given action for each member of the list
	 * 
	 * @param action
	 *            The action to apply to each member of the list.
	 */
	void forEach(Consumer<ContainedType> action);

	/**
	 * Apply a given function to each element in the list and its index.
	 * 
	 * @param indexedAction
	 *            The function to apply to each element in the list and its
	 *            index.
	 */
	void forEachIndexed(BiConsumer<Integer, ContainedType> indexedAction);

	/**
	 * Retrieve a value in the list by its index.
	 * 
	 * @param index
	 *            The index to retrieve a value from.
	 * @return The value at the specified index in the list.
	 */
	ContainedType getByIndex(int index);

	/**
	 * Retrieve a list containing all elements matching a predicate
	 * 
	 * @param matchPredicate
	 *            The predicate to match by
	 * @return A list containing all elements that match the predicate
	 */
	IList<ContainedType> getMatching(
			Predicate<ContainedType> matchPredicate);

	/**
	 * Retrieve the size of the wrapped list
	 * 
	 * @return The size of the wrapped list
	 */
	int getSize();

	/**
	 * Check if this list is empty.
	 * 
	 * @return Whether or not this list is empty.
	 */
	boolean isEmpty();

	/**
	 * Create a new list by applying the given function to each element in
	 * the list. Does not change the underlying list.
	 * 
	 * @param <MappedType>
	 *            The type of the transformed list
	 * 
	 * @param elementTransformer
	 *            The function to apply to each element in the list
	 * @return A new list containing the mapped elements of this list.
	 */
	<MappedType> IList<MappedType> map(
			Function<ContainedType, MappedType> elementTransformer);

	/**
	 * Zip two lists into a list of pairs
	 * 
	 * @param <OtherType>
	 *            The type of the second list
	 * 
	 * @param rightList
	 *            The list to use as the left side of the pair
	 * @return A list containing pairs of this element and the specified
	 *         list
	 */
	<OtherType> IList<IPair<ContainedType, OtherType>> pairWith(
			IList<OtherType> rightList);

	/**
	 * Partition this list into a list of sublists
	 * 
	 * @param numberPerPartition
	 *            The size of elements to put into each one of the sublists
	 * @return A list partitioned into partitions of size nPerPart
	 */
	IList<IList<ContainedType>> partition(int numberPerPartition);

	/**
	 * Prepend an item to the list
	 * 
	 * @param item
	 *            The item to prepend to the list
	 */
	void prepend(ContainedType item);

	/**
	 * Select a random item from the list, using a default random number
	 * generator
	 * 
	 * @return A random item from the list
	 */
	default ContainedType randItem() {
		return randItem((num) -> (int) (Math.random() * num));
	}

	/**
	 * Select a random item from this list, using the provided random
	 * number generator.
	 * 
	 * @param rnd
	 *            The random number generator to use.
	 * @return A random element from this list.
	 */
	ContainedType randItem(Function<Integer, Integer> rnd);

	/**
	 * Reduce this list to a single value, using a accumulative approach.
	 * 
	 * @param <StateType>
	 *            The in-between type of the values
	 * @param <ReducedType>
	 *            The final value type
	 * 
	 * @param initialValue
	 *            The initial value of the accumulative state.
	 * @param stateAccumulator
	 *            The function to use to combine a list element with the
	 *            accumulative state.
	 * @param resultTransformer
	 *            The function to use to convert the accumulative state
	 *            into a final result.
	 * @return A single value condensed from this list and transformed into
	 *         its final state.
	 */
	<StateType, ReducedType> ReducedType reduceAux(StateType initialValue,
			BiFunction<ContainedType, StateType, StateType> stateAccumulator,
			Function<StateType, ReducedType> resultTransformer);

	/**
	 * Remove all elements that match a given predicate
	 * 
	 * @param removePredicate
	 *            The predicate to use to determine elements to delete
	 * @return Whether there was anything that satisfied the predicate
	 */
	boolean removeIf(Predicate<ContainedType> removePredicate);

	/**
	 * Remove all parameters that match a given parameter
	 * 
	 * @param desiredElement
	 *            The object to remove all matching copies of
	 */
	void removeMatching(ContainedType desiredElement);

	/**
	 * Reverse the contents of this list in place
	 */
	void reverse();

	/**
	 * Perform a binary search for the specified key using the provided
	 * means of comparing elements. Since this IS a binary search, the list
	 * must have been sorted before hand.
	 * 
	 * @param searchKey
	 *            The key to search for.
	 * @param comparator
	 *            The way to compare elements for searching. Pass null to
	 *            use the natural ordering for E
	 * @return The element if it is in this list, or null if it is not.
	 */
	ContainedType search(ContainedType searchKey,
			Comparator<ContainedType> comparator);

	/**
	 * Sort the elements of this list using the provided way of comparing
	 * elements. Does change the underlying list.
	 * 
	 * @param comparator
	 *            The way to compare elements for sorting. Pass null to use
	 *            E's natural ordering
	 */
	void sort(Comparator<ContainedType> comparator);

	/**
	 * Get the tail of this list (the list without the first element
	 * 
	 * @return The list without the first element
	 */
	public IList<ContainedType> tail();

	/**
	 * Convert this list into an array
	 * 
	 * @param arrType
	 *            The type of array to return
	 * @return The list, as an array
	 */
	public ContainedType[] toArray(ContainedType[] arrType);

	/**
	 * Convert the list into a iterable
	 * 
	 * @return An iterable view onto the list
	 */
	public Iterable<ContainedType> toIterable();
}