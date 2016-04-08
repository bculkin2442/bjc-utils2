package bjc.utils.funcdata;

import java.util.Comparator;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import bjc.utils.data.IPair;

/**
 * A wrapper over another list that provides eager functional operations
 * over it. Differs from a stream in every way except for the fact that
 * they both provide functional operations.
 * 
 * @author ben
 *
 * @param <E>
 *            The type in this list
 */
public interface IFunctionalList<E> {

	/**
	 * Add an item to this list
	 * 
	 * @param item
	 *            The item to add to this list.
	 * @return Whether the item was added to the list succesfully.
	 */
	boolean add(E item);

	/**
	 * Check if all of the elements of this list match the specified
	 * predicate.
	 * 
	 * @param matchPredicate
	 *            The predicate to use for checking.
	 * @return Whether all of the elements of the list match the specified
	 *         predicate.
	 */
	boolean allMatch(Predicate<E> matchPredicate);

	/**
	 * Check if any of the elements in this list match the specified list.
	 * 
	 * @param matchPredicate
	 *            The predicate to use for checking.
	 * @return Whether any element in the list matches the provided
	 *         predicate.
	 */
	boolean anyMatch(Predicate<E> matchPredicate);

	/**
	 * Combine this list with another one into a new list and merge the
	 * results. Works sort of like a combined zip/map over resulting pairs.
	 * Does not change the underlying list.
	 * 
	 * NOTE: The returned list will have the length of the shorter of this
	 * list and the combined one.
	 * 
	 * @param <T>
	 *            The type of the second list
	 * @param <F>
	 *            The type of the combined list
	 * 
	 * @param rightList
	 *            The list to combine with
	 * @param itemCombiner
	 *            The function to use for combining element pairs.
	 * @return A new list containing the merged pairs of lists.
	 */
	<T, F> IFunctionalList<F> combineWith(IFunctionalList<T> rightList,
			BiFunction<E, T, F> itemCombiner);

	/**
	 * Check if the list contains the specified item
	 * 
	 * @param item
	 *            The item to see if it is contained
	 * @return Whether or not the specified item is in the list
	 */
	boolean contains(E item);

	/**
	 * Get the first element in the list
	 * 
	 * @return The first element in this list.
	 */
	E first();

	/**
	 * Apply a function to each member of the list, then flatten the
	 * results. Does not change the underlying list.
	 * 
	 * @param <T>
	 *            The type of the flattened list
	 * 
	 * @param elementExpander
	 *            The function to apply to each member of the list.
	 * @return A new list containing the flattened results of applying the
	 *         provided function.
	 */
	<T> IFunctionalList<T> flatMap(
			Function<E, IFunctionalList<T>> elementExpander);

	/**
	 * Apply a given action for each member of the list
	 * 
	 * @param action
	 *            The action to apply to each member of the list.
	 */
	void forEach(Consumer<E> action);

	/**
	 * Apply a given function to each element in the list and its index.
	 * 
	 * @param indexedAction
	 *            The function to apply to each element in the list and its
	 *            index.
	 */
	void forEachIndexed(BiConsumer<Integer, E> indexedAction);

	/**
	 * Retrieve a value in the list by its index.
	 * 
	 * @param index
	 *            The index to retrieve a value from.
	 * @return The value at the specified index in the list.
	 */
	E getByIndex(int index);

	/**
	 * Retrieve a list containing all elements matching a predicate
	 * 
	 * @param matchPredicate
	 *            The predicate to match by
	 * @return A list containing all elements that match the predicate
	 */
	IFunctionalList<E> getMatching(Predicate<E> matchPredicate);

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
	 * @param <T>
	 *            The type of the transformed list
	 * 
	 * @param elementTransformer
	 *            The function to apply to each element in the list
	 * @return A new list containing the mapped elements of this list.
	 */
	<T> IFunctionalList<T> map(Function<E, T> elementTransformer);

	/**
	 * Zip two lists into a list of pairs
	 * 
	 * @param <T>
	 *            The type of the second list
	 * 
	 * @param rightList
	 *            The list to use as the left side of the pair
	 * @return A list containing pairs of this element and the specified
	 *         list
	 */
	<T> IFunctionalList<IPair<E, T>> pairWith(
			IFunctionalList<T> rightList);

	/**
	 * Partition this list into a list of sublists
	 * 
	 * @param numberPerPartition
	 *            The size of elements to put into each one of the sublists
	 * @return A list partitioned into partitions of size nPerPart
	 */
	IFunctionalList<IFunctionalList<E>> partition(int numberPerPartition);

	/**
	 * Prepend an item to the list
	 * 
	 * @param item
	 *            The item to prepend to the list
	 */
	void prepend(E item);

	/**
	 * Select a random item from this list, using the provided random
	 * number generator.
	 * 
	 * @param rnd
	 *            The random number generator to use.
	 * @return A random element from this list.
	 */
	E randItem(Random rnd);

	/**
	 * Reduce this list to a single value, using a accumulative approach.
	 * 
	 * @param <T>
	 *            The in-between type of the values
	 * @param <F>
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
	<T, F> F reduceAux(T initialValue,
			BiFunction<E, T, T> stateAccumulator,
			Function<T, F> resultTransformer);

	/**
	 * Remove all elements that match a given predicate
	 * 
	 * @param removePredicate
	 *            The predicate to use to determine elements to delete
	 * @return Whether there was anything that satisfied the predicate
	 */
	boolean removeIf(Predicate<E> removePredicate);

	/**
	 * Remove all parameters that match a given parameter
	 * 
	 * @param desiredElement
	 *            The object to remove all matching copies of
	 */
	void removeMatching(E desiredElement);

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
	E search(E searchKey, Comparator<E> comparator);

	/**
	 * Sort the elements of this list using the provided way of comparing
	 * elements. Does change the underlying list.
	 * 
	 * @param comparator
	 *            The way to compare elements for sorting. Pass null to use
	 *            E's natural ordering
	 */
	void sort(Comparator<E> comparator);

	/**
	 * Convert the list into a iterable
	 * 
	 * @return An iterable view onto the list
	 */
	Iterable<E> toIterable();
}