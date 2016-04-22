package bjc.utils.data;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

class HalfBoundLazyPair<OldType, NewLeft, NewRight>
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
	public <MergedType> MergedType merge(
			BiFunction<NewLeft, NewRight, MergedType> merger) {
		if (!pairBound) {
			boundPair = binder.apply(oldSupplier.get());

			pairBound = true;
		}

		return boundPair.merge(merger);
	}
}