package bjc.utils.esodata;

/**
 * Double-sided tape is essentially two tapes stuck together with a shared cursor. 
 *
 * The main way a double-sided tape differs is that it can be flipped, allowing access to
 * another set of data.
 *
 * However, there is only one cursor, and the position of the cursor on one side is the inverse
 * of the position on the other side.
 *
 * When one side is extended, a null will be inserted into the inactive side regardless of the
 * auto-extension policy of the tape. The policy will still be respected for the active side.
 *
 * All operations that refer to the tape refer to the currently active side of the tape, except for flip.
 * 
 * Flip refers to the entire tape for 'obvious' reasons.
 *
 * @param T The element type of the tape.
 * @author bjculkin
 */
public class DoubleTape<T> implements Tape<T> {
	private Tape<T> front;
	private Tape<T> back;

	/**
	 * Create a new empty double-sided tape that doesn't autoextend.
	 */
	public DoubleTape() {
		this(false);
	}

	/**
	 * Create a new empty double-sided tape that follows the specified auto-extension policy.
	 *
	 * @param autoExtnd Whether or not to auto-extend the tape to the right
	 * 			w/ nulls.
	 */
	public DoubleTape(boolean autoExtnd) {
		front = new SingleTape<>(autoExtnd);
		back  = new SingleTape<>(autoExtnd);
	}

	/**
	 * Get the item the tape is currently on.
	 *
	 * @return The item the tape is on.
	 */
	public T item() {
		return front.item();
	}

	/**
	 * Set the item the tape is currently on.
	 *
	 * @param itm The new value for the tape item.
	 */
	public void item(T itm) {
		front.item(itm);
	}

	/**
	 * Get the current number of elements in the tape.
	 *
	 * @return The current number of elements in the tape.
	 */
	public int size() {
		return front.size();
	}

	/**
	 * Insert an element before the current item.
	 *
	 * @param itm The item to add.
	 */
	public void insertBefore(T itm) {
		front.insertBefore(itm);
		back.insertAfter(null);
	}

	/**
	 * Insert an element after the current item.
	 */
	public void insertAfter(T itm) {
		front.insertAfter(itm);
		back.insertBefore(itm);
	}

	/**
	 * Remove the current element.
	 * 
	 * Also moves the cursor back one step if possible to maintain
	 * relative position, and removes the corresponding item from the non-active side
	 *
	 * @return The removed item from the active side.
	 */
	public T remove() {
		back.remove();

		return front.remove();
	}

	/**
	 * Move the cursor to the left-most position.
	 */
	public void first() {
		front.first();
		back.last();
	}

	/**
	 * Move the cursor the right-most position.
	 */
	public void last() {
		front.last();
		back.first();
	}

	/**
	 * Move the cursor one space left.
	 *
	 * The cursor can't go past zero.
	 *
	 * @return True if the cursor was moved left.
	 */
	public boolean left() {
		return left(1);
	}

	/**
	 * Move the cursor the specified amount left.
	 *
	 * The cursor can't go past zero.
	 * Attempts to move the cursor by amounts that would exceed zero
	 * don't move the cursor at all.
	 *
	 * @param amt The amount to attempt to move the cursor left.
	 *
	 * @return True if the cursor was moved left.
	 */
	public boolean left(int amt) {
		boolean succ = front.left(amt);

		if(succ) back.right(amt);

		return succ;
	}


	/**
	 * Move the cursor one space right.
	 *
	 * Moving the cursor right will auto-extend the tape if that is enabled.
	 *
	 * @return Whether the cursor was moved right.
	 */
	public boolean right() {
		return right(1);
	}

	/**
	 * Move the cursor the specified amount right.
	 *
	 * Moving the cursor right will auto-extend the tape if that is enabled.
	 *
	 * @param amt The amount to move the cursor right by.
	 *
	 * @return Whether the cursor was moved right.
	 */
	public boolean right(int amt) {
		boolean succ = front.right(amt);

		if(succ) back.left(amt);

		return succ;
	}

	/**
	 * Flips the tape.
	 *
	 * The active side becomes inactive, and the inactive side becomes active.
	 */
	public void flip() {
		Tape<T> tmp = front;

		front = back;

		back = tmp;
	}

	@Override
	public boolean isDoubleSided() {
		return true;
	}
}
