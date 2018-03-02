package bjc.utils.data;

/**
 * A toggle that will only give the first value once, only yielding the second
 * value afterwards.
 * 
 * @author student
 *
 * @param <E>
 *            The type of value stored
 */
public class OneWayToggle<E> implements Toggle<E> {
	private E first;
	private E second;

	private boolean gotFirst;

	/**
	 * Create a new one-way toggle
	 * 
	 * @param first
	 *            The value to offer first, and only once
	 * @param second
	 *            The value to offer second and repeatedly
	 */
	public OneWayToggle(E first, E second) {
		this.first = first;

		this.second = second;
	}

	@Override
	public E get() {
		if (gotFirst)
			return second;

		gotFirst = true;
		return first;
	}

	@Override
	public E peek() {
		if (gotFirst)
			return second;
		return first;
	}

	@Override
	public void set(boolean gotFirst) {
		this.gotFirst = gotFirst;
	}

}
