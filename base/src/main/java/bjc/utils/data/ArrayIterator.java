package bjc.utils.data;

import java.util.Iterator;
/**
 * Represents an iterator over an array of values.
 *
 * @param <T> The type of values in the array.
 * 
 * @author Ben Culkin
 */
public class ArrayIterator<T> implements Iterator<T> {
	private Object[] arr;
	private int      idx;

	/**
	 * Create a new array iterator.
	 *
	 * @param elms
	 * 	The array that will be iterated over.
	 */
	@SafeVarargs
	public ArrayIterator(T... elms) {
		arr = elms;
		idx = 0;
	}

	@Override
	public boolean hasNext() {
		return idx < arr.length;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T next() {
		if (idx >= arr.length) return null;

		return (T)(arr[idx++]);
	}
}
