package bjc.utils.funcdata;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import bjc.utils.data.GenHolder;
import bjc.utils.data.IHolder;
import bjc.utils.data.Pair;

import java.util.ArrayList;

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
public class FunctionalList<E> implements Cloneable {
	/**
	 * The list used as a backing store
	 */
	private List<E> wrappedList;

	/**
	 * Create a new empty functional list.
	 */
	public FunctionalList() {
		wrappedList = new ArrayList<>();
	}

	/**
	 * Create a new functional list containing the specified items.
	 * 
	 * @param items
	 *            The items to put into this functional list.
	 */
	@SafeVarargs
	public FunctionalList(E... items) {
		wrappedList = new ArrayList<>(items.length);

		for (E item : items) {
			wrappedList.add(item);
		}
	}

	/**
	 * Create a functional list using the same backing list as the provided
	 * list.
	 * 
	 * @param sourceList
	 *            The source for a backing list
	 */
	public FunctionalList(FunctionalList<E> sourceList) {
		if (sourceList == null) {
			throw new NullPointerException("Source list must be non-null");
		}

		// Find out if this should make a copy of the source's wrapped list
		// instead.
		//
		// I've decided it shouldn't. Call clone() if that's what you want.
		// This method just creates another list that refers to the
		// source's backing list
		wrappedList = sourceList.wrappedList;
	}

	/**
	 * Create a new functional list with the specified size.
	 * 
	 * @param size
	 *            The size of the backing list .
	 */
	private FunctionalList(int size) {
		wrappedList = new ArrayList<>(size);
	}

	/**
	 * Create a new functional list as a wrapper of a existing list.
	 * 
	 * @param backingList
	 *            The list to use as a backing list.
	 */
	public FunctionalList(List<E> backingList) {
		if (backingList == null) {
			throw new NullPointerException(
					"Backing list must be non-null");
		}

		wrappedList = backingList;
	}

	/**
	 * Add an item to this list
	 * 
	 * @param item
	 *            The item to add to this list.
	 * @return Whether the item was added to the list succesfully.
	 */
	public boolean add(E item) {
		return wrappedList.add(item);
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
	public boolean allMatch(Predicate<E> matchPredicate) {
		if (matchPredicate == null) {
			throw new NullPointerException("Predicate must be non-null");
		}

		for (E item : wrappedList) {
			if (!matchPredicate.test(item)) {
				// We've found a non-matching item
				return false;
			}
		}

		// All of the items matched
		return true;
	}

	/**
	 * Check if any of the elements in this list match the specified list.
	 * 
	 * @param matchPredicate
	 *            The predicate to use for checking.
	 * @return Whether any element in the list matches the provided
	 *         predicate.
	 */
	public boolean anyMatch(Predicate<E> matchPredicate) {
		if (matchPredicate == null) {
			throw new NullPointerException("Predicate must be not null");
		}

		for (E item : wrappedList) {
			if (matchPredicate.test(item)) {
				// We've found a matching item
				return true;
			}
		}

		// We didn't find a matching item
		return false;
	}

	@Override
	public FunctionalList<E> clone() {
		FunctionalList<E> clonedList = new FunctionalList<>();

		for (E element : wrappedList) {
			clonedList.add(element);
		}

		return clonedList;
	}

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
	public <T, F> FunctionalList<F> combineWith(
			FunctionalList<T> rightList,
			BiFunction<E, T, F> itemCombiner) {
		if (rightList == null) {
			throw new NullPointerException(
					"Target combine list must not be null");
		} else if (itemCombiner == null) {
			throw new NullPointerException("Combiner must not be null");
		}

		FunctionalList<F> returnedList = new FunctionalList<>();

		// Get the iterator for the other list
		Iterator<T> rightIterator = rightList.toIterable().iterator();

		for (Iterator<E> leftIterator = wrappedList
				.iterator(); leftIterator.hasNext()
						&& rightIterator.hasNext();) {
			// Add the transformed items to the result list
			E leftVal = leftIterator.next();
			T rightVal = rightIterator.next();

			returnedList.add(itemCombiner.apply(leftVal, rightVal));
		}

		return returnedList;
	}

	/**
	 * Check if the list contains the specified item
	 * 
	 * @param item
	 *            The item to see if it is contained
	 * @return Whether or not the specified item is in the list
	 */
	public boolean contains(E item) {
		// Check if any items in the list match the provided item
		return this.anyMatch(item::equals);
	}

	/**
	 * Get the first element in the list
	 * 
	 * @return The first element in this list.
	 */
	public E first() {
		if (wrappedList.size() < 1) {
			throw new NoSuchElementException(
					"Attempted to get first element of empty list");
		}

		return wrappedList.get(0);
	}

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
	public <T> FunctionalList<T> flatMap(
			Function<E, FunctionalList<T>> elementExpander) {
		if (elementExpander == null) {
			throw new NullPointerException("Expander must not be null");
		}

		FunctionalList<T> returnedList = new FunctionalList<>(
				this.wrappedList.size());

		forEach(element -> {
			FunctionalList<T> expandedElement = elementExpander
					.apply(element);

			if (expandedElement == null) {
				throw new NullPointerException(
						"Expander returned null list");
			}

			// Add each element to the returned list
			expandedElement.forEach(returnedList::add);
		});

		return returnedList;
	}

	/**
	 * Apply a given action for each member of the list
	 * 
	 * @param action
	 *            The action to apply to each member of the list.
	 */
	public void forEach(Consumer<E> action) {
		if (action == null) {
			throw new NullPointerException("Action is null");
		}

		wrappedList.forEach(action);
	}

	/**
	 * Apply a given function to each element in the list and its index.
	 * 
	 * @param indexedAction
	 *            The function to apply to each element in the list and its
	 *            index.
	 */
	public void forEachIndexed(BiConsumer<Integer, E> indexedAction) {
		if (indexedAction == null) {
			throw new NullPointerException("Action must not be null");
		}

		// This is held b/c ref'd variables must be final/effectively final
		GenHolder<Integer> currentIndex = new GenHolder<>(0);

		wrappedList.forEach((element) -> {
			// Call the action with the index and the value
			indexedAction.accept(currentIndex.unwrap(index -> index),
					element);

			// Increment the value
			currentIndex.transform((index) -> index + 1);
		});
	}

	/**
	 * Retrieve a value in the list by its index.
	 * 
	 * @param index
	 *            The index to retrieve a value from.
	 * @return The value at the specified index in the list.
	 */
	public E getByIndex(int index) {
		return wrappedList.get(index);
	}

	/**
	 * Get the internal backing list.
	 * 
	 * @return The backing list this list is based off of.
	 */
	public List<E> getInternal() {
		return wrappedList;
	}

	/**
	 * Retrieve a list containing all elements matching a predicate
	 * 
	 * @param matchPredicate
	 *            The predicate to match by
	 * @return A list containing all elements that match the predicate
	 */
	public FunctionalList<E> getMatching(Predicate<E> matchPredicate) {
		if (matchPredicate == null) {
			throw new NullPointerException("Predicate must not be null");
		}

		FunctionalList<E> returnedList = new FunctionalList<>();

		wrappedList.forEach((element) -> {
			if (matchPredicate.test(element)) {
				// The item matches, so add it to the returned list
				returnedList.add(element);
			}
		});

		return returnedList;
	}

	/**
	 * Retrieve the size of the wrapped list
	 * 
	 * @return The size of the wrapped list
	 */
	public int getSize() {
		return wrappedList.size();
	}

	/**
	 * Check if this list is empty.
	 * 
	 * @return Whether or not this list is empty.
	 */
	public boolean isEmpty() {
		return wrappedList.isEmpty();
	}

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
	public <T> FunctionalList<T> map(Function<E, T> elementTransformer) {
		if (elementTransformer == null) {
			throw new NullPointerException("Transformer must be not null");
		}

		FunctionalList<T> returnedList = new FunctionalList<>(
				this.wrappedList.size());

		forEach(element -> {
			// Add the transformed item to the result
			returnedList.add(elementTransformer.apply(element));
		});

		return returnedList;
	}

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
	public <T> FunctionalList<Pair<E, T>> pairWith(
			FunctionalList<T> rightList) {
		return combineWith(rightList, Pair<E, T>::new);
	}

	/**
	 * Partition this list into a list of sublists
	 * 
	 * @param numberPerPartition
	 *            The size of elements to put into each one of the sublists
	 * @return A list partitioned into partitions of size nPerPart
	 */
	public FunctionalList<FunctionalList<E>> partition(
			int numberPerPartition) {
		if (numberPerPartition < 1
				|| numberPerPartition > wrappedList.size()) {
			throw new IllegalArgumentException("" + numberPerPartition
					+ " is an invalid partition size. Must be between 1 and "
					+ wrappedList.size());
		}

		FunctionalList<FunctionalList<E>> returnedList = new FunctionalList<>();

		// The current partition being filled
		GenHolder<FunctionalList<E>> currentPartition = new GenHolder<>(
				new FunctionalList<>());

		this.forEach((element) -> {
			if (isPartitionFull(numberPerPartition, currentPartition)) {
				// Add the partition to the list
				returnedList.add(
						currentPartition.unwrap((partition) -> partition));

				// Start a new partition
				currentPartition
						.transform((partition) -> new FunctionalList<>());
			} else {
				// Add the element to the current partition
				currentPartition
						.unwrap((partition) -> partition.add(element));
			}
		});

		return returnedList;
	}

	/*
	 * Check if a partition has room for another item
	 */
	private Boolean isPartitionFull(int numberPerPartition,
			GenHolder<FunctionalList<E>> currentPartition) {
		return currentPartition.unwrap(
				(partition) -> partition.getSize() >= numberPerPartition);
	}

	/**
	 * Prepend an item to the list
	 * 
	 * @param item
	 *            The item to prepend to the list
	 */
	public void prepend(E item) {
		wrappedList.add(0, item);
	}

	/**
	 * Select a random item from this list, using the provided random
	 * number generator.
	 * 
	 * @param rnd
	 *            The random number generator to use.
	 * @return A random element from this list.
	 */
	public E randItem(Random rnd) {
		if (rnd == null) {
			throw new NullPointerException(
					"Random source must not be null");
		}

		int randomIndex = rnd.nextInt(wrappedList.size());

		return wrappedList.get(randomIndex);
	}

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
	public <T, F> F reduceAux(T initialValue,
			BiFunction<E, T, T> stateAccumulator,
			Function<T, F> resultTransformer) {
		if (stateAccumulator == null) {
			throw new NullPointerException("Accumulator must not be null");
		} else if (resultTransformer == null) {
			throw new NullPointerException("Transformer must not be null");
		}

		// The current collapsed list
		IHolder<T> currentState = new GenHolder<>(initialValue);

		wrappedList.forEach(element -> {
			// Accumulate a new value into the state
			currentState.transform(
					(state) -> stateAccumulator.apply(element, state));
		});

		// Convert the state to its final value
		return currentState.unwrap(resultTransformer);
	}

	/**
	 * Remove all elements that match a given predicate
	 * 
	 * @param removePredicate
	 *            The predicate to use to determine elements to delete
	 * @return Whether there was anything that satisfied the predicate
	 */
	public boolean removeIf(Predicate<E> removePredicate) {
		if (removePredicate == null) {
			throw new NullPointerException("Predicate must be non-null");
		}

		return wrappedList.removeIf(removePredicate);
	}

	/**
	 * Remove all parameters that match a given parameter
	 * 
	 * @param desiredElement
	 *            The object to remove all matching copies of
	 */
	public void removeMatching(E desiredElement) {
		removeIf((element) -> element.equals(desiredElement));
	}

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
	public E search(E searchKey, Comparator<E> comparator) {
		// Search our internal list
		int foundIndex = Collections.binarySearch(wrappedList, searchKey,
				comparator);

		if (foundIndex >= 0) {
			// We found a matching element
			return wrappedList.get(foundIndex);
		}

		// We didn't find an element
		return null;
	}

	/**
	 * Sort the elements of this list using the provided way of comparing
	 * elements. Does change the underlying list.
	 * 
	 * @param comparator
	 *            The way to compare elements for sorting. Pass null to use
	 *            E's natural ordering
	 */
	public void sort(Comparator<E> comparator) {
		Collections.sort(wrappedList, comparator);
	}

	/**
	 * Convert the list into a iterable
	 * 
	 * @return An iterable view onto the list
	 */
	public Iterable<E> toIterable() {
		return wrappedList;
	}

	/*
	 * Reduce this item to a form useful for looking at in the debugger.
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("(");

		// Append the string form of each element
		forEach(strang -> sb.append(strang + ", "));

		// Remove trailing space and comma
		sb.deleteCharAt(sb.length() - 1);
		// sb.deleteCharAt(sb.length() - 2);

		sb.append(")");

		return sb.toString();
	}
}