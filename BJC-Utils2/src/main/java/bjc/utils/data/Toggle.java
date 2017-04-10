package bjc.utils.data;

/**
 * A stateful holder that swaps between two values of the same type.
 *
 * @author EVE
 *
 * @param <E>
 *                The value stored in the toggle.
 */
public interface Toggle<E> {
	/**
	 * Retrieve the currently-aligned value of this toggle, and swap the
	 * alignment.
	 *
	 * @return The previously-aligned value.
	 */
	E get();

	/**
	 * Retrieve the currently-aligned value without altering the alignment.
	 *
	 * @return The currently-aligned value.
	 */
	E peek();

	/**
	 * Change the alignment of the toggle.
	 *
	 * @param isLeft
	 *                Whether the toggle should be left-aligned or not.
	 */
	void set(boolean isLeft);

}