package bjc.utils.data;

import java.util.Iterator;

public class CircularIterator<E> implements Iterator<E> {
	private Iterable<E> source;
	private Iterator<E> curr;

	public CircularIterator(Iterable<E> src) {
		source = src;
		curr = source.iterator();
	}

	public boolean hasNext() {
		// We always have something
		return true;
	}

	public E next() {
		if(curr.hasNext()) {
			return curr.next();
		} else {
			curr = source.iterator();
			return curr.next();
		}
	}

	public void remove() {
		curr.remove();
	}
}
