package bjc.utils.data;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

class BoundLazyPair<OldLeft, OldRight, NewLeft, NewRight>
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
		IHolder<IPair<NewLeft, NewRight>> newPair =
				new Identity<>(boundPair);
		IHolder<Boolean> newPairMade = new Identity<>(pairBound);

		Supplier<NewLeft> leftSupp = () -> {
			if (!newPairMade.getValue()) {
				newPair.replace(binder.apply(leftSupplier.get(),
						rightSupplier.get()));

				newPairMade.replace(false);
			}

			return newPair.unwrap((pair) -> pair.getLeft());
		};

		Supplier<NewRight> rightSupp = () -> {
			if (!newPairMade.getValue()) {
				newPair.replace(binder.apply(leftSupplier.get(),
						rightSupplier.get()));

				newPairMade.replace(false);
			}

			return newPair.unwrap((pair) -> pair.getRight());
		};

		return new BoundLazyPair<>(leftSupp, rightSupp, bindr);
	}

	@Override
	public <MergedType> MergedType
			merge(BiFunction<NewLeft, NewRight, MergedType> merger) {
		if (!pairBound) {
			boundPair =
					binder.apply(leftSupplier.get(), rightSupplier.get());

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