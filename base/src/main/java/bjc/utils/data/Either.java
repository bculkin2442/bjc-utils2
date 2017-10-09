package bjc.utils.data;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a pair where only one side has a value
 *
 * @author ben
 * @param <LeftType>
 *                The type that could be on the left
 * @param <RightType>
 *                The type that could be on the right
 *
 */
public class Either<LeftType, RightType> implements IPair<LeftType, RightType> {
	/**
	 * Create a new either with the left value occupied
	 *
	 * @param <LeftType>
	 *                The type of the left value
	 * @param <RightType>
	 *                The type of the empty right value
	 * @param left
	 *                The value to put on the left
	 * @return An either with the left side occupied
	 */
	public static <LeftType, RightType> Either<LeftType, RightType> left(final LeftType left) {
		return new Either<>(left, null);
	}

	/**
	 * Create a new either with the right value occupied
	 *
	 * @param <LeftType>
	 *                The type of the empty left value
	 * @param <RightType>
	 *                The type of the right value
	 * @param right
	 *                The value to put on the right
	 * @return An either with the right side occupied
	 */
	public static <LeftType, RightType> Either<LeftType, RightType> right(final RightType right) {
		return new Either<>(null, right);
	}

	private LeftType leftVal;

	private RightType rightVal;

	private boolean isLeft;

	private Either(final LeftType left, final RightType right) {
		if (left == null) {
			rightVal = right;
		} else {
			leftVal = left;

			isLeft = true;
		}
	}

	@Override
	public <BoundLeft, BoundRight> IPair<BoundLeft, BoundRight> bind(
			final BiFunction<LeftType, RightType, IPair<BoundLeft, BoundRight>> binder) {
		if (binder == null) throw new NullPointerException("Binder must not be null");

		return binder.apply(leftVal, rightVal);
	}

	@Override
	public <BoundLeft> IPair<BoundLeft, RightType> bindLeft(
			final Function<LeftType, IPair<BoundLeft, RightType>> leftBinder) {
		if (leftBinder == null) throw new NullPointerException("Left binder must not be null");

		if (isLeft) return leftBinder.apply(leftVal);

		return new Either<>(null, rightVal);
	}

	@Override
	public <BoundRight> IPair<LeftType, BoundRight> bindRight(
			final Function<RightType, IPair<LeftType, BoundRight>> rightBinder) {
		if (rightBinder == null) throw new NullPointerException("Right binder must not be null");

		if (isLeft) return new Either<>(leftVal, null);

		return rightBinder.apply(rightVal);
	}

	@Override
	public <OtherLeft, OtherRight, CombinedLeft, CombinedRight> IPair<CombinedLeft, CombinedRight> combine(
			final IPair<OtherLeft, OtherRight> otherPair,
			final BiFunction<LeftType, OtherLeft, CombinedLeft> leftCombiner,
			final BiFunction<RightType, OtherRight, CombinedRight> rightCombiner) {
		if (otherPair == null)
			throw new NullPointerException("Other pair must not be null");
		else if (leftCombiner == null)
			throw new NullPointerException("Left combiner must not be null");
		else if (rightCombiner == null) throw new NullPointerException("Right combiner must not be null");

		if (isLeft) return otherPair.bind((otherLeft, otherRight) -> {
			return new Either<>(leftCombiner.apply(leftVal, otherLeft), null);
		});

		return otherPair.bind((otherLeft, otherRight) -> {
			return new Either<>(null, rightCombiner.apply(rightVal, otherRight));
		});
	}

	@Override
	public <NewLeft> IPair<NewLeft, RightType> mapLeft(final Function<LeftType, NewLeft> mapper) {
		if (mapper == null) throw new NullPointerException("Mapper must not be null");

		if (isLeft) return new Either<>(mapper.apply(leftVal), null);

		return new Either<>(null, rightVal);
	}

	@Override
	public <NewRight> IPair<LeftType, NewRight> mapRight(final Function<RightType, NewRight> mapper) {
		if (mapper == null) throw new NullPointerException("Mapper must not be null");

		if (isLeft) return new Either<>(leftVal, null);

		return new Either<>(null, mapper.apply(rightVal));
	}

	@Override
	public <MergedType> MergedType merge(final BiFunction<LeftType, RightType, MergedType> merger) {
		if (merger == null) throw new NullPointerException("Merger must not be null");

		return merger.apply(leftVal, rightVal);
	}

	@Override
	public int hashCode() {
		final int prime = 31;

		int result = 1;
		result = prime * result + (isLeft ? 1231 : 1237);
		result = prime * result + (leftVal == null ? 0 : leftVal.hashCode());
		result = prime * result + (rightVal == null ? 0 : rightVal.hashCode());

		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Either<?, ?>)) return false;

		final Either<?, ?> other = (Either<?, ?>) obj;

		if (isLeft != other.isLeft) return false;

		if (leftVal == null) {
			if (other.leftVal != null) return false;
		} else if (!leftVal.equals(other.leftVal)) return false;

		if (rightVal == null) {
			if (other.rightVal != null) return false;
		} else if (!rightVal.equals(other.rightVal)) return false;

		return true;
	}

	@Override
	public String toString() {
		return String.format("Either [leftVal='%s', rightVal='%s', isLeft=%s]", leftVal, rightVal, isLeft);
	}
}