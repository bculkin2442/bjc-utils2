package bjc.utils.data;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import bjc.utils.data.internals.BoundLazy;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

/**
 * A holder that holds a means to create a value, but doesn't actually compute
 * the value until it's needed
 *
 * @author ben
 *
 * @param <ContainedType>
 */
public class Lazy<ContainedType> implements IHolder<ContainedType> {
	private Supplier<ContainedType> valueSupplier;

	private IList<UnaryOperator<ContainedType>> actions = new FunctionalList<>();

	private boolean valueMaterialized;

	private ContainedType heldValue;

	/**
	 * Create a new lazy value from the specified seed value
	 *
	 * @param value
	 *                The seed value to use
	 */
	public Lazy(final ContainedType value) {
		heldValue = value;

		valueMaterialized = true;
	}

	/**
	 * Create a new lazy value from the specified value source
	 *
	 * @param supp
	 *                The source of a value to use
	 */
	public Lazy(final Supplier<ContainedType> supp) {
		valueSupplier = new SingleSupplier<>(supp);

		valueMaterialized = false;
	}

	private Lazy(final Supplier<ContainedType> supp, final IList<UnaryOperator<ContainedType>> pendingActions) {
		valueSupplier = supp;

		actions = pendingActions;
	}

	@Override
	public <BoundType> IHolder<BoundType> bind(final Function<ContainedType, IHolder<BoundType>> binder) {
		final IList<UnaryOperator<ContainedType>> pendingActions = new FunctionalList<>();

		actions.forEach(pendingActions::add);

		final Supplier<ContainedType> supplier = () -> {
			if (valueMaterialized) return heldValue;

			return valueSupplier.get();
		};

		return new BoundLazy<>(() -> {
			return new Lazy<>(supplier, pendingActions);
		}, binder);
	}

	@Override
	public <NewType> Function<ContainedType, IHolder<NewType>> lift(final Function<ContainedType, NewType> func) {
		return val -> {
			return new Lazy<>(func.apply(val));
		};
	}

	@Override
	public <MappedType> IHolder<MappedType> map(final Function<ContainedType, MappedType> mapper) {
		final IList<UnaryOperator<ContainedType>> pendingActions = new FunctionalList<>();

		actions.forEach(pendingActions::add);

		return new Lazy<>(() -> {
			ContainedType currVal = heldValue;

			if (!valueMaterialized) {
				currVal = valueSupplier.get();
			}

			return pendingActions.reduceAux(currVal, UnaryOperator<ContainedType>::apply,
					value -> mapper.apply(value));
		});
	}

	@Override
	public String toString() {
		if (valueMaterialized) {
			if (actions.isEmpty())
				return String.format("value[v='%s']", heldValue);
			else return String.format("value[v='%s'] (has pending transforms)", heldValue);
		}

		return "(unmaterialized)";
	}

	@Override
	public IHolder<ContainedType> transform(final UnaryOperator<ContainedType> transformer) {
		actions.add(transformer);

		return this;
	}

	@Override
	public <UnwrappedType> UnwrappedType unwrap(final Function<ContainedType, UnwrappedType> unwrapper) {
		if (!valueMaterialized) {
			heldValue = valueSupplier.get();

			valueMaterialized = true;
		}

		actions.forEach(action -> {
			heldValue = action.apply(heldValue);
		});

		actions = new FunctionalList<>();

		return unwrapper.apply(heldValue);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + (actions == null ? 0 : actions.hashCode());
		result = prime * result + (heldValue == null ? 0 : heldValue.hashCode());
		result = prime * result + (valueMaterialized ? 1231 : 1237);

		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Lazy<?>)) return false;

		final Lazy<?> other = (Lazy<?>) obj;

		if (valueMaterialized != other.valueMaterialized) return false;

		if (valueMaterialized) {
			if (heldValue == null) {
				if (other.heldValue != null) return false;
			} else if (!heldValue.equals(other.heldValue)) return false;
		} else return false;

		if (actions == null) {
			if (other.actions != null) return false;
		} else if (actions.getSize() > 0 || other.actions.getSize() > 0) return false;

		return true;
	}

	/**
	 * Create a new lazy container with an already present value.
	 *
	 * @param val
	 *                The value for the lazy container.
	 *
	 * @return A new lazy container holding that value.
	 */
	public static <ContainedType> Lazy<ContainedType> lazy(final ContainedType val) {
		return new Lazy<>(val);
	}

	/**
	 * Create a new lazy container with a suspended value.
	 *
	 * @param supp
	 *                The suspended value for the lazy container.
	 *
	 * @return A new lazy container that will un-suspend the value when
	 *         necessary.
	 */
	public static <ContainedType> Lazy<ContainedType> lazy(final Supplier<ContainedType> supp) {
		return new Lazy<>(supp);
	}
}
