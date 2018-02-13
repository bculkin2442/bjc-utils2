package bjc.utils.data.internals;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import bjc.utils.data.IHolder;
import bjc.utils.data.IPair;
import bjc.utils.data.Identity;
import bjc.utils.data.LazyPair;

/*
 * @NOTE
 * 	I am not convinced that this code works correctly. Tests should be
 * 	written to make sure things only ever get instantiated once.
 *
 * 	Namely, my main concern is to whether the places that bind the pair
 * 	without setting pairBound are doing the right thing.
 */
/**
 * A lazy pair, with only one side bound.
 *
 * @author Ben Culkin
 */
@SuppressWarnings("javadoc")
public class HalfBoundLazyPair<OldType, NewLeft, NewRight> implements IPair<NewLeft, NewRight> {
	/* The supplier of the old value. */
	private final Supplier<OldType> oldSupplier;

	/* The function to transform the old value into a new pair. */
	private final Function<OldType, IPair<NewLeft, NewRight>> binder;

	/* The new bound pair. */
	private IPair<NewLeft, NewRight> boundPair;
	/* Has the pair been bound yet or not? */
	private boolean pairBound;

	/**
	 * Create a new half-bound lazy pair.
	 *
	 * @param oldSupp
	 *        The supplier of the old value.
	 *
	 * @param bindr
	 *        The function to use to create the pair from the old value.
	 */
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
			if(!newPairMade.getValue()) {
				/* Bind the pair if it hasn't been bound yet. */
				newPair.replace(binder.apply(oldSupplier.get()));
				newPairMade.replace(true);
			}

			return newPair.unwrap((pair) -> pair.getLeft());
		};

		final Supplier<NewRight> rightSupp = () -> {
			if(!newPairMade.getValue()) {
				/* Bind the pair if it hasn't been bound yet. */
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

			if(!pairBound) {
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

			if(!pairBound) {
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
				CombinedLeft cLeft = leftCombiner.apply(leftVal, otherLeft);
				CombinedRight cRight = rightCombiner.apply(rightVal, otherRight);

				return new LazyPair<>(cLeft, cRight);
			});
		});
	}

	@Override
	public <NewLeftType> IPair<NewLeftType, NewRight> mapLeft(final Function<NewLeft, NewLeftType> mapper) {
		final Supplier<NewLeftType> leftSupp = () -> {
			if(pairBound) return mapper.apply(boundPair.getLeft());

			final NewLeft leftVal = binder.apply(oldSupplier.get()).getLeft();

			return mapper.apply(leftVal);
		};

		final Supplier<NewRight> rightSupp = () -> {
			if(pairBound) return boundPair.getRight();

			return binder.apply(oldSupplier.get()).getRight();
		};

		return new LazyPair<>(leftSupp, rightSupp);
	}

	@Override
	public <NewRightType> IPair<NewLeft, NewRightType> mapRight(final Function<NewRight, NewRightType> mapper) {
		final Supplier<NewLeft> leftSupp = () -> {
			if(pairBound) return boundPair.getLeft();

			return binder.apply(oldSupplier.get()).getLeft();
		};

		final Supplier<NewRightType> rightSupp = () -> {
			if(pairBound) return mapper.apply(boundPair.getRight());

			final NewRight rightVal = binder.apply(oldSupplier.get()).getRight();

			return mapper.apply(rightVal);
		};

		return new LazyPair<>(leftSupp, rightSupp);
	}

	@Override
	public <MergedType> MergedType merge(final BiFunction<NewLeft, NewRight, MergedType> merger) {
		if(!pairBound) {
			boundPair = binder.apply(oldSupplier.get());

			pairBound = true;
		}

		return boundPair.merge(merger);
	}
}
