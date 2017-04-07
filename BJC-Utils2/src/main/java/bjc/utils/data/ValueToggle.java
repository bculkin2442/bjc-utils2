package bjc.utils.data;

/**
 * A simple implementation of {@link Toggle}.
 * 
 * @author EVE
 * 
 * @param <E>
 *                The type of value to toggle between.
 */
public class ValueToggle<E> implements Toggle<E> {
	private final E	lft;
	private final E	rght;

	private BooleanToggle alignment;

	/**
	 * Create a new toggle.
	 * 
	 * All toggles start right-aligned.
	 * 
	 * @param left
	 *                The value when the toggle is left-aligned.
	 * 
	 * @param right
	 *                The value when the toggle is right-aligned.
	 */
	public ValueToggle(E left, E right) {
		lft = left;

		rght = right;

		alignment = new BooleanToggle();
	}

	@Override
	public E get() {
		if(alignment.get()) {
			return lft;
		} else {
			return rght;
		}
	}

	@Override
	public E peek() {
		if(alignment.peek()) {
			return lft;
		} else {
			return rght;
		}
	}

	@Override
	public void set(boolean isLeft) {
		alignment.set(isLeft);
	}
}
