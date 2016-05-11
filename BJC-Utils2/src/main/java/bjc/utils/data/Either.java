package bjc.utils.data;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a pair where only one side has a value
 * 
 * @author ben
 * @param <LeftType>
 *            The type that could be on the left
 * @param <RightType>
 *            The type that could be on the right
 *
 */
public class Either<LeftType, RightType>
		implements IPair<LeftType, RightType> {
	private LeftType	leftVal;
	private RightType	rightVal;

	private boolean		isLeft;

	private Either(LeftType left, RightType right) {
		if (left == null) {
			rightVal = right;
		} else {
			leftVal = left;

			isLeft = true;
		}
	}

	/**
	 * Create a new either with the left value occupied
	 * 
	 * @param <LeftType>
	 *            The type of the left value
	 * @param <RightType>
	 *            The type of the empty right value
	 * @param left
	 *            The value to put on the left
	 * @return An either with the left side occupied
	 */
	public static <LeftType, RightType> Either<LeftType, RightType>
			fromLeft(LeftType left) {
		return new Either<>(left, null);
	}

	/**
	 * Create a new either with the right value occupied
	 * 
	 * @param <LeftType>
	 *            The type of the empty left value
	 * @param <RightType>
	 *            The type of the right value
	 * @param right
	 *            The value to put on the right
	 * @return An either with the right side occupied
	 */
	public static <LeftType, RightType> Either<LeftType, RightType>
			fromRight(RightType right) {
		return new Either<>(null, right);
	}

	@Override
	public <BoundLeft, BoundRight> IPair<BoundLeft, BoundRight> bind(
			BiFunction<LeftType, RightType, IPair<BoundLeft, BoundRight>> binder) {
		return binder.apply(leftVal, rightVal);
	}

	@Override
	public <BoundLeft> IPair<BoundLeft, RightType> bindLeft(
			Function<LeftType, IPair<BoundLeft, RightType>> leftBinder) {
		if (isLeft) {
			return leftBinder.apply(leftVal);
		}

		return new Either<>(null, rightVal);
	}

	@Override
	public <BoundRight> IPair<LeftType, BoundRight> bindRight(
			Function<RightType, IPair<LeftType, BoundRight>> rightBinder) {
		if (isLeft) {
			return new Either<>(leftVal, null);
		}

		return rightBinder.apply(rightVal);
	}

	@Override
	public <NewLeft> IPair<NewLeft, RightType>
			mapLeft(Function<LeftType, NewLeft> mapper) {
		if (isLeft) {
			return new Either<>(mapper.apply(leftVal), null);
		}

		return new Either<>(null, rightVal);
	}

	@Override
	public <NewRight> IPair<LeftType, NewRight>
			mapRight(Function<RightType, NewRight> mapper) {
		if (isLeft) {
			return new Either<>(leftVal, null);
		}

		return new Either<>(null, mapper.apply(rightVal));
	}

	@Override
	public <MergedType> MergedType
			merge(BiFunction<LeftType, RightType, MergedType> merger) {
		return merger.apply(leftVal, rightVal);
	}

	@Override
	public <OtherLeft, OtherRight, CombinedLeft, CombinedRight>
			IPair<CombinedLeft, CombinedRight>
			combine(IPair<OtherLeft, OtherRight> otherPair,
					BiFunction<LeftType, OtherLeft, CombinedLeft> leftCombiner,
					BiFunction<RightType, OtherRight, CombinedRight> rightCombiner) {
		if (isLeft) {
			return otherPair.bind((otherLeft, otherRight) -> {
				return new Either<>(leftCombiner.apply(leftVal, otherLeft),
						null);
			});
		}

		return otherPair.bind((otherLeft, otherRight) -> {
			return new Either<>(null,
					rightCombiner.apply(rightVal, otherRight));
		});
	}
}
