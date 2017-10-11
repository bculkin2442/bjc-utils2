package bjc.utils.esodata;

/**
 * A tape changer is essentially a tape of tapes.
 *
 * It has a current tape that you can do operations to, but also operations to
 * add/remove other tapes.
 *
 * If there is no tape currently loaded into the changer, all the methods will
 * either return null/false.
 *
 * @param <T>
 * 	The element type of the tapes.
 */
public class TapeChanger<T> implements Tape<T> {
	/* Our list of tapes. */
	private Tape<Tape<T>> tapes;
	/* The current tape. */
	private Tape<T>       currentTape;

	/** Create a new empty tape changer. */
	public TapeChanger() {
		tapes = new SingleTape<>();
	}

	/**
	 * Create a new tape changer with the specified tapes.
	 *
	 * @param current
	 * 	The tape to mount first.
	 *
	 * @param others
	 * 	The tapes to put in this tape changer.
	 */
	@SafeVarargs
	public TapeChanger(final Tape<T> current, final Tape<T>... others) {
		this();

		tapes.insertBefore(current);

		for (final Tape<T> tp : others) {
			tapes.insertAfter(tp);
			tapes.right();
		}

		tapes.first();
		currentTape = tapes.item();
	}

	@Override
	public T item() {
		if (currentTape == null) return null;

		return currentTape.item();
	}

	@Override
	public void item(final T itm) {
		if (currentTape == null) return;

		currentTape.item(itm);
	}

	@Override
	public int size() {
		if (currentTape == null) return 0;

		return currentTape.size();
	}

	@Override
	public int position() {
		if (currentTape == null) return 0;

		return currentTape.position();
	}

	@Override
	public void insertBefore(final T itm) {
		if (currentTape == null) return;

		currentTape.insertBefore(itm);
	}

	@Override
	public void insertAfter(final T itm) {
		if (currentTape == null) return;

		currentTape.insertAfter(itm);
	}

	@Override
	public T remove() {
		if (currentTape == null) return null;

		return currentTape.remove();
	}

	@Override
	public void first() {
		if (currentTape == null) return;

		currentTape.first();
	}

	@Override
	public void last() {
		if (currentTape == null) return;

		currentTape.last();
	}

	@Override
	public boolean left() {
		return left(1);
	}

	@Override
	public boolean left(final int amt) {
		if (currentTape == null) return false;

		return currentTape.left(amt);
	}

	@Override
	public boolean right() {
		return right(1);
	}

	@Override
	public boolean right(final int amt) {
		if (currentTape == null) return false;

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
		if (currentTape == null) return;

		if (currentTape.isDoubleSided()) {
			((DoubleTape<T>) currentTape).flip();
		}
	}

	@Override
	public boolean isDoubleSided() {
		if (currentTape == null) return false;

		return currentTape.isDoubleSided();
	}

	/**
	 * Check if a tape is currently loaded.
	 *
	 * @return
	 * 	Whether or not a tape is loaded.
	 */
	public boolean isLoaded() {
		return currentTape != null;
	}

	/**
	 * Move to the next tape in the changer.
	 *
	 * Attempting to load a tape that isn't there won't eject the current
	 * tape.
	 *
	 * @return
	 * 	Whether or not the next tape was loaded.
	 */
	public boolean nextTape() {
		final boolean succ = tapes.right();

		if (succ) {
			currentTape = tapes.item();
		}

		return succ;
	}

	/**
	 * Move to the previous tape in the changer.
	 *
	 * Attempting to load a tape that isn't there won't eject the current
	 * tape.
	 *
	 * @return
	 * 	Whether or not the previous tape was loaded.
	 */
	public boolean prevTape() {
		final boolean succ = tapes.left();

		if (succ) {
			currentTape = tapes.item();
		}

		return succ;
	}

	/**
	 * Inserts a tape into the tape changer.
	 *
	 * Any currently loaded tape is ejected, and becomes the previous tape.
	 *
	 * The specified tape is loaded.
	 *
	 * @param tp
	 * 	The tape to insert and load.
	 */
	public void insertTape(final Tape<T> tp) {
		tapes.insertAfter(tp);
		tapes.right();

		currentTape = tapes.item();
	}

	/**
	 * Removes the current tape.
	 *
	 * Does nothing if there is not a tape loaded.
	 *
	 * Loads the previous tape, if there is one.
	 *
	 * @return
	 * 	The removed tape.
	 */
	public Tape<T> removeTape() {
		if (currentTape == null) return null;

		final Tape<T> tp = tapes.remove();
		currentTape = tapes.item();

		return tp;
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
	 * Get how many tapes are currently in the changer.
	 *
	 * @return
	 * 	How many tapes are currently in the changer.
	 */
	public int tapeCount() {
		return tapes.size();
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
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof TapeChanger<?>)) return false;

		final TapeChanger<?> other = (TapeChanger<?>) obj;

		if (currentTape == null) {
			if (other.currentTape != null) return false;
		} else if (!currentTape.equals(other.currentTape)) return false;

		if (tapes == null) {
			if (other.tapes != null) return false;
		} else if (!tapes.equals(other.tapes)) return false;

		return true;
	}

	@Override
	public String toString() {
		return String.format("TapeChanger [tapes=%s, currentTape='%s']", tapes, currentTape);
	}
}