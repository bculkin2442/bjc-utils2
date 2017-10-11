package bjc.utils.data.internals;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import bjc.utils.data.IHolder;
import bjc.utils.data.Lazy;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

/**
 * Implements a lazy holder that has been bound.
 *
 * @author Ben Culkin
 */
public class BoundLazy<OldType, BoundContainedType> implements IHolder<BoundContainedType> {
	/* The old value. */
	private final Supplier<IHolder<OldType>> oldSupplier;

	/* The function to use to transform the old value into a new value. */
	private final Function<OldType, IHolder<BoundContainedType>> binder;

	/* The bound value being held. */
	private IHolder<BoundContainedType> boundHolder;

	/* Whether the bound value has been actualized or not. */
	private boolean holderBound;

	/* Transformations currently pending on the bound value. */
	private final IList<UnaryOperator<BoundContainedType>> actions = new FunctionalList<>();

	/**
	 * Create a new bound lazy value. 
	 *
	 * @param supp
	 * 	The supplier of the old value.
	 *
	 * @param binder
	 * 	The function to use to bind the old value to the new one.
	 */
	public BoundLazy(final Supplier<IHolder<OldType>> supp,
			final Function<OldType, IHolder<BoundContainedType>> binder) {
		oldSupplier = supp;
		this.binder = binder;
	}

	@Override
	public <BoundType> IHolder<BoundType> bind(final Function<BoundContainedType, IHolder<BoundType>> bindr) {
		if (bindr == null) throw new NullPointerException("Binder must not be null");

		/* Prepare a list of pending actions. */
		final IList<UnaryOperator<BoundContainedType>> pendingActions = new FunctionalList<>();
		actions.forEach(pendingActions::add);

		/* Create the new supplier of a value. */
		final Supplier<IHolder<BoundContainedType>> typeSupplier = () -> {
			IHolder<BoundContainedType> oldHolder = boundHolder;

			/* Bind the value if it hasn't been bound before. */
			if (!holderBound) {
				oldHolder = oldSupplier.get().unwrap(binder);
			}

			/* Apply all the pending actions. */
			return pendingActions.reduceAux(oldHolder, (action, state) -> {
				return state.transform(action);
			}, (value) -> value);
		};

		return new BoundLazy<>(typeSupplier, bindr);
	}

	@Override
	public <NewType> Function<BoundContainedType, IHolder<NewType>> lift(
			final Function<BoundContainedType, NewType> func) {
		if (func == null) throw new NullPointerException("Function to lift must not be null");

		return (val) -> {
			return new Lazy<>(func.apply(val));
		};
	}

	@Override
	public <MappedType> IHolder<MappedType> map(final Function<BoundContainedType, MappedType> mapper) {
		if (mapper == null) throw new NullPointerException("Mapper must not be null");

		/* Prepare a list of pending actions. */
		final IList<UnaryOperator<BoundContainedType>> pendingActions = new FunctionalList<>();
		actions.forEach(pendingActions::add);

		/* Prepare the new supplier. */
		final Supplier<MappedType> typeSupplier = () -> {
			IHolder<BoundContainedType> oldHolder = boundHolder;

			/* Bound the value if it hasn't been bound. */
			if (!holderBound) {
				oldHolder = oldSupplier.get().unwrap(binder);
			}

			/* Apply pending actions. */
			return pendingActions.reduceAux(oldHolder.getValue(), (action, state) -> {
				return action.apply(state);
			}, (value) -> mapper.apply(value));
		};

		return new Lazy<>(typeSupplier);
	}

	@Override
	public String toString() {
		if (holderBound) return boundHolder.toString();

		return "(unmaterialized)";
	}

	@Override
	public IHolder<BoundContainedType> transform(final UnaryOperator<BoundContainedType> transformer) {
		if (transformer == null) throw new NullPointerException("Transformer must not be null");

		actions.add(transformer);

		return this;
	}

	@Override
	public <UnwrappedType> UnwrappedType unwrap(final Function<BoundContainedType, UnwrappedType> unwrapper) {
		if (unwrapper == null) throw new NullPointerException("Unwrapper must not be null");

		if (!holderBound) {
			boundHolder = oldSupplier.get().unwrap(binder::apply);
		}

		return boundHolder.unwrap(unwrapper);
	}
}
