package bjc.utils.data;

import java.util.Iterator;

public class SingleIterator<T> implements Iterator<T> {
	private T itm;

	private boolean yielded;

	public SingleIterator(T item) {
		itm = item;

		yielded = false;
	}

	public boolean hasNext() {
		return !yielded;
	}

	public T next() {
		yielded = true;

		return itm;
	}
}
