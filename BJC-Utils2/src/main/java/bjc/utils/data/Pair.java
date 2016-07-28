package bjc.utils.data;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A pair of values, with nothing special about them.
 * 
 * @author ben
 *
 * @param <LeftType>
 *            The type of the left value
 * @param <RightType>
 *            The type of the right value
 */
public class Pair<LeftType, RightType>
		implements IPair<LeftType, RightType> {
	// The left value
	private LeftType	leftValue;
	// The right value
	private RightType	rightValue;

	/**
	 * Create a new pair with both sides set to null
	 */
	public Pair() {
	}

	/**
	 * Create a new pair with both sides set to the specified values
	 * 
	 * @param left
	 *            The value of the left side
	 * @param right
	 *            The value of the right side
	 */
	public Pair(LeftType left, RightType right) {
		leftValue = left;
		rightValue = right;
	}

	@Override
	public <BoundLeft, BoundRight> IPair<BoundLeft, BoundRight> bind(
			BiFunction<LeftType, RightType, IPair<BoundLeft, BoundRight>> binder) {
		if (binder == null) {
			throw new NullPointerException("Binder must not be null.");
		}

		return binder.apply(leftValue, rightValue);
	}

	@Override
	public <BoundLeft> IPair<BoundLeft, RightType> bindLeft(
			Function<LeftType, IPair<BoundLeft, RightType>> leftBinder) {
		if (leftBinder == null) {
			throw new NullPointerException("Binder must not be null");
		}

		return leftBinder.apply(leftValue);
	}

	@Override
	public <BoundRight> IPair<LeftType, BoundRight> bindRight(
			Function<RightType, IPair<LeftType, BoundRight>> rightBinder) {
		if (rightBinder == null) {
			throw new NullPointerException("Binder must not be null");
		}

		return rightBinder.apply(rightValue);
	}

	@Override
	public <OtherLeft, OtherRight, CombinedLeft, CombinedRight> IPair<CombinedLeft, CombinedRight> combine(
			IPair<OtherLeft, OtherRight> otherPair,
			BiFunction<LeftType, OtherLeft, CombinedLeft> leftCombiner,
			BiFunction<RightType, OtherRight, CombinedRight> rightCombiner) {
		return otherPair.bind((otherLeft, otherRight) -> {
			return new Pair<>(leftCombiner.apply(leftValue, otherLeft),
					rightCombiner.apply(rightValue, otherRight));
		});
	}

	@Override
	public <NewLeft> IPair<NewLeft, RightType> mapLeft(
			Function<LeftType, NewLeft> mapper) {
		if (mapper == null) {
			throw new NullPointerException("Mapper must not be null");
		}

		return new Pair<>(mapper.apply(leftValue), rightValue);
	}

	@Override
	public <NewRight> IPair<LeftType, NewRight> mapRight(
			Function<RightType, NewRight> mapper) {
		if (mapper == null) {
			throw new NullPointerException("Mapper must not be null");
		}

		return new Pair<>(leftValue, mapper.apply(rightValue));
	}

	@Override
	public <MergedType> MergedType merge(
			BiFunction<LeftType, RightType, MergedType> merger) {
		if (merger == null) {
			throw new NullPointerException("Merger must not be null");
		}

		return merger.apply(leftValue, rightValue);
	}

	@Override
	public String toString() {
		return "pair[l=" + leftValue.toString() + ", r="
				+ rightValue.toString() + "]";
	}
}