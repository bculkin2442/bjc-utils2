package bjc.utils.esodata;

import java.util.ArrayList;

/**
 * A tape is a one-dimensional array that can only be accessed in one position at a time.
 *
 * A tape is essentially a 1D array with a cursor attached to it, and you can only
 * affect elements at that cursor. The size of the array is theoretically unbounded
 * to the right, but in practice bounded by available memory.
 *
 * You can choose whether or not you want the tape to automatically extend itself to the
 * right with null elements by specifiying its auto-extension policy.
 *
 * @param T The element type of the tape.
 * @author bjculkin
 */
public class SingleTape<T> implements Tape<T> {
	protected ArrayList<T> backing;
	protected int pos;

	protected boolean autoExtend;

	/**
	 * Create a new empty tape that doesn't autoextend.
	 */
	public SingleTape() {
		this(false);
	}

	/**
	 * Create a new empty tape that follows the specified auto-extension policy.
	 *
	 * @param autoExtnd Whether or not to auto-extend the tape to the right
	 * 			w/ nulls.
	 */
	public SingleTape(boolean autoExtnd) {
		autoExtend = autoExtnd;

		backing = new ArrayList<>();
	}

	/**
	 * Get the item the tape is currently on.
	 *
	 * @return The item the tape is on.
	 */
	public T item() {
		return backing.get(pos);
	}

	/**
	 * Set the item the tape is currently on.
	 *
	 * @param itm The new value for the tape item.
	 */
	public void item(T itm) {
		backing.set(pos, itm);
	}

	/**
	 * Get the current number of elements in the tape.
	 *
	 * @return The current number of elements in the tape.
	 */
	public int size() {
		return backing.size();
	}

	/**
	 * Insert an element before the current item.
	 *
	 * @param itm The item to add.
	 */
	public void insertBefore(T itm) {
		backing.add(pos, itm);
	}

	/**
	 * Insert an element after the current item.
	 */
	public void insertAfter(T itm) {
		if(pos == (backing.size() - 1)) backing.add(itm);
		else							backing.add(pos+1, itm);
	}

	/**
	 * Remove the current element.
	 * 
	 * Also moves the cursor back one step if possible to maintain
	 * relative position.
	 *
	 * @return The removed item.
	 */
	public T remove() {
		T res = backing.remove(pos);
		if(pos != 0) pos -= 1;
		return res;
	}

	/**
	 * Move the cursor to the left-most position.
	 */
	public void first() {
		pos = 0;
	}

	/**
	 * Move the cursor the right-most position.
	 */
	public void last() {
		pos = backing.size() - 1;
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
		if((pos - amt) < 0) return false;

		pos -= amt;
		return true;
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
		if((pos + amt) >= (backing.size() - 1)) {
			if(autoExtend) {
				while((pos + amt) >= (backing.size() - 1)) {
					backing.add(null);
				}
			} else {
				return false;
			}
		}

		pos += amt;
		return true;
	}

	@Override
	public boolean isDoubleSided() {
		return false;
	}
}