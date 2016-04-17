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
	private LeftType	leftValue;
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
	public <BoundLeft> IPair<BoundLeft, RightType> bindLeft(
			Function<LeftType, IPair<BoundLeft, RightType>> leftBinder) {
		return leftBinder.apply(leftValue);
	}

	@Override
	public <BoundRight> IPair<LeftType, BoundRight> bindRight(
			Function<RightType, IPair<LeftType, BoundRight>> rightBinder) {
		return rightBinder.apply(rightValue);
	}

	@Override
	public <BoundLeft, BoundRight> IPair<BoundLeft, BoundRight> bind(
			BiFunction<LeftType, RightType, IPair<BoundLeft, BoundRight>> binder) {
		return binder.apply(leftValue, rightValue);
	}

	@Override
	public <MergedType> MergedType merge(
			BiFunction<LeftType, RightType, MergedType> merger) {
		return merger.apply(leftValue, rightValue);
	}
}