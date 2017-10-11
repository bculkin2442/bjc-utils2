package bjc.utils.esodata;

/**
 * Double-sided tape is essentially two tapes stuck together with a shared
 * cursor.
 *
 * The main way a double-sided tape differs is that it can be flipped, allowing
 * access to another set of data.
 *
 * However, there is only one cursor, and the position of the cursor on one side
 * is the inverse of the position on the other side.
 *
 * When one side is extended, a null will be inserted into the inactive side
 * regardless of the auto-extension policy of the tape. The policy will still be
 * respected for the active side.
 *
 * All operations that refer to the tape refer to the currently active side of
 * the tape, except for flip.
 *
 * Flip refers to the entire tape for 'obvious' reasons.
 *
 * @param <T>
 * 	The element type of the tape.
 *
 * @author bjculkin
 */
public class DoubleTape<T> implements Tape<T> {
	/* The front-side of the tape. */
	private Tape<T>	front;
	/* The back-side of the tape. */
	private Tape<T>	back;

	/** Create a new empty double-sided tape that doesn't autoextend. */
	public DoubleTape() {
		this(false);
	}

	/**
	 * Create a new empty double-sided tape that follows the specified
	 * auto-extension policy.
	 *
	 * @param autoExtnd
	 * 	Whether or not to auto-extend the tape to the right w/ nulls.
	 */
	public DoubleTape(final boolean autoExtnd) {
		front = new SingleTape<>(autoExtnd);
		back = new SingleTape<>(autoExtnd);
	}

	@Override
	public T item() {
		return front.item();
	}

	@Override
	public void item(final T itm) {
		front.item(itm);
	}

	@Override
	public int size() {
		return front.size();
	}

	@Override
	public int position() {
		return front.position();
	}

	@Override
	public void insertBefore(final T itm) {
		front.insertBefore(itm);
		back.insertAfter(null);
	}

	@Override
	public void insertAfter(final T itm) {
		front.insertAfter(itm);
		back.insertBefore(itm);
	}

	@Override
	public T remove() {
		back.remove();

		return front.remove();
	}

	@Override
	public void first() {
		front.first();
		back.last();
	}

	@Override
	public void last() {
		front.last();
		back.first();
	}

	@Override
	public boolean left() {
		return left(1);
	}

	@Override
	public boolean left(final int amt) {
		final boolean succ = front.left(amt);

		if (succ) {
			back.right(amt);
		}

		return succ;
	}

	@Override
	public boolean right() {
		return right(1);
	}

	@Override
	public boolean right(final int amt) {
		final boolean succ = front.right(amt);

		if (succ) {
			back.left(amt);
		}

		return succ;
	}

	/**
	 * Flips the tape.
	 *
	 * The active side becomes inactive, and the inactive side becomes
	 * active.
	 */
	public void flip() {
		final Tape<T> tmp = front;

		front = back;

		back = tmp;
	}

	@Override
	public boolean isDoubleSided() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (back == null ? 0 : back.hashCode());
		result = prime * result + (front == null ? 0 : front.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof DoubleTape<?>)) return false;

		final DoubleTape<?> other = (DoubleTape<?>) obj;

		if (back == null) {
			if (other.back != null) return false;
		} else if (!back.equals(other.back)) return false;

		if (front == null) {
			if (other.front != null) return false;
		} else if (!front.equals(other.front)) return false;

		return true;
	}

	@Override
	public String toString() {
		return String.format("DoubleTape [front=%s, back=%s]", front, back);
	}
}
