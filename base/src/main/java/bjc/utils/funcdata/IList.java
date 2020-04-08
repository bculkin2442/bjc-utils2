package bjc.utils.funcdata;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

import bjc.data.IPair;
import bjc.utils.functypes.ID;

/**
 * A wrapper over another list that provides functional operations over it.
 *
 * @author ben
 *
 * @param <ContainedType>
 *                The type in this list
 */
public interface IList<ContainedType> extends Iterable<ContainedType> {
	/**
	 * Add an item to this list.
	 *
	 * @param item
	 *                The item to add to this list.
	 *
	 * @return Whether the item was added to the list successfully..
	 */
	boolean add(ContainedType item);

	/**
	 * Add all of the elements in the provided list to this list.
	 *
	 * @param items
	 *                The list of items to add.
	 *
	 * @return True if every item was successfully added to the list, false
	 *         otherwise.
	 */
	default boolean addAll(final IList<ContainedType> items) {
		return items.map(this::add).anyMatch(bl -> bl == false);
	}

	/**
	 * Add all of the elements in the provided array to this list.
	 *
	 * @param items
	 *                The array of items to add.
	 *
	 * @return True if every item was successfully added to the list, false
	 *         otherwise.
	 */
	@SuppressWarnings("unchecked")
	default boolean addAll(final ContainedType... items) {
		boolean succ = true;

		for (final ContainedType item : items) {
			final boolean addSucc = add(item);

			succ = succ ? addSucc : false;
		}

		return succ;
	}

	/**
	 * Check if all of the elements of this list match the specified
	 * predicate.
	 *
	 * @param matcher
	 *                The predicate to use for checking.
	 *
	 * @return Whether all of the elements of the list match the specified
	 *         predicate.
	 */
	boolean allMatch(Predicate<ContainedType> matcher);

	/**
	 * Check if any of the elements in this list match the specified list.
	 *
	 * @param matcher
	 *                The predicate to use for checking.
	 *
	 * @return Whether any element in the list matches the provided
	 *         predicate.
	 */
	boolean anyMatch(Predicate<ContainedType> matcher);

	/**
	 * Reduce the contents of this list using a collector.
	 *
	 * @param <StateType>
	 *                The intermediate accumulation type.
	 *
	 * @param <ReducedType>
	 *                The final, reduced type.
	 *
	 * @param collector
	 *                The collector to use for reduction.
	 *
	 * @return The reduced list.
	 */
	default <StateType, ReducedType> ReducedType collect(
			final Collector<ContainedType, StateType, ReducedType> collector) {
		final BiConsumer<StateType, ContainedType> accumulator = collector.accumulator();

		final StateType initial = collector.supplier().get();
		return reduceAux(initial, (value, state) -> {
			accumulator.accept(state, value);

			return state;
		}, collector.finisher());
	}

	/**
	 * Combine this list with another one into a new list and merge the
	 * results.
	 *
	 * Works sort of like a combined zip/map over resulting pairs. Does not
	 * change the underlying list.
	 *
	 * NOTE: The returned list will have the length of the shorter of this
	 * list and the combined one.
	 *
	 * @param <OtherType>
	 *                The type of the second list.
	 *
	 * @param <CombinedType>
	 *                The type of the combined list.
	 *
	 * @param list
	 *                The list to combine with.
	 *
	 * @param combiner
	 *                The function to use for combining element pairs.
	 *
	 * @return A new list containing the merged pairs of lists.
	 */
	<OtherType, CombinedType> IList<CombinedType> combineWith(IList<OtherType> list,
			BiFunction<ContainedType, OtherType, CombinedType> combiner);

	/**
	 * Check if the list contains the specified item.
	 *
	 * @param item
	 *                The item to see if it is contained.
	 *
	 * @return Whether or not the specified item is in the list.
	 */
	boolean contains(ContainedType item);

	/**
	 * Get the first element in the list.
	 *
	 * @return The first element in this list.
	 */
	ContainedType first();

	/**
	 * Get the last element in the list.
	 *
	 * @return The last element in this list.
	 */
	ContainedType last();

	/**
	 * Remove and return the first element from the list.
	 * 
	 * @return The first element from the list.
	 */
	ContainedType popFirst();

	/**
	 * Remove and return the last element from the list.
	 * 
	 * @return The last element from the list.
	 */
	ContainedType popLast();

	/**
	 * Apply a function to each member of the list, then flatten the
	 * results.
	 *
	 * Does not change the underlying list.
	 *
	 * @param <MappedType>
	 *                The type of the flattened list.
	 *
	 * @param expander
	 *                The function to apply to each member of the list.
	 *
	 * @return A new list containing the flattened results of applying the
	 *         provided function.
	 */
	<MappedType> IList<MappedType> flatMap(Function<ContainedType, IList<MappedType>> expander);

	/**
	 * Apply a given action for each member of the list.
	 *
	 * @param action
	 *                The action to apply to each member of the list.
	 */
	@Override
	void forEach(Consumer<? super ContainedType> action);

	/**
	 * Apply a given function to each element in the list and its index.
	 *
	 * @param action
	 *                The function to apply to each element in the list and
	 *                its index.
	 */
	void forEachIndexed(BiConsumer<Integer, ContainedType> action);

	/**
	 * Retrieve a value in the list by its index.
	 *
	 * @param index
	 *                The index to retrieve a value from.
	 *
	 * @return The value at the specified index in the list.
	 */
	ContainedType getByIndex(int index);

	/**
	 * Retrieve a list containing all elements matching a predicate.
	 *
	 * @param predicate
	 *                The predicate to match by.
	 *
	 * @return A list containing all elements that match the predicate.
	 */
	IList<ContainedType> getMatching(Predicate<ContainedType> predicate);

	/**
	 * Retrieve the size of the wrapped list.
	 *
	 * @return The size of the wrapped list.
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
	 * the list.
	 *
	 * Does not change the underlying list.
	 *
	 * @param <MappedType>
	 *                The type of the transformed list.
	 *
	 * @param transformer
	 *                The function to apply to each element in the list.
	 *
	 * @return A new list containing the mapped elements of this list.
	 */
	<MappedType> IList<MappedType> map(Function<ContainedType, MappedType> transformer);

	/**
	 * Zip two lists into a list of pairs.
	 *
	 * @param <OtherType>
	 *                The type of the second list.
	 *
	 * @param list
	 *                The list to use as the left side of the pair.
	 *
	 * @return A list containing pairs of this element and the specified
	 *         list.
	 */
	<OtherType> IList<IPair<ContainedType, OtherType>> pairWith(IList<OtherType> list);

	/**
	 * Partition this list into a list of sublists.
	 *
	 * @param partitionSize
	 *                The size of elements to put into each one of the
	 *                sublists.
	 * 
	 * @return A list partitioned into partitions of size partitionSize. The
	 *         last partition may not be completely full if the size of the
	 *         list is not a multiple of partitionSize.
	 */
	IList<IList<ContainedType>> partition(int partitionSize);

	/**
	 * Prepend an item to the list.
	 *
	 * @param item
	 *                The item to prepend to the list.
	 */
	void prepend(ContainedType item);

	/**
	 * Prepend an array of items to the list.
	 *
	 * @param items
	 *                The items to prepend to the list.
	 */
	@SuppressWarnings("unchecked")
	default void prependAll(final ContainedType... items) {
		for (final ContainedType item : items) {
			prepend(item);
		}
	}

	/**
	 * Select a random item from the list, using a default random number
	 * generator.
	 *
	 * @return A random item from the list
	 */
	default ContainedType randItem() {
		return randItem(num -> (int) (Math.random() * num));
	}

	/**
	 * Select a random item from this list, using the provided random number
	 * generator.
	 *
	 * @param rnd
	 *                The random number generator to use.
	 *
	 * @return A random element from this list.
	 */
	ContainedType randItem(Function<Integer, Integer> rnd);

	/**
	 * Reduce this list to a single value, using a accumulative approach.
	 *
	 * @param <StateType>
	 *                The in-between type of the values
	 *
	 * @param <ReducedType>
	 *                The final value type
	 *
	 * @param initial
	 *                The initial value of the accumulative state.
	 *
	 * @param accumulator
	 *                The function to use to combine a list element with the
	 *                accumulative state.
	 *
	 * @param transformer
	 *                The function to use to convert the accumulative state
	 *                into a final result.
	 *
	 * @return A single value condensed from this list and transformed into
	 *         its final state.
	 */
	<StateType, ReducedType> ReducedType reduceAux(StateType initial,
			BiFunction<ContainedType, StateType, StateType> accumulator,
			Function<StateType, ReducedType> transformer);

	/**
	 * Reduce this list to a single value, using a accumulative approach.
	 *
	 * @param <StateType>
	 *                The in-between type of the values.
	 *
	 * @param initial
	 *                The initial value of the accumulative state.
	 * 
	 * @param accumulator
	 *                The function to use to combine a list element with the
	 *                accumulative state.
	 * 
	 * @return A single value condensed from this list.
	 */
	default <StateType> StateType reduceAux(StateType initial,
			BiFunction<ContainedType, StateType, StateType> accumulator) {
		return reduceAux(initial, accumulator, ID.id());
	}

	/**
	 * Remove all elements that match a given predicate.
	 *
	 * @param predicate
	 *                The predicate to use to determine elements to delete.
	 *
	 * @return Whether there was anything that satisfied the predicate.
	 */
	boolean removeIf(Predicate<ContainedType> predicate);

	/**
	 * Remove all parameters that match a given parameter.
	 *
	 * @param element
	 *                The object to remove all matching copies of.
	 */
	void removeMatching(ContainedType element);

	/** Reverse the contents of this list in place. */
	void reverse();

	/**
	 * Perform a binary search for the specified key using the provided
	 * means of comparing elements.
	 *
	 * Since this IS a binary search, the list must have been sorted before
	 * hand.
	 *
	 * @param key
	 *                The key to search for.
	 *
	 * @param comparator
	 *                The way to compare elements for searching. Pass null
	 *                to use the natural ordering for E.
	 *
	 * @return The element if it is in this list, or null if it is not.
	 */
	ContainedType search(ContainedType key, Comparator<ContainedType> comparator);

	/**
	 * Sort the elements of this list using the provided way of comparing
	 * elements.
	 *
	 * Does change the underlying list.
	 *
	 * @param comparator
	 *                The way to compare elements for sorting. Pass null to
	 *                use E's natural ordering
	 */
	void sort(Comparator<ContainedType> comparator);

	/**
	 * Get the tail of this list (the list without the first element).
	 *
	 * @return The list without the first element.
	 */
	IList<ContainedType> tail();

	/**
	 * Convert this list into an array.
	 *
	 * @param type
	 *                The type of array to return.
	 *
	 * @return The list, as an array.
	 */
	ContainedType[] toArray(ContainedType[] type);

	/**
	 * Convert the list into a Iterable.
	 *
	 * @return An iterable view onto the list.
	 */
	Iterable<ContainedType> toIterable();

	@Override
	default Iterator<ContainedType> iterator() {
		return toIterable().iterator();
	}
}
