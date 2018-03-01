package bjc.utils.data;

import java.util.Iterator;

/**
 * An iterator that repeats elements from a provided iterable.
 *
 * @author EVE
 *
 * @param <E>
 *        The type of the iterable.
 */
public class CircularIterator<E> implements Iterator<E> {
	/* The iterable, and our current iterator into it. */
	private Iterable<E> source;
	private Iterator<E> curr;

	/* Our current element. */
	private E curElm;

	/*
	 * Should we actually get new iterators, or just repeat the last
	 * element?
	 */
	private boolean doCircle;

	/**
	 * Create a new circular iterator.
	 *
	 * @param src
	 *        The iterable to iterate from.
	 *
	 * @param circ
	 *        Should we actually do circular iteration, or just repeat the
	 *        terminal element?
	 */
	public CircularIterator(final Iterable<E> src, final boolean circ) {
		source = src;
		curr = source.iterator();

		doCircle = circ;
	}

	/**
	 * Create a new circular iterator that does actual circular iteration.
	 *
	 * @param src
	 *        The iterable to iterate from.
	 */
	public CircularIterator(final Iterable<E> src) {
		this(src, true);
	}

	@Override
	public boolean hasNext() {
		/* We always have something. */
		return true;
	}

	@Override
	public E next() {
		if(!curr.hasNext()) {
			if(doCircle) {
				curr = source.iterator();
			} else
				return curElm;
		}

		curElm = curr.next();

		return curElm;
	}

	@Override
	public void remove() {
		curr.remove();
	}
}
