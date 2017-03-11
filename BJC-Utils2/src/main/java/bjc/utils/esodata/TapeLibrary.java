package bjc.utils.esodata;

import java.util.HashMap;
import java.util.Map;

/**
 * A tape changer is essentially a map of tapes.
 *
 * It has a current tape that you can do operations to, but also operations to add/remove other tapes.
 * 
 * If there is no tape currently loaded into the changer, all the methods will either return null/false.
 *
 * @param T The element type of the tapes.
 */
public class TapeLibrary<T> implements Tape<T> {
	private Map<String, Tape> tapes;
	private Tape<T>           currentTape;

	/**
	 * Create a new empty tape library.
	 */
	public TapeLibrary() {
		tapes = new HashMap<>();
	}

	/**
	 * Get the item the tape is currently on.
	 *
	 * @return The item the tape is on.
	 */
	public T item() {
		if(currentTape == null) return null;

		return currentTape.item();
	}

	/**
	 * Set the item the tape is currently on.
	 *
	 * @param itm The new value for the tape item.
	 */
	public void item(T itm) {
		if(currentTape == null) return;

		currentTape.item(itm);
	}

	/**
	 * Get the current number of elements in the tape.
	 *
	 * @return The current number of elements in the tape.
	 */
	public int size() {
		if(currentTape == null) return 0;

		return currentTape.size();
	}

	/**
	 * Insert an element before the current item.
	 *
	 * @param itm The item to add.
	 */
	public void insertBefore(T itm) {
		if(currentTape == null) return;

		currentTape.insertBefore(itm);
	}

	/**
	 * Insert an element after the current item.
	 */
	public void insertAfter(T itm) {
		if(currentTape == null) return;

		currentTape.insertAfter(itm);
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
		if(currentTape == null) return null;

		return currentTape.remove();
	}

	/**
	 * Move the cursor to the left-most position.
	 */
	public void first() {
		if(currentTape == null) return;

		currentTape.first();
	}

	/**
	 * Move the cursor the right-most position.
	 */
	public void last() {
		if(currentTape == null) return;

		currentTape.last();
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
		if(currentTape == null) return false;

		return currentTape.left(amt);
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
		if(currentTape == null) return false;

		return currentTape.right(amt);
	}

	/**
	 * Flips the tape.
	 *
	 * The active side becomes inactive, and the inactive side becomes active.
	 *
	 * If the current tape is not double-sided, does nothing.
	 */
	public void flip() {
		if(currentTape == null) return;

		if(currentTape.isDoubleSided()) {
			((DoubleTape<T>)currentTape).flip();
		}
	}

	@Override
	public boolean isDoubleSided() {
		if(currentTape == null) return false;

		return currentTape.isDoubleSided();
	}

	/**
	 * Check if a tape is currently loaded.
	 *
	 * @return Whether or not a tape is loaded.
	 */
	public boolean isLoaded() {
		return currentTape != null;
	}

	/**
	 * Move to the specified tape in the library.
	 *
	 * Attempting to load a tape that isn't there won't eject the current tape.
	 *
	 * @param label The label of the tape to load.
	 *
	 * @return Whether or not the next tape was loaded.
	 */
	public boolean switchTape(String label) {
		if(tapes.containsKey(label)) {
			currentTape = tapes.get(label);
			return true;
		}

		return false;
	}

	/**
	 * Inserts a tape into the tape library.
	 *
	 * Any currently loaded tape is ejected.
	 *
	 * The specified tape is loaded.
	 *
	 * Adding a duplicate tape will overwrite any existing types.
	 *
	 * @param The tape to insert and load.
	 */
	public void insertTape(String label, Tape<T> tp) {
		tapes.put(label, tp);

		currentTape = tp;
	}

	/**
	 * Remove a tape from the library.
	 *
	 * Does nothing if there is not a tape of that name loaded.
	 *
	 * @param label The tape to remove.
	 *
	 * @return The removed tape.
	 */
	public Tape<T> removeTape(String label) {
		return tapes.remove(label);
	}

	/**
	 * Ejects the current tape.
	 *
	 * Does nothing if no tape is loaded.
	 */
	public void eject() {
		currentTape = null;
	}

	/**
	 * Get how many tapes are currently in the library.
	 *
	 * @return How many tapes are currently in the library.
	 */
	public int tapeCount() {
		return tapes.size();
	}

	/**
	 * Check if a specific tape is loaded into the library.
	 *
	 * @param label The tape to check for.
	 *
	 * @return Whether or not a tape of that name exists
	 */
	public boolean hasTape(String label) {
		return tapes.containsKey(label);
	}
}