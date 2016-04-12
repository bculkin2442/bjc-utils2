package bjc.utils.data;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Holds a pair of values of two different types.
 * 
 * Is an eager variant of {@link IPair}
 * 
 * @author ben
 *
 * @param <L>
 *            The type of the thing held on the left (first)
 * @param <R>
 *            The type of the thing held on the right (second)
 */
public class Pair<L, R> implements IPair<L, R> {
	/**
	 * The left value of the pair
	 */
	protected L	leftValue;

	/**
	 * The right value of the pair
	 */
	protected R	rightValue;

	/**
	 * Create a new pair that holds two nulls.
	 */
	public Pair() {

	}

	/**
	 * Create a new pair holding the specified values.
	 * 
	 * @param left
	 *            The value to hold on the left.
	 * @param right
	 *            The value to hold on the right.
	 */
	public Pair(L left, R right) {
		leftValue = left;
		rightValue = right;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IPair#doWith(java.util.function.BiConsumer)
	 */
	@Override
	public void doWith(BiConsumer<L, R> action) {
		if (action == null) {
			throw new NullPointerException("Action must be non-null");
		}

		action.accept(leftValue, rightValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IPair#merge(java.util.function.BiFunction)
	 */
	@Override
	public <E> E merge(BiFunction<L, R, E> merger) {
		if (merger == null) {
			throw new NullPointerException("Merger must be non-null");
		}

		return merger.apply(leftValue, rightValue);
	}

	@Override
	public String toString() {
		String leftValueString;

		if (leftValue != null) {
			leftValueString = leftValue.toString();
		} else {
			leftValueString = "(null)";
		}

		String rightValueString;

		if (rightValue != null) {
			rightValueString = rightValue.toString();
		} else {
			rightValueString = "(null)";
		}

		return "pair[l=" + leftValueString + ", r=" + rightValueString
				+ "]";
	}

	@Override
	public <L2, R2> IPair<L2, R2> bind(
			BiFunction<L, R, IPair<L2, R2>> binder) {
		return binder.apply(leftValue, rightValue);
	}
}
