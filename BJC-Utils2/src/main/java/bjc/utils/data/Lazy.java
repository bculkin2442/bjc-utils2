package bjc.utils.data;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

/**
 * A holder that holds a means to create a value, but doesn't actually
 * compute the value until it's needed
 * 
 * @author ben
 *
 * @param <ContainedType>
 */
public class Lazy<ContainedType> implements IHolder<ContainedType> {
	private Supplier<ContainedType>				valueSupplier;

	private IList<UnaryOperator<ContainedType>>	actions	= new FunctionalList<>();

	private boolean								valueMaterialized;

	private ContainedType						heldValue;

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
		valueSupplier = new SingleSupplier<>(supp);

		valueMaterialized = false;
	}

	private Lazy(Supplier<ContainedType> supp,
			IList<UnaryOperator<ContainedType>> pendingActions) {
		valueSupplier = supp;

		actions = pendingActions;
	}

	@Override
	public <BoundType> IHolder<BoundType> bind(
			Function<ContainedType, IHolder<BoundType>> binder) {
		IList<UnaryOperator<ContainedType>> pendingActions = new FunctionalList<>();

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
		IList<UnaryOperator<ContainedType>> pendingActions = new FunctionalList<>();

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
	public String toString() {
		if (valueMaterialized) {
			if (actions.isEmpty()) {
				return "value[v='" + heldValue + "']";
			}

			return "value[v='" + heldValue + "'] (has pending transforms)";
		}

		return "(unmaterialized)";
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

	@Override
	public <NewType> Function<ContainedType, IHolder<NewType>> lift(
			Function<ContainedType, NewType> func) {
		return (val) -> {
			return new Lazy<>(func.apply(val));
		};
	}
}
