package bjc.utils.data;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

/*
 * Implements a lazy holder that has been bound
 */
class BoundLazy<OldType, BoundContainedType>
		implements IHolder<BoundContainedType> {
	/*
	 * The old value
	 */
	private Supplier<IHolder<OldType>>						oldSupplier;

	/*
	 * The function to use to transform the old value into a new value
	 */
	private Function<OldType, IHolder<BoundContainedType>>	binder;

	/*
	 * The bound value being held
	 */
	private IHolder<BoundContainedType>						boundHolder;

	/*
	 * Whether the bound value has been actualized or not
	 */
	private boolean											holderBound;

	/*
	 * Transformations currently pending on the bound value
	 */
	private IList<UnaryOperator<BoundContainedType>>		actions	= new FunctionalList<>();

	/*
	 * Create a new bound lazy value
	 */
	public BoundLazy(Supplier<IHolder<OldType>> supp,
			Function<OldType, IHolder<BoundContainedType>> binder) {
		oldSupplier = supp;
		this.binder = binder;
	}

	@Override
	public <BoundType> IHolder<BoundType> bind(
			Function<BoundContainedType, IHolder<BoundType>> bindr) {
		/*
		 * Prepare a list of pending actions
		 */
		IList<UnaryOperator<BoundContainedType>> pendingActions = new FunctionalList<>();
		actions.forEach(pendingActions::add);

		/*
		 * Create the new supplier of a value
		 */
		Supplier<IHolder<BoundContainedType>> typeSupplier = () -> {
			IHolder<BoundContainedType> oldHolder = boundHolder;

			/*
			 * Bind the value if it hasn't been bound before
			 */
			if (!holderBound) {
				oldHolder = oldSupplier.get().unwrap(binder);
			}

			/*
			 * Apply all the pending actions
			 */
			return pendingActions.reduceAux(oldHolder, (action, state) -> {
				return state.transform(action);
			}, (value) -> value);
		};

		return new BoundLazy<>(typeSupplier, bindr);
	}

	@Override
	public <NewType> Function<BoundContainedType, IHolder<NewType>> lift(
			Function<BoundContainedType, NewType> func) {
		return (val) -> {
			return new Lazy<>(func.apply(val));
		};
	}

	@Override
	public <MappedType> IHolder<MappedType> map(
			Function<BoundContainedType, MappedType> mapper) {
		// Prepare a list of pending actions
		IList<UnaryOperator<BoundContainedType>> pendingActions = new FunctionalList<>();
		actions.forEach(pendingActions::add);

		// Prepare the new supplier
		Supplier<MappedType> typeSupplier = () -> {
			IHolder<BoundContainedType> oldHolder = boundHolder;

			// Bound the value if it hasn't been bound
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
}