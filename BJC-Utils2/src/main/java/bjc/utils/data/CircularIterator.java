package bjc.utils.data;

import java.util.Iterator;

public class CircularIterator<E> implements Iterator<E> {
	private Iterable<E> source;
	private Iterator<E> curr;

	private E curElm;

	private boolean doCircle;

	public CircularIterator(Iterable<E> src, boolean circ) {
		source = src;
		curr = source.iterator();

		doCircle = circ;
	}

	public CircularIterator(Iterable<E> src) {
		this(src, true);
	}

	public boolean hasNext() {
		// We always have something
		return true;
	}

	public E next() {
		if (!curr.hasNext()) {
			if (doCircle) {
				curr = source.iterator();
			} else {
				return curElm;
			}
		}

		curElm = curr.next();

		return curElm;
	}

	public void remove() {
		curr.remove();
	}
}
