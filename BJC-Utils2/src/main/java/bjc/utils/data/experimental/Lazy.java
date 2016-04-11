package bjc.utils.data.experimental;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IFunctionalList;

/**
 * A holder that holds a means to create a value, but doesn't actually
 * compute the value until it's needed
 * 
 * @author ben
 *
 * @param <ContainedType>
 */
public class Lazy<ContainedType> implements IHolder<ContainedType> {
	private static class BoundLazy<OldType, BoundContainedType>
			implements IHolder<BoundContainedType> {
		private Supplier<IHolder<OldType>>							oldSupplier;

		private Function<OldType, IHolder<BoundContainedType>>		binder;

		private IHolder<BoundContainedType>							boundHolder;

		private boolean												holderBound;

		private IFunctionalList<UnaryOperator<BoundContainedType>>	actions	=
				new FunctionalList<>();

		public BoundLazy(Supplier<IHolder<OldType>> supp,
				Function<OldType, IHolder<BoundContainedType>> binder) {
			oldSupplier = supp;
			this.binder = binder;
		}

		@Override
		public <BoundType> IHolder<BoundType> bind(
				Function<BoundContainedType, IHolder<BoundType>> bindr) {
			IFunctionalList<UnaryOperator<BoundContainedType>> pendingActions =
					new FunctionalList<>();

			actions.forEach(pendingActions::add);

			Supplier<IHolder<BoundContainedType>> typeSupplier = () -> {
				IHolder<BoundContainedType> oldHolder = boundHolder;

				if (!holderBound) {
					oldHolder = oldSupplier.get().unwrap(binder);
				}

				return pendingActions.reduceAux(oldHolder,
						(action, state) -> {
							return state.transform(action);
						}, (value) -> value);
			};

			return new BoundLazy<>(typeSupplier, bindr);
		}

		@Override
		public <MappedType> IHolder<MappedType> map(
				Function<BoundContainedType, MappedType> mapper) {
			IFunctionalList<UnaryOperator<BoundContainedType>> pendingActions =
					new FunctionalList<>();

			actions.forEach(pendingActions::add);

			Supplier<MappedType> typeSupplier = () -> {
				IHolder<BoundContainedType> oldHolder = boundHolder;

				if (!holderBound) {
					oldHolder = oldSupplier.get().unwrap(binder);
				}

				return pendingActions.reduceAux(oldHolder.getValue(),
						(action, state) -> {
							return action.apply(state);
						}, (value) -> mapper.apply(value));
			};

			return new Lazy<>(typeSupplier);
		}

		@Override
		public IHolder<BoundContainedType> transform(
				UnaryOperator<BoundContainedType> transformer) {
			actions.add(transformer);

			return this;
		}

		@Override
		public <UnwrappedType> UnwrappedType unwrap(
				Function<BoundContainedType, UnwrappedType> unwrapper) {
			if (!holderBound) {
				boundHolder = oldSupplier.get().unwrap(binder::apply);
			}

			return boundHolder.unwrap(unwrapper);
		}

	}

	private Supplier<ContainedType>							valueSupplier;

	private IFunctionalList<UnaryOperator<ContainedType>>	actions	=
			new FunctionalList<>();

	private boolean											valueMaterialized;

	private ContainedType									heldValue;

	/**
	 * Create a new lazy value from the specified seed value
	 * 
	 * @param value
	 *            The seed value to use
	 */
	public Lazy(ContainedType value) {
		heldValue = value;

		valueMaterialized = true;
	}

	/**
	 * Create a new lazy value from the specified value source
	 * 
	 * @param supp
	 *            The source of a value to use
	 */
	public Lazy(Supplier<ContainedType> supp) {
		valueSupplier = supp;

		valueMaterialized = false;
	}

	private Lazy(Supplier<ContainedType> supp,
			IFunctionalList<UnaryOperator<ContainedType>> pendingActions) {
		valueSupplier = supp;

		actions = pendingActions;
	}

	@Override
	public <BoundType> IHolder<BoundType> bind(
			Function<ContainedType, IHolder<BoundType>> binder) {
		IFunctionalList<UnaryOperator<ContainedType>> pendingActions =
				new FunctionalList<>();

		actions.forEach(pendingActions::add);

		Supplier<ContainedType> supplier = () -> {
			if (valueMaterialized) {
				return heldValue;
			}

			return valueSupplier.get();
		};

		return new BoundLazy<>(() -> {
			return new Lazy<>(supplier, pendingActions);
		}, binder);
	}

	@Override
	public <MappedType> IHolder<MappedType> map(
			Function<ContainedType, MappedType> mapper) {
		IFunctionalList<UnaryOperator<ContainedType>> pendingActions =
				new FunctionalList<>();

		actions.forEach(pendingActions::add);

		return new Lazy<>(() -> {
			ContainedType currVal = heldValue;

			if (!valueMaterialized) {
				currVal = valueSupplier.get();
			}

			return pendingActions.reduceAux(currVal,
					UnaryOperator<ContainedType>::apply,
					(value) -> mapper.apply(value));
		});
	}

	@Override
	public IHolder<ContainedType> transform(
			UnaryOperator<ContainedType> transformer) {
		actions.add(transformer);

		return this;
	}

	@Override
	public <UnwrappedType> UnwrappedType unwrap(
			Function<ContainedType, UnwrappedType> unwrapper) {
		if (!valueMaterialized) {
			heldValue = valueSupplier.get();

			valueMaterialized = true;
		}

		actions.forEach((action) -> {
			heldValue = action.apply(heldValue);
		});

		actions = new FunctionalList<>();

		return unwrapper.apply(heldValue);
	}
}
