package bjc.utils.data.internals;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import bjc.utils.data.IHolder;
import bjc.utils.data.IPair;
import bjc.utils.data.Identity;
import bjc.utils.data.LazyPair;

/*
 * A lazy pair, with only one side bound
 */
@SuppressWarnings("javadoc")
public class HalfBoundLazyPair<OldType, NewLeft, NewRight> implements IPair<NewLeft, NewRight> {
	private final Supplier<OldType> oldSupplier;

	private final Function<OldType, IPair<NewLeft, NewRight>> binder;

	private IPair<NewLeft, NewRight>	boundPair;
	private boolean				pairBound;

	public HalfBoundLazyPair(final Supplier<OldType> oldSupp,
			final Function<OldType, IPair<NewLeft, NewRight>> bindr) {
		oldSupplier = oldSupp;
		binder = bindr;
	}

	@Override
	public <BoundLeft, BoundRight> IPair<BoundLeft, BoundRight> bind(
			final BiFunction<NewLeft, NewRight, IPair<BoundLeft, BoundRight>> bindr) {
		final IHolder<IPair<NewLeft, NewRight>> newPair = new Identity<>(boundPair);
		final IHolder<Boolean> newPairMade = new Identity<>(pairBound);

		final Supplier<NewLeft> leftSupp = () -> {
			if (!newPairMade.getValue()) {
				newPair.replace(binder.apply(oldSupplier.get()));
				newPairMade.replace(true);
			}

			return newPair.unwrap((pair) -> pair.getLeft());
		};

		final Supplier<NewRight> rightSupp = () -> {
			if (!newPairMade.getValue()) {
				newPair.replace(binder.apply(oldSupplier.get()));
				newPairMade.replace(true);
			}

			return newPair.unwrap((pair) -> pair.getRight());
		};

		return new BoundLazyPair<>(leftSupp, rightSupp, bindr);
	}

	@Override
	public <BoundLeft> IPair<BoundLeft, NewRight> bindLeft(
			final Function<NewLeft, IPair<BoundLeft, NewRight>> leftBinder) {
		final Supplier<NewLeft> leftSupp = () -> {
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
			final Function<NewRight, IPair<NewLeft, BoundRight>> rightBinder) {
		final Supplier<NewRight> rightSupp = () -> {
			IPair<NewLeft, NewRight> newPair = boundPair;

			if (!pairBound) {
				newPair = binder.apply(oldSupplier.get());
			}

			return newPair.getRight();
		};

		return new HalfBoundLazyPair<>(rightSupp, rightBinder);
	}

	@Override
	public <OtherLeft, OtherRight, CombinedLeft, CombinedRight> IPair<CombinedLeft, CombinedRight> combine(
			final IPair<OtherLeft, OtherRight> otherPair,
			final BiFunction<NewLeft, OtherLeft, CombinedLeft> leftCombiner,
			final BiFunction<NewRight, OtherRight, CombinedRight> rightCombiner) {
		return otherPair.bind((otherLeft, otherRight) -> {
			return bind((leftVal, rightVal) -> {
				return new LazyPair<>(leftCombiner.apply(leftVal, otherLeft),
						rightCombiner.apply(rightVal, otherRight));
			});
		});
	}

	@Override
	public <NewLeftType> IPair<NewLeftType, NewRight> mapLeft(final Function<NewLeft, NewLeftType> mapper) {
		final Supplier<NewLeftType> leftSupp = () -> {
			if (pairBound) return mapper.apply(boundPair.getLeft());

			final NewLeft leftVal = binder.apply(oldSupplier.get()).getLeft();

			return mapper.apply(leftVal);
		};

		final Supplier<NewRight> rightSupp = () -> {
			if (pairBound) return boundPair.getRight();

			return binder.apply(oldSupplier.get()).getRight();
		};

		return new LazyPair<>(leftSupp, rightSupp);
	}

	@Override
	public <NewRightType> IPair<NewLeft, NewRightType> mapRight(final Function<NewRight, NewRightType> mapper) {
		final Supplier<NewLeft> leftSupp = () -> {
			if (pairBound) return boundPair.getLeft();

			return binder.apply(oldSupplier.get()).getLeft();
		};

		final Supplier<NewRightType> rightSupp = () -> {
			if (pairBound) return mapper.apply(boundPair.getRight());

			final NewRight rightVal = binder.apply(oldSupplier.get()).getRight();

			return mapper.apply(rightVal);
		};

		return new LazyPair<>(leftSupp, rightSupp);
	}

	@Override
	public <MergedType> MergedType merge(final BiFunction<NewLeft, NewRight, MergedType> merger) {
		if (!pairBound) {
			boundPair = binder.apply(oldSupplier.get());

			pairBound = true;
		}

		return boundPair.merge(merger);
	}
}