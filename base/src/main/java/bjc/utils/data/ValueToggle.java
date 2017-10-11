package bjc.utils.data;

/**
 * A simple implementation of {@link Toggle}.
 *
 * @author EVE
 *
 * @param <E>
 * 	The type of value to toggle between.
 */
public class ValueToggle<E> implements Toggle<E> {
	/* Our left value. */
	private final E	lft;
	/* Our right value. */
	private final E	rght;
	/* Our alignment. */
	private final BooleanToggle alignment;

	/**
	 * Create a new toggle.
	 *
	 * All toggles start right-aligned.
	 *
	 * @param left
	 * 	The value when the toggle is left-aligned.
	 *
	 * @param right
	 * 	The value when the toggle is right-aligned.
	 */
	public ValueToggle(final E left, final E right) {
		lft = left;

		rght = right;

		alignment = new BooleanToggle();
	}

	@Override
	public E get() {
		if (alignment.get()) {
			return lft;
		} else {
			return rght;
		}
	}

	@Override
	public E peek() {
		if (alignment.peek()) {
			return lft;
		} else {
			return rght;
		}
	}

	@Override
	public void set(final boolean isLeft) {
		alignment.set(isLeft);
	}
}
