package bjc.utils.esodata;

import java.util.HashMap;
import java.util.Map;

/**
 * A tape changer is essentially a map of tapes.
 *
 * It has a current tape that you can do operations to, but also operations to
 * add/remove other tapes.
 *
 * If there is no tape currently loaded into the changer, all the methods will
 * either return null/false.
 *
 * @param <T>
 *        The element type of the tapes.
 */
public class TapeLibrary<T> implements Tape<T> {
	/* Our backing store of tapes. */
	private final Map<String, Tape<T>> tapes;
	/* The current tape. */
	private Tape<T> currentTape;

	/** Create a new empty tape library. */
	public TapeLibrary() {
		tapes = new HashMap<>();
	}

	@Override
	public T item() {
		if(currentTape == null) return null;

		return currentTape.item();
	}

	@Override
	public void item(final T itm) {
		if(currentTape == null) return;

		currentTape.item(itm);
	}

	@Override
	public int size() {
		if(currentTape == null) return 0;

		return currentTape.size();
	}

	@Override
	public int position() {
		if(currentTape == null) return 0;

		return currentTape.position();
	}

	@Override
	public void insertBefore(final T itm) {
		if(currentTape == null) return;

		currentTape.insertBefore(itm);
	}

	@Override
	public void insertAfter(final T itm) {
		if(currentTape == null) return;

		currentTape.insertAfter(itm);
	}

	@Override
	public T remove() {
		if(currentTape == null) return null;

		return currentTape.remove();
	}

	@Override
	public void first() {
		if(currentTape == null) return;

		currentTape.first();
	}

	@Override
	public void last() {
		if(currentTape == null) return;

		currentTape.last();
	}

	@Override
	public boolean left() {
		return left(1);
	}

	@Override
	public boolean left(final int amt) {
		if(currentTape == null) return false;

		return currentTape.left(amt);
	}

	@Override
	public boolean right() {
		return right(1);
	}

	@Override
	public boolean right(final int amt) {
		if(currentTape == null) return false;

		return currentTape.right(amt);
	}

	/**
	 * Flips the tape.
	 *
	 * The active side becomes inactive, and the inactive side becomes
	 * active.
	 *
	 * If the current tape is not double-sided, does nothing.
	 */
	public void flip() {
		if(currentTape == null) return;

		if(currentTape.isDoubleSided()) {
			((DoubleTape<T>) currentTape).flip();
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
	 * Attempting to load a tape that isn't there won't eject the current
	 * tape.
	 *
	 * @param label
	 *        The label of the tape to load.
	 *
	 * @return Whether or not the next tape was loaded.
	 */
	public boolean switchTape(final String label) {
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
	 * @param label
	 *        The label of the tape to add.
	 *
	 * @param tp
	 *        The tape to insert and load.
	 */
	public void insertTape(final String label, final Tape<T> tp) {
		tapes.put(label, tp);

		currentTape = tp;
	}

	/**
	 * Remove a tape from the library.
	 *
	 * Does nothing if there is not a tape of that name loaded.
	 *
	 * @param label
	 *        The tape to remove.
	 *
	 * @return The removed tape.
	 */
	public Tape<T> removeTape(final String label) {
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
	 * @param label
	 *        The tape to check for.
	 *
	 * @return Whether or not a tape of that name exists
	 */
	public boolean hasTape(final String label) {
		return tapes.containsKey(label);
	}

	@Override
	public int hashCode() {
		final int prime = 31;

		int result = 1;
		result = prime * result + (currentTape == null ? 0 : currentTape.hashCode());
		result = prime * result + (tapes == null ? 0 : tapes.hashCode());

		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof TapeLibrary<?>)) return false;

		final TapeLibrary<?> other = (TapeLibrary<?>) obj;

		if(currentTape == null) {
			if(other.currentTape != null) return false;
		} else if(!currentTape.equals(other.currentTape)) return false;

		if(tapes == null) {
			if(other.tapes != null) return false;
		} else if(!tapes.equals(other.tapes)) return false;

		return true;
	}

	@Override
	public String toString() {
		return String.format("TapeLibrary [tapes=%s, currentTape='%s']", tapes, currentTape);
	}
}
