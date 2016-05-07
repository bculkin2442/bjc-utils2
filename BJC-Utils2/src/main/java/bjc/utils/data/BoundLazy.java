package bjc.utils.data;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IFunctionalList;

class BoundLazy<OldType, BoundContainedType>
		implements IHolder<BoundContainedType> {
	private Supplier<IHolder<OldType>>							oldSupplier;

	private Function<OldType, IHolder<BoundContainedType>>		binder;

	private IHolder<BoundContainedType>							boundHolder;

	private boolean												holderBound;

	private IFunctionalList<UnaryOperator<BoundContainedType>>	actions	= new FunctionalList<>();

	public BoundLazy(Supplier<IHolder<OldType>> supp,
			Function<OldType, IHolder<BoundContainedType>> binder) {
		oldSupplier = supp;
		this.binder = binder;
	}

	@Override
	public <BoundType> IHolder<BoundType> bind(
			Function<BoundContainedType, IHolder<BoundType>> bindr) {
		IFunctionalList<UnaryOperator<BoundContainedType>> pendingActions = new FunctionalList<>();

		actions.forEach(pendingActions::add);

		Supplier<IHolder<BoundContainedType>> typeSupplier = () -> {
			IHolder<BoundContainedType> oldHolder = boundHolder;

			if (!holderBound) {
				oldHolder = oldSupplier.get().unwrap(binder);
			}

			return pendingActions.reduceAux(oldHolder, (action, state) -> {
				return state.transform(action);
			}, (value) -> value);
		};

		return new BoundLazy<>(typeSupplier, bindr);
	}

	@Override
	public <MappedType> IHolder<MappedType> map(
			Function<BoundContainedType, MappedType> mapper) {
		IFunctionalList<UnaryOperator<BoundContainedType>> pendingActions = new FunctionalList<>();

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
	public String toString() {
		if (holderBound) {
			return boundHolder.toString();
		}

		return "(unmaterialized)";
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

	@Override
	public <NewType> Function<BoundContainedType, IHolder<NewType>> lift(
			Function<BoundContainedType, NewType> func) {
		return (val) -> {
			return new Lazy<>(func.apply(val));
		};
	}
}