package bjc.utils.data;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A pair of values, with nothing special about them.
 *
 * @author ben
 *
 * @param <LeftType>
 *                The type of the left value
 * @param <RightType>
 *                The type of the right value
 */
public class Pair<LeftType, RightType> implements IPair<LeftType, RightType> {
	// The left value
	private LeftType leftValue;

	// The right value
	private RightType rightValue;

	/**
	 * Create a new pair with both sides set to null
	 */
	public Pair() {

	}

	/**
	 * Create a new pair with both sides set to the specified values
	 *
	 * @param left
	 *                The value of the left side
	 * @param right
	 *                The value of the right side
	 */
	public Pair(final LeftType left, final RightType right) {
		leftValue = left;
		rightValue = right;
	}

	@Override
	public <BoundLeft, BoundRight> IPair<BoundLeft, BoundRight> bind(
			final BiFunction<LeftType, RightType, IPair<BoundLeft, BoundRight>> binder) {
		if (binder == null) throw new NullPointerException("Binder must not be null.");

		return binder.apply(leftValue, rightValue);
	}

	@Override
	public <BoundLeft> IPair<BoundLeft, RightType> bindLeft(
			final Function<LeftType, IPair<BoundLeft, RightType>> leftBinder) {
		if (leftBinder == null) throw new NullPointerException("Binder must not be null");

		return leftBinder.apply(leftValue);
	}

	@Override
	public <BoundRight> IPair<LeftType, BoundRight> bindRight(
			final Function<RightType, IPair<LeftType, BoundRight>> rightBinder) {
		if (rightBinder == null) throw new NullPointerException("Binder must not be null");

		return rightBinder.apply(rightValue);
	}

	@Override
	public <OtherLeft, OtherRight, CombinedLeft, CombinedRight> IPair<CombinedLeft, CombinedRight> combine(
			final IPair<OtherLeft, OtherRight> otherPair,
			final BiFunction<LeftType, OtherLeft, CombinedLeft> leftCombiner,
			final BiFunction<RightType, OtherRight, CombinedRight> rightCombiner) {
		return otherPair.bind((otherLeft, otherRight) -> {
			final CombinedLeft left = leftCombiner.apply(leftValue, otherLeft);
			final CombinedRight right = rightCombiner.apply(rightValue, otherRight);

			return new Pair<>(left, right);
		});
	}

	@Override
	public <NewLeft> IPair<NewLeft, RightType> mapLeft(final Function<LeftType, NewLeft> mapper) {
		if (mapper == null) throw new NullPointerException("Mapper must not be null");

		return new Pair<>(mapper.apply(leftValue), rightValue);
	}

	@Override
	public <NewRight> IPair<LeftType, NewRight> mapRight(final Function<RightType, NewRight> mapper) {
		if (mapper == null) throw new NullPointerException("Mapper must not be null");

		return new Pair<>(leftValue, mapper.apply(rightValue));
	}

	@Override
	public <MergedType> MergedType merge(final BiFunction<LeftType, RightType, MergedType> merger) {
		if (merger == null) throw new NullPointerException("Merger must not be null");

		return merger.apply(leftValue, rightValue);
	}

	@Override
	public String toString() {
		return String.format("Pair [leftValue='%s', rightValue='%s']", leftValue, rightValue);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + (leftValue == null ? 0 : leftValue.hashCode());
		result = prime * result + (rightValue == null ? 0 : rightValue.hashCode());

		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Pair<?, ?>)) return false;

		final Pair<?, ?> other = (Pair<?, ?>) obj;

		if (leftValue == null) {
			if (other.leftValue != null) return false;
		} else if (!leftValue.equals(other.leftValue)) return false;

		if (rightValue == null) {
			if (other.rightValue != null) return false;
		} else if (!rightValue.equals(other.rightValue)) return false;

		return true;
	}
}
