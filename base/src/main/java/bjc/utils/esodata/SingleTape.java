package bjc.utils.esodata;

import java.util.ArrayList;

/**
 * A tape is a one-dimensional array that can only be accessed in one position
 * at a time.
 *
 * A tape is essentially a 1D array with a cursor attached to it, and you can
 * only affect elements at that cursor. The size of the array is theoretically
 * unbounded to the right, but in practice bounded by available memory.
 *
 * You can choose whether or not you want the tape to automatically extend
 * itself to the right with null elements by specifying its auto-extension
 * policy.
 *
 * @param <T>
 * 	The element type of the tape.
 *
 * @author bjculkin
 */
public class SingleTape<T> implements Tape<T> {
	/* @NOTE
	 * 	Does this stuff still need to be protected? We're not trying to
	 * 	use inheritance to implement tape types any more, so I don't see
	 * 	any reason to not have it be private.
	 */
	/* Our backing store. */
	protected ArrayList<T>	backing;
	/* Our position in the list. */
	protected int		pos;
	/* Whether to auto-extend the list on the left with nulls. */
	protected boolean autoExtend;

	/**
	 * Create a new tape with the specified contents that doesn't
	 * autoextend.
	 */
	@SafeVarargs
	public SingleTape(T... vals) {
		autoExtend = false;

		backing = new ArrayList<>(vals.length);

		for(T val : vals) {
			backing.add(val);
		}
	}

	/** Create a new empty tape that doesn't autoextend. */
	public SingleTape() {
		this(false);
	}

	/**
	 * Create a new empty tape that follows the specified auto-extension
	 * policy.
	 *
	 * @param autoExtnd
	 * 	Whether or not to auto-extend the tape to the right w/ nulls.
	 */
	public SingleTape(final boolean autoExtnd) {
		autoExtend = autoExtnd;

		backing = new ArrayList<>();
	}

	@Override
	public T item() {
		return backing.get(pos);
	}

	@Override
	public void item(final T itm) {
		backing.set(pos, itm);
	}

	@Override
	public int size() {
		return backing.size();
	}

	@Override
	public int position() {
		return pos;
	}

	@Override
	public void insertBefore(final T itm) {
		backing.add(pos, itm);
	}

	@Override
	public void insertAfter(final T itm) {
		if (pos == backing.size() - 1) {
			backing.add(itm);
		} else {
			backing.add(pos + 1, itm);
		}
	}

	@Override
	public T remove() {
		final T res = backing.remove(pos);
		if (pos != 0) {
			pos -= 1;
		}
		return res;
	}

	@Override
	public void first() {
		pos = 0;
	}

	@Override
	public void last() {
		pos = backing.size() - 1;
	}

	@Override
	public boolean left() {
		return left(1);
	}

	@Override
	public boolean left(final int amt) {
		if (pos - amt < 0) return false;

		pos -= amt;
		return true;
	}

	@Override
	public boolean right() {
		return right(1);
	}

	@Override
	public boolean right(final int amt) {
		if (pos + amt >= backing.size() - 1) {
			if (autoExtend) {
				while (pos + amt >= backing.size() - 1) {
					backing.add(null);
				}
			} else return false;
		}

		pos += amt;
		return true;
	}

	@Override
	public boolean isDoubleSided() {
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;

		int result = 1;
		result = prime * result + (backing == null ? 0 : backing.hashCode());

		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof SingleTape<?>)) return false;

		final SingleTape<?> other = (SingleTape<?>) obj;

		if (backing == null) {
			if (other.backing != null) return false;
		} else if (!backing.equals(other.backing)) return false;

		return true;
	}

	@Override
	public String toString() {
		return String.format("SingleTape [backing=%s, pos=%s, autoExtend=%s]", backing, pos, autoExtend);
	}
}
