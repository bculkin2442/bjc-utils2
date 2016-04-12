package bjc.utils.data.lazy;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import bjc.utils.data.GenHolder;
import bjc.utils.data.IPair;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IFunctionalList;

/**
 * New implementation of lazy pair not delegating to {@link LazyHolder}
 * 
 * @author ben
 * @param <L>
 *            The type on the left side of the pair
 * @param <R>
 *            The type on the right side of the pair
 *
 */
public class NewLazyPair<L, R> implements IPair<L, R>, ILazy {
	private static class BoundLazyPair<OldL, OldR, NewL, NewR>
			implements IPair<NewL, NewR> {
		private Supplier<OldL>								oldLeftSupplier;
		private Supplier<OldR>								oldRightSupplier;

		private BiFunction<OldL, OldR, IPair<NewL, NewR>>	newPairSupplier;

		private IPair<NewL, NewR>							newPair;

		private boolean										newPairMaterialized;

		public BoundLazyPair(Supplier<OldL> leftSup,
				Supplier<OldR> rightSup,
				BiFunction<OldL, OldR, IPair<NewL, NewR>> binder) {
			oldLeftSupplier = leftSup;
			oldRightSupplier = rightSup;

			newPairSupplier = binder;
		}

		@Override
		public <L2, R2> IPair<L2, R2> bind(
				BiFunction<NewL, NewR, IPair<L2, R2>> binder) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void doWith(BiConsumer<NewL, NewR> action) {
			// TODO Auto-generated method stub

		}

		@Override
		public <E> E merge(BiFunction<NewL, NewR, E> merger) {
			if (!newPairMaterialized) {
				newPair = newPairSupplier.apply(oldLeftSupplier.get(),
						oldRightSupplier.get());
			}

			return newPair.merge(merger);
		}
	}

	private L									leftValue;
	private R									rightValue;

	private Supplier<R>							rightValueSupplier;
	private Supplier<L>							leftValueSupplier;

	private boolean								rightMaterialized;
	private boolean								leftMaterialized;

	private IFunctionalList<BiConsumer<L, R>>	actions;

	/**
	 * Create a new lazy pair
	 * 
	 * @param leftVal
	 *            The left value in the pair
	 * @param rightVal
	 *            The right value in the pair
	 */
	public NewLazyPair(L leftVal, R rightVal) {
		leftValueSupplier = () -> leftVal;
		rightValueSupplier = () -> rightVal;
	}

	/**
	 * Create a new lazy pair
	 * 
	 * @param leftSupplier
	 *            The supplier for the left value in the pair
	 * @param rightSupplier
	 *            The supplier for the right value in the pair
	 */
	public NewLazyPair(Supplier<L> leftSupplier,
			Supplier<R> rightSupplier) {
		leftValueSupplier = leftSupplier;
		rightValueSupplier = rightSupplier;
	}

	@Override
	public void applyPendingActions() {
		if (!isMaterialized()) {
			throw new UnsupportedOperationException(
					"Can only apply actions to materialized values");
		}

		actions.forEach((action) -> {
			action.accept(leftValue, rightValue);
		});

		actions = new FunctionalList<>();
	}

	private void applyPossiblyPendingActions(GenHolder<Boolean> hasActions,
			IFunctionalList<BiConsumer<L, R>> pendingActions) {
		if (hasActions.unwrap((val) -> val) == true) {
			pendingActions.forEach((action) -> {
				action.accept(leftValue, rightValue);
			});

			hasActions.transform((val) -> false);
		}
	}

	@Override
	public <L2, R2> IPair<L2, R2> bind(
			BiFunction<L, R, IPair<L2, R2>> binder) {
		GenHolder<Boolean> hasActions = new GenHolder<>(true);
		IFunctionalList<BiConsumer<L, R>> pendingActions = new FunctionalList<>();

		actions.forEach(pendingActions::add);

		return new BoundLazyPair<>(() -> {
			applyPossiblyPendingActions(hasActions, pendingActions);

			return leftValue;
		}, () -> {
			applyPossiblyPendingActions(hasActions, pendingActions);

			return rightValue;
		}, binder);
	}

	@Override
	public void doWith(BiConsumer<L, R> action) {
		actions.add(action);
	}

	@Override
	public boolean hasPendingActions() {
		return !actions.isEmpty();
	}

	@Override
	public boolean isMaterialized() {
		if (leftValueSupplier == null) {
			if (rightValueSupplier == null) {
				return true;
			}

			return rightMaterialized;
		}

		if (rightValueSupplier == null) {
			return leftMaterialized;
		}

		return leftMaterialized && rightMaterialized;
	}

	@Override
	public void materialize() {
		if (isMaterialized()) {
			throw new UnsupportedOperationException(
					"Cannot materialize lazy object twice");
		}

		if (leftValueSupplier != null) {
			leftValue = leftValueSupplier.get();
		}

		if (rightValueSupplier != null) {
			rightValue = rightValueSupplier.get();
		}
	}

	@Override
	public <E> E merge(BiFunction<L, R, E> merger) {
		if (!isMaterialized()) {
			materialize();
		}

		applyPendingActions();

		return merger.apply(leftValue, rightValue);
	}
}