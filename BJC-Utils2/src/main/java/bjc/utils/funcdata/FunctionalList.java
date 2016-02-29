package bjc.utils.funcdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import bjc.utils.data.GenHolder;
import bjc.utils.data.Pair;

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
	private List<E> wrap;

	/**
	 * Create a new empty functional list.
	 */
	public FunctionalList() {
		wrap = new ArrayList<>();
	}

	/**
	 * Create a new functional list containing the specified items.
	 * 
	 * @param items
	 *            The items to put into this functional list.
	 */
	@SafeVarargs
	public FunctionalList(E... items) {
		wrap = new ArrayList<>(items.length);

		for (E item : items) {
			wrap.add(item);
		}
	}

	/**
	 * Create a functional list using the same backing list as the provided
	 * list.
	 * 
	 * @param fl
	 *            The source for a backing list
	 */
	public FunctionalList(FunctionalList<E> fl) {
		// TODO Find out if this should make a copy of fl.wrap instead.
		wrap = fl.wrap;
	}

	/**
	 * Create a new functional list with the specified size.
	 * 
	 * @param sz
	 *            The size of the backing list .
	 */
	private FunctionalList(int sz) {
		wrap = new ArrayList<>(sz);
	}

	/**
	 * Create a new functional list as a wrapper of a existing list.
	 * 
	 * @param l
	 *            The list to use as a backing list.
	 */
	public FunctionalList(List<E> l) {
		wrap = l;
	}

	/**
	 * Add an item to this list
	 * 
	 * @param item
	 *            The item to add to this list.
	 * @return Whether the item was added to the list succesfully.
	 */
	public boolean add(E item) {
		return wrap.add(item);
	}

	/**
	 * Check if all of the elements of this list match the specified
	 * predicate.
	 * 
	 * @param p
	 *            The predicate to use for checking.
	 * @return Whether all of the elements of the list match the specified
	 *         predicate.
	 */
	public boolean allMatch(Predicate<E> p) {
		for (E item : wrap) {
			if (!p.test(item)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if any of the elements in this list match the specified list.
	 * 
	 * @param p
	 *            The predicate to use for checking.
	 * @return Whether any element in the list matches the provided
	 *         predicate.
	 */
	public boolean anyMatch(Predicate<E> p) {
		for (E item : wrap) {
			if (p.test(item)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public FunctionalList<E> clone() {
		FunctionalList<E> fl = new FunctionalList<>();

		for (E ele : wrap) {
			fl.add(ele);
		}

		return fl;
	}

	/**
	 * Combine this list with another one into a new list and merge the
	 * results. Works sort of like a combined zip/map over resulting pairs.
	 * Does not change the underlying list.
	 * 
	 * NOTE: The returned list will have the length of the shorter of this
	 * list and the combined one.
	 * 
	 * @param l
	 *            The list to combine with
	 * @param bf
	 *            The function to use for combining element pairs.
	 * @return A new list containing the merged pairs of lists.
	 */
	public <T, F> FunctionalList<F> combineWith(FunctionalList<T> l,
			BiFunction<E, T, F> bf) {
		FunctionalList<F> r = new FunctionalList<>();

		Iterator<T> i2 = l.wrap.iterator();

		for (Iterator<E> i1 = wrap.iterator(); i1.hasNext()
				&& i2.hasNext();) {
			r.add(bf.apply(i1.next(), i2.next()));
		}

		return r;
	}

	/**
	 * Check if the list contains the specified item
	 * 
	 * @param item
	 *            The item to see if it is contained
	 * @return Whether or not the specified item is in the list
	 */
	public boolean contains(E item) {
		return this.anyMatch(item::equals);
	}

	/**
	 * Get the first element in the list
	 * 
	 * @return The first element in this list.
	 */
	public E first() {
		return wrap.get(0);
	}

	/**
	 * Apply a function to each member of the list, then flatten the
	 * results. Does not change the underlying list.
	 * 
	 * @param f
	 *            The function to apply to each member of the list.
	 * @return A new list containing the flattened results of applying the
	 *         provided function.
	 */
	public <T> FunctionalList<T> flatMap(Function<E, List<T>> f) {
		FunctionalList<T> fl = new FunctionalList<>(this.wrap.size());

		forEach(e -> {
			f.apply(e).forEach(e2 -> {
				fl.add(e2);
			});
		});

		return fl;
	}

	/**
	 * Apply a given action for each member of the list
	 * 
	 * @param c
	 *            The action to apply to each member of the list.
	 */
	public void forEach(Consumer<E> c) {
		wrap.forEach(c);
	}

	/**
	 * Apply a given function to each element in the list and its index.
	 * 
	 * @param c
	 *            The function to apply to each element in the list and its
	 *            index.
	 */
	public void forEachIndexed(BiConsumer<Integer, E> c) {
		int i = 0;

		for (E e : wrap) {
			c.accept(i++, e);
		}
	}

	/**
	 * Retrieve a value in the list by its index.
	 * 
	 * @param i
	 *            The index to retrieve a value from.
	 * @return The value at the specified index in the list.
	 */
	public E getByIndex(int i) {
		return wrap.get(i);
	}

	/**
	 * Get the internal backing list.
	 * 
	 * @return The backing list this list is based off of.
	 */
	public List<E> getInternal() {
		return wrap;
	}

	/**
	 * Retrieve a list containing all elements matching a predicate
	 * 
	 * @param matchPred
	 *            The predicate to match by
	 * @return A list containing all elements that match the predicate
	 */
	public FunctionalList<E> getMatching(Predicate<E> matchPred) {
		FunctionalList<E> fl = new FunctionalList<>();

		this.forEach((elem) -> {
			if (matchPred.test(elem)) {
				fl.add(elem);
			}
		});

		return fl;
	}

	/**
	 * Check if this list is empty.
	 * 
	 * @return Whether or not this list is empty.
	 */
	public boolean isEmpty() {
		return wrap.isEmpty();
	}

	/**
	 * Create a new list by applying the given function to each element in
	 * the list. Does not change the underlying list.
	 * 
	 * @param f
	 *            The function to apply to each element in the list
	 * @return A new list containing the mapped elements of this list.
	 */
	public <T> FunctionalList<T> map(Function<E, T> f) {
		FunctionalList<T> fl = new FunctionalList<>(this.wrap.size());

		forEach(e -> fl.add(f.apply(e)));

		return fl;
	}

	/**
	 * Zip two lists into a list of pairs
	 * 
	 * @param fl
	 *            The list to use as the left side of the pair
	 * @return A list containing pairs of this element and the specified
	 *         list
	 */
	public <T> FunctionalList<Pair<E, T>> pairWith(FunctionalList<T> fl) {
		return combineWith(fl, Pair<E, T>::new);
	}

	/**
	 * Prepend an item to the list
	 * 
	 * @param item
	 *            The item to prepend to the list
	 */
	public void prepend(E item) {
		wrap.add(0, item);
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
		return wrap.get(rnd.nextInt(wrap.size()));
	}

	/**
	 * Reduce this list to a single value, using a accumulative approach.
	 * 
	 * @param val
	 *            The initial value of the accumulative state.
	 * @param bf
	 *            The function to use to combine a list element with the
	 *            accumulative state.
	 * @param finl
	 *            The function to use to convert the accumulative state
	 *            into a final result.
	 * @return A single value condensed from this list and transformed into
	 *         its final state.
	 */
	public <T, F> F reduceAux(T val, BiFunction<E, T, T> bf,
			Function<T, F> finl) {
		GenHolder<T> acum = new GenHolder<>(val);

		wrap.forEach(e -> acum.transform((vl) -> bf.apply(e, vl)));

		return acum.unwrap(finl);
	}

	/**
	 * Remove all elements that match a given predicate
	 * 
	 * @param remPred
	 *            The predicate to use to determine elements to delete
	 * @return Whether there was anything that satisfied the predicate
	 */
	public boolean removeIf(Predicate<E> remPred) {
		return wrap.removeIf(remPred);
	}

	/**
	 * Perform a binary search for the specified key using the provided
	 * means of comparing elements. Since this IS a binary search, the list
	 * must have been sorted before hand.
	 * 
	 * @param key
	 *            The key to search for.
	 * @param cmp
	 *            The way to compare elements for searching
	 * @return The element if it is in this list, or null if it is not.
	 */
	public E search(E key, Comparator<E> cmp) {
		int res = Collections.binarySearch(wrap, key, cmp);

		return (res >= 0) ? wrap.get(res) : null;
	}

	/**
	 * Sort the elements of this list using the provided way of comparing
	 * elements. Does change the underlying list.
	 * 
	 * @param cmp
	 *            The way to compare elements for sorting.
	 */
	public void sort(Comparator<E> cmp) {
		Collections.sort(wrap, cmp);
	}

	/**
	 * Convert the list into a iterable
	 * 
	 * @return An iterable view onto the list
	 */
	public Iterable<E> toIterable() {
		return wrap;
	}

	/*
	 * Reduce this item to a form useful for looking at in the debugger.
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder("(");

		forEach(s -> sb.append(s + ", "));

		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");

		return sb.toString();
	}

	public void removeMatching(E obj) {
		removeIf((ele) -> {
			return ele.equals(obj);
		});
	}
}
