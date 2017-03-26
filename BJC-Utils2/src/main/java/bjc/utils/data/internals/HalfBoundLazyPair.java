package bjc.utils.data.internals;

import bjc.utils.data.IHolder;
import bjc.utils.data.IPair;
import bjc.utils.data.Identity;
import bjc.utils.data.LazyPair;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/*
 * A lazy pair, with only one side bound
 */
@SuppressWarnings("javadoc")
public class HalfBoundLazyPair<OldType, NewLeft, NewRight> implements IPair<NewLeft, NewRight> {
	private Supplier<OldType> oldSupplier;

	private Function<OldType, IPair<NewLeft, NewRight>> binder;

	private IPair<NewLeft, NewRight>	boundPair;
	private boolean				pairBound;

	public HalfBoundLazyPair(Supplier<OldType> oldSupp, Function<OldType, IPair<NewLeft, NewRight>> bindr) {
		oldSupplier = oldSupp;
		binder = bindr;
	}

	@Override
	public <BoundLeft, BoundRight> IPair<BoundLeft, BoundRight> bind(
			BiFunction<NewLeft, NewRight, IPair<BoundLeft, BoundRight>> bindr) {
		IHolder<IPair<NewLeft, NewRight>> newPair = new Identity<>(boundPair);
		IHolder<Boolean> newPairMade = new Identity<>(pairBound);

		Supplier<NewLeft> leftSupp = () -> {
			if(!newPairMade.getValue()) {
				newPair.replace(binder.apply(oldSupplier.get()));
				newPairMade.replace(true);
			}

			return newPair.unwrap((pair) -> pair.getLeft());
		};

		Supplier<NewRight> rightSupp = () -> {
			if(!newPairMade.getValue()) {
				newPair.replace(binder.apply(oldSupplier.get()));
				newPairMade.replace(true);
			}

			return newPair.unwrap((pair) -> pair.getRight());
		};

		return new BoundLazyPair<>(leftSupp, rightSupp, bindr);
	}

	@Override
	public <BoundLeft> IPair<BoundLeft, NewRight> bindLeft(
			Function<NewLeft, IPair<BoundLeft, NewRight>> leftBinder) {
		Supplier<NewLeft> leftSupp = () -> {
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
			Function<NewRight, IPair<NewLeft, BoundRight>> rightBinder) {
		Supplier<NewRight> rightSupp = () -> {
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
			IPair<OtherLeft, OtherRight> otherPair,
			BiFunction<NewLeft, OtherLeft, CombinedLeft> leftCombiner,
			BiFunction<NewRight, OtherRight, CombinedRight> rightCombiner) {
		return otherPair.bind((otherLeft, otherRight) -> {
			return bind((leftVal, rightVal) -> {
				return new LazyPair<>(leftCombiner.apply(leftVal, otherLeft),
						rightCombiner.apply(rightVal, otherRight));
			});
		});
	}

	@Override
	public <NewLeftType> IPair<NewLeftType, NewRight> mapLeft(Function<NewLeft, NewLeftType> mapper) {
		Supplier<NewLeftType> leftSupp = () -> {
			if(pairBound) return mapper.apply(boundPair.getLeft());

			NewLeft leftVal = binder.apply(oldSupplier.get()).getLeft();

			return mapper.apply(leftVal);
		};

		Supplier<NewRight> rightSupp = () -> {
			if(pairBound) return boundPair.getRight();

			return binder.apply(oldSupplier.get()).getRight();
		};

		return new LazyPair<>(leftSupp, rightSupp);
	}

	@Override
	public <NewRightType> IPair<NewLeft, NewRightType> mapRight(Function<NewRight, NewRightType> mapper) {
		Supplier<NewLeft> leftSupp = () -> {
			if(pairBound) return boundPair.getLeft();

			return binder.apply(oldSupplier.get()).getLeft();
		};

		Supplier<NewRightType> rightSupp = () -> {
			if(pairBound) return mapper.apply(boundPair.getRight());

			NewRight rightVal = binder.apply(oldSupplier.get()).getRight();

			return mapper.apply(rightVal);
		};

		return new LazyPair<>(leftSupp, rightSupp);
	}

	@Override
	public <MergedType> MergedType merge(BiFunction<NewLeft, NewRight, MergedType> merger) {
		if(!pairBound) {
			boundPair = binder.apply(oldSupplier.get());

			pairBound = true;
		}

		return boundPair.merge(merger);
	}
}