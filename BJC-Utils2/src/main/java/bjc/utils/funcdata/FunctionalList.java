package bjc.utils.funcdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import bjc.utils.data.IHolder;
import bjc.utils.data.IPair;
import bjc.utils.data.Identity;
import bjc.utils.data.Pair;

/**
 * A wrapper over another list that provides eager functional operations
 * over it. 
 *
 * Differs from a stream in every way except for the fact that
 * they both provide functional operations.
 * 
 * @author ben
 *
 * @param <E>
 *            The type in this list
 */
public class FunctionalList<E> implements Cloneable, IList<E> {
	/*
	 * The list used as a backing store
	 */
	private List<E> wrapped;

	/**
	 * Create a new empty functional list.
	 */
	public FunctionalList() {
		wrapped = new ArrayList<>();
	}

	/**
	 * Create a new functional list containing the specified items.
	 * 
	 * Takes O(n) time, where n is the number of items specified
	 * 
	 * @param items
	 *            The items to put into this functional list.
	 */
	@SafeVarargs
	public FunctionalList(E... items) {
		wrapped = new ArrayList<>(items.length);

		for (E item : items) {
			wrapped.add(item);
		}
	}

	/**
	 * Create a new functional list with the specified size.
	 * 
	 * @param size
	 *            The size of the backing list .
	 */
	private FunctionalList(int size) {
		wrapped = new ArrayList<>(size);
	}

	/**
	 * Create a new functional list as a wrapper of a existing list.
	 * 
	 * Takes O(1) time, since it doesn't copy the list.
	 * 
	 * @param backing
	 *            The list to use as a backing list.
	 */
	public FunctionalList(List<E> backing) {
		if (backing == null) {
			throw new NullPointerException(
					"Backing list must be non-null");
		}

		wrapped = backing;
	}

	@Override
	public boolean add(E item) {
		return wrapped.add(item);
	}

	@Override
	public boolean allMatch(Predicate<E> predicate) {
		if (predicate == null) {
			throw new NullPointerException("Predicate must be non-null");
		}

		for (E item : wrapped) {
			if (!predicate.test(item)) {
				// We've found a non-matching item
				return false;
			}
		}

		// All of the items matched
		return true;
	}

	@Override
	public boolean anyMatch(Predicate<E> predicate) {
		if (predicate == null) {
			throw new NullPointerException("Predicate must be not null");
		}

		for (E item : wrapped) {
			if (predicate.test(item)) {
				// We've found a matching item
				return true;
			}
		}

		// We didn't find a matching item
		return false;
	}

	/**
	 * Clone this list into a new one, and clone the backing list as well
	 * 
	 * Takes O(n) time, where n is the number of elements in the list
	 * 
	 * @return A list
	 */
	@Override
	public IList<E> clone() {
		IList<E> cloned = new FunctionalList<>();

		for (E element : wrapped) {
			cloned.add(element);
		}

		return cloned;
	}

	@Override
	public <T, F> IList<F> combineWith(IList<T> rightList,
			BiFunction<E, T, F> itemCombiner) {
		if (rightList == null) {
			throw new NullPointerException( "Target combine list must not be null");
		} else if (itemCombiner == null) {
			throw new NullPointerException("Combiner must not be null");
		}

		IList<F> returned = new FunctionalList<>();

		// Get the iterator for the other list
		Iterator<T> rightIterator = rightList.toIterable().iterator();

		for (Iterator<E> leftIterator = wrapped.iterator();
				leftIterator.hasNext() && rightIterator.hasNext();) {
			// Add the transformed items to the result list
			E leftVal = leftIterator.next();
			T rightVal = rightIterator.next();

			returned.add(itemCombiner.apply(leftVal, rightVal));
		}

		return returned;
	}

	@Override
	public boolean contains(E item) {
		// Check if any items in the list match the provided item
		return this.anyMatch(item::equals);
	}

	@Override
	public E first() {
		if (wrapped.size() < 1) {
			throw new NoSuchElementException("Attempted to get first element of empty list");
		}

		return wrapped.get(0);
	}

	@Override
	public <T> IList<T> flatMap(Function<E, IList<T>> expander) {
		if (expander == null) {
			throw new NullPointerException("Expander must not be null");
		}

		IList<T> returned = new FunctionalList<>(this.wrapped.size());

		forEach(element -> {
			IList<T> expandedElement = expander.apply(element);

			if (expandedElement == null) {
				throw new NullPointerException("Expander returned null list");
			}

			// Add each element to the returned list
			expandedElement.forEach(returned::add);
		});

		return returned;
	}

	@Override
	public void forEach(Consumer<E> action) {
		if (action == null) {
			throw new NullPointerException("Action is null");
		}

		wrapped.forEach(action);
	}

	@Override
	public void forEachIndexed(BiConsumer<Integer, E> indexedAction) {
		if (indexedAction == null) {
			throw new NullPointerException("Action must not be null");
		}

		// This is held b/c ref'd variables must be final/effectively final
		IHolder<Integer> currentIndex = new Identity<>(0);

		wrapped.forEach((element) -> {
			// Call the action with the index and the value
			indexedAction.accept(currentIndex.unwrap(index -> index), element);

			// Increment the value
			currentIndex.transform((index) -> index + 1);
		});
	}

	@Override
	public E getByIndex(int index) {
		return wrapped.get(index);
	}

	/**
	 * Get the internal backing list.
	 * 
	 * @return The backing list this list is based off of.
	 */
	public List<E> getInternal() {
		return wrapped;
	}

	@Override
	public IList<E> getMatching(Predicate<E> predicate) {
		if (predicate == null) {
			throw new NullPointerException("Predicate must not be null");
		}

		IList<E> returned = new FunctionalList<>();

		wrapped.forEach((element) -> {
			if (predicate.test(element)) {
				// The item matches, so add it to the returned list
				returned.add(element);
			}
		});

		return returned;
	}

	@Override
	public int getSize() {
		return wrapped.size();
	}

	@Override
	public boolean isEmpty() {
		return wrapped.isEmpty();
	}

	/*
	 * Check if a partition has room for another item
	 */
	private Boolean isPartitionFull(int numberPerPartition,
			IHolder<IList<E>> currentPartition) {
		return currentPartition.unwrap((partition) -> partition.getSize() >= numberPerPartition);
	}

	@Override
	public <T> IList<T> map(Function<E, T> elementTransformer) {
		if (elementTransformer == null) {
			throw new NullPointerException("Transformer must be not null");
		}

		IList<T> returned = new FunctionalList<>(this.wrapped.size());

		forEach(element -> {
			// Add the transformed item to the result
			returned.add(elementTransformer.apply(element));
		});

		return returned;
	}

	@Override
	public <T> IList<IPair<E, T>> pairWith(IList<T> rightList) {
		return combineWith(rightList, Pair<E, T>::new);
	}

	@Override
	public IList<IList<E>> partition(int numberPerPartition) {
		if (numberPerPartition < 1
				|| numberPerPartition > wrapped.size()) {
			throw new IllegalArgumentException("" + numberPerPartition
					+ " is an invalid partition size. Must be between 1 and "
					+ wrapped.size());
		}

		IList<IList<E>> returned = new FunctionalList<>();

		// The current partition being filled
		IHolder<IList<E>> currentPartition = new Identity<>(
				new FunctionalList<>());

		this.forEach((element) -> {
			if (isPartitionFull(numberPerPartition, currentPartition)) {
				// Add the partition to the list
				returned.add(currentPartition.unwrap((partition) -> partition));

				// Start a new partition
				currentPartition.transform((partition) -> new FunctionalList<>());
			} else {
				// Add the element to the current partition
				currentPartition.unwrap((partition) -> partition.add(element));
			}
		});

		return returned;
	}

	@Override
	public void prepend(E item) {
		wrapped.add(0, item);
	}

	@Override
	public E randItem(Function<Integer, Integer> rnd) {
		if (rnd == null) {
			throw new NullPointerException("Random source must not be null");
		}

		int randomIndex = rnd.apply(wrapped.size());

		return wrapped.get(randomIndex);
	}

	@Override
	public <T, F> F reduceAux(T initialValue,
			BiFunction<E, T, T> stateAccumulator,
			Function<T, F> resultTransformer) {
		if (stateAccumulator == null) {
			throw new NullPointerException("Accumulator must not be null");
		} else if (resultTransformer == null) {
			throw new NullPointerException("Transformer must not be null");
		}

		// The current collapsed list
		IHolder<T> currentState = new Identity<>(initialValue);

		wrapped.forEach(element -> {
			// Accumulate a new value into the state
			currentState.transform((state) -> stateAccumulator.apply(element, state));
		});

		// Convert the state to its final value
		return currentState.unwrap(resultTransformer);
	}

	@Override
	public boolean removeIf(Predicate<E> removePredicate) {
		if (removePredicate == null) {
			throw new NullPointerException("Predicate must be non-null");
		}

		return wrapped.removeIf(removePredicate);
	}

	@Override
	public void removeMatching(E desiredElement) {
		removeIf((element) -> element.equals(desiredElement));
	}

	@Override
	public void reverse() {
		Collections.reverse(wrapped);
	}

	@Override
	public E search(E searchKey, Comparator<E> comparator) {
		// Search our internal list
		int foundIndex = Collections.binarySearch(wrapped, searchKey,
				comparator);

		if (foundIndex >= 0) {
			// We found a matching element
			return wrapped.get(foundIndex);
		}

		// We didn't find an element
		return null;
	}

	@Override
	public void sort(Comparator<E> comparator) {
		// sb.deleteCharAt(sb.length() - 2);
		Collections.sort(wrapped, comparator);
	}

	@Override
	public IList<E> tail() {
		return new FunctionalList<>(wrapped.subList(1, getSize()));
	}

	@Override
	public E[] toArray(E[] arrType) {
		return wrapped.toArray(arrType);
	}

	@Override
	public Iterable<E> toIterable() {
		return wrapped;
	}

	@Override
	public String toString() {
		int lSize = getSize();

		if(lSize == 0) return "()";

		StringBuilder sb = new StringBuilder("(");
		Iterator<E> itr = toIterable().iterator();
		E itm = itr.next();

		if(lSize == 1) {
			return "(" + itm + ")";
		}

		for(int i = 0; itr.hasNext(); itm = itr.next()) {
			sb.append(itm.toString());

			if(i < lSize-1) {
				sb.append(", ");
			}

			i += 1;
		}

		sb.append(")");
		return sb.toString();
	}
}
