package bjc.utils.data;

/**
 * A simple {@link ValueToggle} that swaps between true and false.
 *
 * @author EVE
 *
 */
public class BooleanToggle implements Toggle<Boolean> {
	private boolean val;

	/**
	 * Create a new, initially false, flip-flop.
	 */
	public BooleanToggle() {
		this(false);
	}

	/**
	 * Create a flip-flop with the specified initial value.
	 *
	 * @param initial
	 *                The initial value of the flip-flop.
	 */
	public BooleanToggle(final boolean initial) {
		val = initial;
	}

	@Override
	public Boolean get() {
		final boolean res = val;

		val = !res;

		return res;
	}

	@Override
	public Boolean peek() {
		return val;
	}

	@Override
	public void set(final boolean vl) {
		val = vl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;

		int result = 1;

		result = prime * result + (val ? 1231 : 1237);

		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof BooleanToggle)) return false;

		final BooleanToggle other = (BooleanToggle) obj;

		if (val != other.val) return false;

		return true;
	}

	@Override
	public String toString() {
		return String.format("BooleanToggle [val=%s]", val);
	}
}