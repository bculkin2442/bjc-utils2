package bjc.utils.data;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IFunctionalList;

/**
 * A holder that represents a set of non-deterministic computations
 * 
 * @author ben
 *
 * @param <ContainedType>
 *            The type of contained value
 */
public class ListHolder<ContainedType> implements IHolder<ContainedType> {
	private IFunctionalList<ContainedType> heldValues;

	private ListHolder(IFunctionalList<ContainedType> toHold) {
		heldValues = toHold;
	}

	/**
	 * Create a new list holder
	 * 
	 * @param values
	 *            The possible values for the computation
	 */
	@SafeVarargs
	public ListHolder(ContainedType... values) {
		heldValues = new FunctionalList<>();

		if (values != null) {
			for (ContainedType containedValue : values) {
				heldValues.add(containedValue);
			}
		}
	}

	@Override
	public <BoundType> IHolder<BoundType> bind(
			Function<ContainedType, IHolder<BoundType>> binder) {
		IFunctionalList<IHolder<BoundType>> boundValues = heldValues
				.map(binder);

		return new BoundListHolder<>(boundValues);
	}

	@Override
	public <MappedType> IHolder<MappedType> map(
			Function<ContainedType, MappedType> mapper) {
		IFunctionalList<MappedType> mappedValues = heldValues.map(mapper);

		return new ListHolder<>(mappedValues);
	}

	@Override
	public IHolder<ContainedType> transform(
			UnaryOperator<ContainedType> transformer) {
		heldValues = heldValues.map(transformer);

		return this;
	}

	@Override
	public <UnwrappedType> UnwrappedType unwrap(
			Function<ContainedType, UnwrappedType> unwrapper) {
		return unwrapper.apply(heldValues.randItem());
	}

	@Override
	public <NewType> Function<ContainedType, IHolder<NewType>> lift(
			Function<ContainedType, NewType> func) {
		return (val) -> {
			return new ListHolder<>(new FunctionalList<>(func.apply(val)));
		};
	}
}
