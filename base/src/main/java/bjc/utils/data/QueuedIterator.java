package bjc.utils.data;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * An iterator that supports queuing elements after/before the current iterator;
 * 
 * @author bjculkin
 *
 * @param <E> The type of element this iterator iterates over.
 */
public class QueuedIterator<E> implements Iterator<E> {
	private Iterator<E> cur;

	private Deque<Iterator<E>> pending;

	/**
	 * Static method for constructing iterators.
	 * 
	 * @return A queued iterator.
	 */
	public static <E> QueuedIterator<E> queued() {
		return new QueuedIterator<>();
	}

	/**
	 * Static method for constructing iterators.
	 * 
	 * @param vals
	 * 		The values to iterate over.
	 *
	 * @return A queued iterator.
	 */
	@SafeVarargs
	public static <E> QueuedIterator<E> queued(E... vals) {
		return new QueuedIterator<>(new ArrayIterator<>(vals));
	}

	/**
	 * Static method for constructing iterators.
	 * 
	 * @param itrs
	 * 		The iterators to use.
	 *
	 * @return A queued iterator over the provided iterators.
	 */
	@SafeVarargs
	public static <E> QueuedIterator<E> queued(Iterator<E>... itrs) {
		return new QueuedIterator<>(itrs);
	}

	/**
	 * Static method for constructing iterators.
	 * 
	 * @param itrs
	 * 		The iterables to use.
	 *
	 * @return A queued iterator over the provided iterables.
	 */
	@SafeVarargs
	public static <E> QueuedIterator<E> queued(Iterable<E>... itrs) {
		return new QueuedIterator<>(itrs);
	}

	/**
	 * Create a new queued iterator that starts blank.
	 */
	public QueuedIterator() {
		pending = new ArrayDeque<>();
	}

	/**
	 * Create a new queued iterator with a set of initial sources.
	 * 
	 * @param inits
	 *                The set of initial iterators to use.
	 */
	@SafeVarargs
	public QueuedIterator(Iterator<E>... inits) {
		this();

		for (Iterator<E> init : inits) {
			pending.add(init);
		}
	}

	/**
	 * Create a new queued iterator with a set of initial sources.
	 * 
	 * @param inits
	 *                The set of initial iterables to use.
	 */
	@SafeVarargs
	public QueuedIterator(Iterable<E>... inits) {
		this();

		for (Iterable<E> init : inits) {
			pending.add(init.iterator());
		}
	}

	/**
	 * Create a new queued iterator with a set of initial values.
	 * 
	 * @param vals
	 * 		The set of initial values to use.
	 */
	@SafeVarargs
	public QueuedIterator(E... vals) {
		this(new ArrayIterator<>(vals));
	}

	/**
	 * Add a new iterator who we will iterate through first.
	 * 
	 * @param itr
	 *                The iterator to go through first.
	 */
	public void before(Iterator<E> itr) {
		pending.push(cur);

		cur = itr;
	}

	/**
	 * Add a new iterable who we will iterate through first.
	 * 
	 * @param itr
	 *                The iterable to go through first.
	 */
	public void before(Iterable<E> itr) {
		before(itr.iterator());
	}
	
	/**
	 * Add a new set of values who we will iterate through first.
	 * 
	 * @param vals
	 * 		Values to iterate over first.
	 */
	public void before(@SuppressWarnings("unchecked") E... vals) {
		before(new ArrayIterator<>(vals));
	}

	/**
	 * Add a new iterator who we will iterate through next.
	 * 
	 * @param itr
	 *                The iterator to go through next.
	 */
	public void after(Iterator<E> itr) {
		pending.push(itr);
	}

	/**
	 * Add a new iterable who we will iterate through next.
	 * 
	 * @param itr
	 *                The iterable to go through next.
	 */
	public void after(Iterable<E> itr) {
		after(itr.iterator());
	}

	/**
	 * Add a new set of values who we will iterate through next.
	 * 
	 * @param vals
	 * 		The values to iterate over next.
	 */
	public void after(@SuppressWarnings("unchecked") E... vals) {
		after(new ArrayIterator<>(vals));
	}
	
	/**
	 * Add a new iterator who we will iterate through last.
	 * 
	 * @param itr
	 *                The iterator to go through last.
	 */
	public void last(Iterator<E> itr) {
		pending.add(itr);
	}

	/**
	 * Add a new iterable who we will iterate through last.
	 * 
	 * @param itr
	 *                The iterable to go through last.
	 */
	public void last(Iterable<E> itr) {
		last(itr.iterator());
	}

	/**
	 * Add a new set of values who we will iterate through last.
	 * 
	 * @param vals
	 * 	The values we will iterate over.
	 */
	public void last(@SuppressWarnings("unchecked") E... vals) {
		last(new ArrayIterator<>(vals));
	}

	@Override
	public boolean hasNext() {
		while (cur == null || !cur.hasNext()) {
			if (pending.isEmpty()) return false;

			cur = pending.pop();
		}

		return cur.hasNext();
	}

	@Override
	public E next() {
		while (cur == null || !cur.hasNext()) {
			if (pending.isEmpty()) return null;

			cur = pending.pop();
		}

		return cur.next();
	}

}
