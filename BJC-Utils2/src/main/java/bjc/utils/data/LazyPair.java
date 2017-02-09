package bjc.utils.data;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A lazy implementation of a pair
 * 
 * @author ben
 *
 * @param <LeftType>
 *            The type on the left side of the pair
 * @param <RightType>
 *            The type on the right side of the pair
 * 
 */
public class LazyPair<LeftType, RightType>
		implements IPair<LeftType, RightType> {
	private LeftType			leftValue;
	private RightType			rightValue;

	private Supplier<LeftType>	leftSupplier;
	private Supplier<RightType>	rightSupplier;

	private boolean				leftMaterialized;
	private boolean				rightMaterialized;

	/**
	 * Create a new lazy pair, using the set values
	 * 
	 * @param leftVal
	 *            The value for the left side of the pair
	 * @param rightVal
	 *            The value for the right side of the pair
	 */
	public LazyPair(LeftType leftVal, RightType rightVal) {
		leftValue = leftVal;
		rightValue = rightVal;

		leftMaterialized = true;
		rightMaterialized = true;
	}

	/**
	 * Create a new lazy pair from the given value sources
	 * 
	 * @param leftSupp
	 *            The source for a value on the left side of the pair
	 * @param rightSupp
	 *            The source for a value on the right side of the pair
	 */
	public LazyPair(Supplier<LeftType> leftSupp,
			Supplier<RightType> rightSupp) {
		// Use single suppliers to catch double-instantiation bugs
		leftSupplier = new SingleSupplier<>(leftSupp);
		rightSupplier = new SingleSupplier<>(rightSupp);

		leftMaterialized = false;
		rightMaterialized = false;
	}

	@Override
	public <BoundLeft, BoundRight> IPair<BoundLeft, BoundRight> bind(
			BiFunction<LeftType, RightType, IPair<BoundLeft, BoundRight>> binder) {
		return new BoundLazyPair<>(leftSupplier, rightSupplier, binder);
	}

	@Override
	public <BoundLeft> IPair<BoundLeft, RightType> bindLeft(Function<LeftType, IPair<BoundLeft, RightType>> leftBinder) {
		Supplier<LeftType> leftSupp = () -> {
			if (leftMaterialized) {
				return leftValue;
			}

			return leftSupplier.get();
		};

		return new HalfBoundLazyPair<>(leftSupp, leftBinder);
	}

	@Override
	public <BoundRight> IPair<LeftType, BoundRight> bindRight(
			Function<RightType, IPair<LeftType, BoundRight>> rightBinder) {
		Supplier<RightType> rightSupp = () -> {
			if (rightMaterialized) {
				return rightValue;
			}

			return rightSupplier.get();
		};

		return new HalfBoundLazyPair<>(rightSupp, rightBinder);
	}

	@Override
	public <OtherLeft, OtherRight, CombinedLeft,
			CombinedRight> IPair<CombinedLeft, CombinedRight> combine(
					IPair<OtherLeft, OtherRight> otherPair,
					BiFunction<LeftType, OtherLeft,
							CombinedLeft> leftCombiner,
					BiFunction<RightType, OtherRight,
							CombinedRight> rightCombiner) {
		return otherPair.bind((otherLeft, otherRight) -> {
			return bind((leftVal, rightVal) -> {
				return new LazyPair<>(
						leftCombiner.apply(leftVal, otherLeft),
						rightCombiner.apply(rightVal, otherRight));
			});
		});
	}

	@Override
	public LeftType getLeft() {
		if (!leftMaterialized) {
			leftValue = leftSupplier.get();

			leftMaterialized = true;
		}

		return leftValue;
	}

	@Override
	public RightType getRight() {
		if (!rightMaterialized) {
			rightValue = rightSupplier.get();

			rightMaterialized = true;
		}

		return rightValue;
	}

	@Override
	public <NewLeft> IPair<NewLeft, RightType> mapLeft(
			Function<LeftType, NewLeft> mapper) {
		Supplier<NewLeft> leftSupp = () -> {
			if (leftMaterialized) {
				return mapper.apply(leftValue);
			}

			return mapper.apply(leftSupplier.get());
		};

		Supplier<RightType> rightSupp = () -> {
			if (rightMaterialized) {
				return rightValue;
			}

			return rightSupplier.get();
		};

		return new LazyPair<>(leftSupp, rightSupp);
	}

	@Override
	public <NewRight> IPair<LeftType, NewRight> mapRight(
			Function<RightType, NewRight> mapper) {
		Supplier<LeftType> leftSupp = () -> {
			if (leftMaterialized) {
				return leftValue;
			}

			return leftSupplier.get();
		};

		Supplier<NewRight> rightSupp = () -> {
			if (rightMaterialized) {
				return mapper.apply(rightValue);
			}

			return mapper.apply(rightSupplier.get());
		};

		return new LazyPair<>(leftSupp, rightSupp);
	}

	@Override
	public <MergedType> MergedType merge(
			BiFunction<LeftType, RightType, MergedType> merger) {
		if (!leftMaterialized) {
			leftValue = leftSupplier.get();

			leftMaterialized = true;
		}

		if (!rightMaterialized) {
			rightValue = rightSupplier.get();

			rightMaterialized = true;
		}

		return merger.apply(leftValue, rightValue);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("pair[l=");

		if (leftMaterialized) {
			sb.append(leftValue.toString());
		} else {
			sb.append("(un-materialized)");
		}

		sb.append(", r=");

		if (rightMaterialized) {
			sb.append(rightValue.toString());
		} else {
			sb.append("(un-materialized)");
		}

		sb.append("]");

		return sb.toString();
	}
}
