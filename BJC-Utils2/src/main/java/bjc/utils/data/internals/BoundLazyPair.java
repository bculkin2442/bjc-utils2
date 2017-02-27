package bjc.utils.data.internals;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import bjc.utils.data.IHolder;
import bjc.utils.data.IPair;
import bjc.utils.data.Identity;
import bjc.utils.data.LazyPair;

/**
 * Implements a lazy pair that has been bound
 */
public class BoundLazyPair<OldLeft, OldRight, NewLeft, NewRight>
		implements IPair<NewLeft, NewRight> {
	/*
	 * The supplier of the left value
	 */
	private Supplier<OldLeft>										leftSupplier;
	/*
	 * The supplier of the right value
	 */
	private Supplier<OldRight>										rightSupplier;

	/*
	 * The binder to transform values
	 */
	private BiFunction<OldLeft, OldRight, IPair<NewLeft, NewRight>>	binder;

	/*
	 * The bound pair
	 */
	private IPair<NewLeft, NewRight>								boundPair;

	/*
	 * Whether the pair has been bound yet
	 */
	private boolean													pairBound;

	public BoundLazyPair(Supplier<OldLeft> leftSupp,
			Supplier<OldRight> rightSupp,
			BiFunction<OldLeft, OldRight, IPair<NewLeft, NewRight>> bindr) {
		leftSupplier = leftSupp;
		rightSupplier = rightSupp;
		binder = bindr;
	}

	@Override
	public <BoundLeft, BoundRight> IPair<BoundLeft, BoundRight> bind(
			BiFunction<NewLeft, NewRight, IPair<BoundLeft, BoundRight>> bindr) {
		if (bindr == null) {
			throw new NullPointerException("Binder must not be null");
		}

		IHolder<IPair<NewLeft, NewRight>> newPair = new Identity<>(
				boundPair);
		IHolder<Boolean> newPairMade = new Identity<>(pairBound);

		Supplier<NewLeft> leftSupp = () -> {
			if (!newPairMade.getValue()) {
				newPair.replace(binder.apply(leftSupplier.get(),
						rightSupplier.get()));

				newPairMade.replace(true);
			}

			return newPair.unwrap((pair) -> pair.getLeft());
		};

		Supplier<NewRight> rightSupp = () -> {
			if (!newPairMade.getValue()) {
				newPair.replace(binder.apply(leftSupplier.get(),
						rightSupplier.get()));

				newPairMade.replace(true);
			}

			return newPair.unwrap((pair) -> pair.getRight());
		};

		return new BoundLazyPair<>(leftSupp, rightSupp, bindr);
	}

	@Override
	public <BoundLeft> IPair<BoundLeft, NewRight> bindLeft(
			Function<NewLeft, IPair<BoundLeft, NewRight>> leftBinder) {
		if (leftBinder == null) {
			throw new NullPointerException("Left binder must not be null");
		}

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
		if (rightBinder == null) {
			throw new NullPointerException(
					"Right binder must not be null");
		}

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
	public <OtherLeft, OtherRight, CombinedLeft, CombinedRight> IPair<CombinedLeft, CombinedRight> combine(
			IPair<OtherLeft, OtherRight> otherPair,
			BiFunction<NewLeft, OtherLeft, CombinedLeft> leftCombiner,
			BiFunction<NewRight, OtherRight, CombinedRight> rightCombiner) {
		if (otherPair == null) {
			throw new NullPointerException("Other pair must not be null");
		} else if (leftCombiner == null) {
			throw new NullPointerException(
					"Left combiner must not be null");
		} else if (rightCombiner == null) {
			throw new NullPointerException(
					"Right combiner must not be null");
		}

		return otherPair.bind((otherLeft, otherRight) -> {
			return bind((leftVal, rightVal) -> {
				return new LazyPair<>(
						leftCombiner.apply(leftVal, otherLeft),
						rightCombiner.apply(rightVal, otherRight));
			});
		});
	}

	@Override
	public <NewLeftType> IPair<NewLeftType, NewRight> mapLeft(
			Function<NewLeft, NewLeftType> mapper) {
		if (mapper == null) {
			throw new NullPointerException("Mapper must not be null");
		}

		Supplier<NewLeftType> leftSupp = () -> {
			if (!pairBound) {
				NewLeft leftVal = binder
						.apply(leftSupplier.get(), rightSupplier.get())
						.getLeft();

				return mapper.apply(leftVal);
			}

			return mapper.apply(boundPair.getLeft());
		};

		Supplier<NewRight> rightSupp = () -> {
			if (!pairBound) {
				return binder
						.apply(leftSupplier.get(), rightSupplier.get())
						.getRight();
			}

			return boundPair.getRight();
		};

		return new LazyPair<>(leftSupp, rightSupp);
	}

	@Override
	public <NewRightType> IPair<NewLeft, NewRightType> mapRight(
			Function<NewRight, NewRightType> mapper) {
		if (mapper == null) {
			throw new NullPointerException("Mapper must not be null");
		}

		Supplier<NewLeft> leftSupp = () -> {
			if (!pairBound) {
				return binder
						.apply(leftSupplier.get(), rightSupplier.get())
						.getLeft();
			}

			return boundPair.getLeft();
		};

		Supplier<NewRightType> rightSupp = () -> {
			if (!pairBound) {
				NewRight rightVal = binder
						.apply(leftSupplier.get(), rightSupplier.get())
						.getRight();

				return mapper.apply(rightVal);
			}

			return mapper.apply(boundPair.getRight());
		};

		return new LazyPair<>(leftSupp, rightSupp);
	}

	@Override
	public <MergedType> MergedType merge(
			BiFunction<NewLeft, NewRight, MergedType> merger) {
		if (merger == null) {
			throw new NullPointerException("Merger must not be null");
		}

		if (!pairBound) {
			boundPair = binder.apply(leftSupplier.get(),
					rightSupplier.get());

			pairBound = true;
		}

		return boundPair.merge(merger);
	}

	@Override
	public String toString() {
		if (pairBound) {
			return boundPair.toString();
		}

		return "(un-materialized)";
	}
}