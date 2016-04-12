package bjc.utils.data.experimental;

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
 */
public class LazyPair<LeftType, RightType>
		implements IPair<LeftType, RightType> {
	private static class HalfBoundLazyPair<OldType, NewLeft, NewRight>
			implements IPair<NewLeft, NewRight> {
		private Supplier<OldType>							oldSupplier;

		private Function<OldType, IPair<NewLeft, NewRight>>	binder;

		private IPair<NewLeft, NewRight>					boundPair;
		private boolean										pairBound;

		public HalfBoundLazyPair(Supplier<OldType> oldSupp,
				Function<OldType, IPair<NewLeft, NewRight>> bindr) {
			oldSupplier = oldSupp;
			binder = bindr;
		}

		@Override
		public <BoundLeft> IPair<BoundLeft, NewRight> bindLeft(
				Function<NewLeft, IPair<BoundLeft, NewRight>> leftBinder) {
			Supplier<NewLeft> leftSupp = () -> {
				IPair<NewLeft, NewRight> newPair = boundPair;

				if (!pairBound) {
					newPair = binder.apply(oldSupplier.get());
				}

				return newPair.getLeft();
			};

			return new HalfBoundLazyPair<>(leftSupp, leftBinder);
		}

		@Override
		public <BoundRight> IPair<NewLeft, BoundRight> bindRight(
				Function<NewRight, IPair<NewLeft, BoundRight>> rightBinder) {
			Supplier<NewRight> rightSupp = () -> {
				IPair<NewLeft, NewRight> newPair = boundPair;

				if (!pairBound) {
					newPair = binder.apply(oldSupplier.get());
				}

				return newPair.getRight();
			};

			return new HalfBoundLazyPair<>(rightSupp, rightBinder);
		}

		@Override
		public <BoundLeft, BoundRight> IPair<BoundLeft, BoundRight> bind(
				BiFunction<NewLeft, NewRight, IPair<BoundLeft, BoundRight>> bindr) {
			IHolder<IPair<NewLeft, NewRight>> newPair = new Identity<>(
					boundPair);
			IHolder<Boolean> newPairMade = new Identity<>(pairBound);

			Supplier<NewLeft> leftSupp = () -> {
				if (!newPairMade.getValue()) {
					newPair.replace(binder.apply(oldSupplier.get()));
					newPairMade.replace(true);
				}

				return newPair.unwrap((pair) -> pair.getLeft());
			};

			Supplier<NewRight> rightSupp = () -> {
				if (!newPairMade.getValue()) {
					newPair.replace(binder.apply(oldSupplier.get()));
					newPairMade.replace(true);
				}

				return newPair.unwrap((pair) -> pair.getRight());
			};

			return new BoundLazyPair<>(leftSupp, rightSupp, bindr);
		}

		@Override
		public <MergedType> MergedType merge(
				BiFunction<NewLeft, NewRight, MergedType> merger) {
			if (!pairBound) {
				boundPair = binder.apply(oldSupplier.get());

				pairBound = true;
			}

			return boundPair.merge(merger);
		}
	}

	private static class BoundLazyPair<OldLeft, OldRight, NewLeft, NewRight>
			implements IPair<NewLeft, NewRight> {
		private Supplier<OldLeft>										leftSupplier;
		private Supplier<OldRight>										rightSupplier;

		private BiFunction<OldLeft, OldRight, IPair<NewLeft, NewRight>>	binder;

		private IPair<NewLeft, NewRight>								boundPair;

		private boolean													pairBound;

		public BoundLazyPair(Supplier<OldLeft> leftSupp,
				Supplier<OldRight> rightSupp,
				BiFunction<OldLeft, OldRight, IPair<NewLeft, NewRight>> bindr) {
			leftSupplier = leftSupp;
			rightSupplier = rightSupp;
			binder = bindr;
		}

		@Override
		public <BoundLeft> IPair<BoundLeft, NewRight> bindLeft(
				Function<NewLeft, IPair<BoundLeft, NewRight>> leftBinder) {
			Supplier<NewLeft> leftSupp = () -> {
				IPair<NewLeft, NewRight> newPair = boundPair;

				if (!pairBound) {
					newPair = binder.apply(leftSupplier.get(),
							rightSupplier.get());
				}

				return newPair.getLeft();
			};

			return new HalfBoundLazyPair<>(leftSupp, leftBinder);
		}

		@Override
		public <BoundRight> IPair<NewLeft, BoundRight> bindRight(
				Function<NewRight, IPair<NewLeft, BoundRight>> rightBinder) {
			Supplier<NewRight> rightSupp = () -> {
				IPair<NewLeft, NewRight> newPair = boundPair;

				if (!pairBound) {
					newPair = binder.apply(leftSupplier.get(),
							rightSupplier.get());
				}

				return newPair.getRight();
			};

			return new HalfBoundLazyPair<>(rightSupp, rightBinder);
		}

		@Override
		public <BoundLeft, BoundRight> IPair<BoundLeft, BoundRight> bind(
				BiFunction<NewLeft, NewRight, IPair<BoundLeft, BoundRight>> bindr) {
			IHolder<IPair<NewLeft, NewRight>> newPair = new Identity<>(
					boundPair);
			IHolder<Boolean> newPairMade = new Identity<>(pairBound);

			return new BoundLazyPair<>(() -> {
				if (!newPairMade.getValue()) {
					newPair.replace(binder.apply(leftSupplier.get(),
							rightSupplier.get()));

					newPairMade.replace(false);
				}

				return newPair.unwrap((pair) -> pair.getLeft());
			}, () -> {
				if (!newPairMade.getValue()) {
					newPair.replace(binder.apply(leftSupplier.get(),
							rightSupplier.get()));

					newPairMade.replace(false);
				}

				return newPair.unwrap((pair) -> pair.getRight());
			}, bindr);
		}

		@Override
		public <MergedType> MergedType merge(
				BiFunction<NewLeft, NewRight, MergedType> merger) {
			if (!pairBound) {
				boundPair = binder.apply(leftSupplier.get(),
						rightSupplier.get());

				pairBound = true;
			}

			return boundPair.merge(merger);
		}
	}

	private LeftType			leftValue;
	private RightType			rightValue;

	private Supplier<LeftType>	leftSupplier;
	private Supplier<RightType>	rightSupplier;

	private boolean				leftMaterialized;
	private boolean				rightMaterialized;

	/**
	 * Create a new lazy pair, using the set value s
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
		leftSupplier = leftSupp;
		rightSupplier = rightSupp;

		leftMaterialized = false;
		rightMaterialized = false;
	}

	@Override
	public <BoundLeft> IPair<BoundLeft, RightType> bindLeft(
			Function<LeftType, IPair<BoundLeft, RightType>> leftBinder) {
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
	public <BoundLeft, BoundRight> IPair<BoundLeft, BoundRight> bind(
			BiFunction<LeftType, RightType, IPair<BoundLeft, BoundRight>> binder) {
		return new BoundLazyPair<>(leftSupplier, rightSupplier, binder);
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
}
