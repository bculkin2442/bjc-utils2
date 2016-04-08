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
import bjc.utils.data.IPair;
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
public class FunctionalList<E> implements Cloneable, IFunctionalList<E> {
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

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#add(E)
	 */
	@Override
	public boolean add(E item) {
		return wrappedList.add(item);
	}

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#allMatch(java.util.function.Predicate)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#anyMatch(java.util.function.Predicate)
	 */
	@Override
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
	public IFunctionalList<E> clone() {
		IFunctionalList<E> clonedList = new FunctionalList<>();

		for (E element : wrappedList) {
			clonedList.add(element);
		}

		return clonedList;
	}

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#combineWith(bjc.utils.funcdata.IFunctionalList, java.util.function.BiFunction)
	 */
	@Override
	public <T, F> IFunctionalList<F> combineWith(
			IFunctionalList<T> rightList,
			BiFunction<E, T, F> itemCombiner) {
		if (rightList == null) {
			throw new NullPointerException(
					"Target combine list must not be null");
		} else if (itemCombiner == null) {
			throw new NullPointerException("Combiner must not be null");
		}

		IFunctionalList<F> returnedList = new FunctionalList<>();

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

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#contains(E)
	 */
	@Override
	public boolean contains(E item) {
		// Check if any items in the list match the provided item
		return this.anyMatch(item::equals);
	}

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#first()
	 */
	@Override
	public E first() {
		if (wrappedList.size() < 1) {
			throw new NoSuchElementException(
					"Attempted to get first element of empty list");
		}

		return wrappedList.get(0);
	}

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#flatMap(java.util.function.Function)
	 */
	@Override
	public <T> IFunctionalList<T> flatMap(
			Function<E, IFunctionalList<T>> elementExpander) {
		if (elementExpander == null) {
			throw new NullPointerException("Expander must not be null");
		}

		IFunctionalList<T> returnedList = new FunctionalList<>(
				this.wrappedList.size());

		forEach(element -> {
			IFunctionalList<T> expandedElement = elementExpander
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

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#forEach(java.util.function.Consumer)
	 */
	@Override
	public void forEach(Consumer<E> action) {
		if (action == null) {
			throw new NullPointerException("Action is null");
		}

		wrappedList.forEach(action);
	}

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#forEachIndexed(java.util.function.BiConsumer)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#getByIndex(int)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#getMatching(java.util.function.Predicate)
	 */
	@Override
	public IFunctionalList<E> getMatching(Predicate<E> matchPredicate) {
		if (matchPredicate == null) {
			throw new NullPointerException("Predicate must not be null");
		}

		IFunctionalList<E> returnedList = new FunctionalList<>();

		wrappedList.forEach((element) -> {
			if (matchPredicate.test(element)) {
				// The item matches, so add it to the returned list
				returnedList.add(element);
			}
		});

		return returnedList;
	}

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#getSize()
	 */
	@Override
	public int getSize() {
		return wrappedList.size();
	}

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return wrappedList.isEmpty();
	}

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#map(java.util.function.Function)
	 */
	@Override
	public <T> IFunctionalList<T> map(Function<E, T> elementTransformer) {
		if (elementTransformer == null) {
			throw new NullPointerException("Transformer must be not null");
		}

		IFunctionalList<T> returnedList = new FunctionalList<>(
				this.wrappedList.size());

		forEach(element -> {
			// Add the transformed item to the result
			returnedList.add(elementTransformer.apply(element));
		});

		return returnedList;
	}

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#pairWith(bjc.utils.funcdata.IFunctionalList)
	 */
	@Override
	public <T> IFunctionalList<IPair<E, T>> pairWith(
			IFunctionalList<T> rightList) {
		return combineWith(rightList, Pair<E, T>::new);
	}

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#partition(int)
	 */
	@Override
	public IFunctionalList<IFunctionalList<E>> partition(
			int numberPerPartition) {
		if (numberPerPartition < 1
				|| numberPerPartition > wrappedList.size()) {
			throw new IllegalArgumentException("" + numberPerPartition
					+ " is an invalid partition size. Must be between 1 and "
					+ wrappedList.size());
		}

		IFunctionalList<IFunctionalList<E>> returnedList = new FunctionalList<>();

		// The current partition being filled
		GenHolder<IFunctionalList<E>> currentPartition = new GenHolder<>(
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
			GenHolder<IFunctionalList<E>> currentPartition) {
		return currentPartition.unwrap(
				(partition) -> partition.getSize() >= numberPerPartition);
	}

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#prepend(E)
	 */
	@Override
	public void prepend(E item) {
		wrappedList.add(0, item);
	}

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#randItem(java.util.Random)
	 */
	@Override
	public E randItem(Random rnd) {
		if (rnd == null) {
			throw new NullPointerException(
					"Random source must not be null");
		}

		int randomIndex = rnd.nextInt(wrappedList.size());

		return wrappedList.get(randomIndex);
	}

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#reduceAux(T, java.util.function.BiFunction, java.util.function.Function)
	 */
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
		IHolder<T> currentState = new GenHolder<>(initialValue);

		wrappedList.forEach(element -> {
			// Accumulate a new value into the state
			currentState.transform(
					(state) -> stateAccumulator.apply(element, state));
		});

		// Convert the state to its final value
		return currentState.unwrap(resultTransformer);
	}

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#removeIf(java.util.function.Predicate)
	 */
	@Override
	public boolean removeIf(Predicate<E> removePredicate) {
		if (removePredicate == null) {
			throw new NullPointerException("Predicate must be non-null");
		}

		return wrappedList.removeIf(removePredicate);
	}

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#removeMatching(E)
	 */
	@Override
	public void removeMatching(E desiredElement) {
		removeIf((element) -> element.equals(desiredElement));
	}

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#search(E, java.util.Comparator)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#sort(java.util.Comparator)
	 */
	@Override
	public void sort(Comparator<E> comparator) {
		Collections.sort(wrappedList, comparator);
	}

	/* (non-Javadoc)
	 * @see bjc.utils.funcdata.IFunctionalList#toIterable()
	 */
	@Override
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