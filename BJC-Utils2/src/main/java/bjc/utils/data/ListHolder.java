package bjc.utils.data;

import bjc.utils.data.internals.BoundListHolder;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A holder that represents a set of non-deterministic computations
 *
 * @author ben
 *
 * @param <ContainedType>
 *                The type of contained value
 */
public class ListHolder<ContainedType> implements IHolder<ContainedType> {
	private IList<ContainedType> heldValues;

	/**
	 * Create a new list holder
	 *
	 * @param values
	 *                The possible values for the computation
	 */
	@SafeVarargs
	public ListHolder(ContainedType... values) {
		heldValues = new FunctionalList<>();

		if(values != null) {
			for(ContainedType containedValue : values) {
				heldValues.add(containedValue);
			}
		}
	}

	private ListHolder(IList<ContainedType> toHold) {
		heldValues = toHold;
	}

	@Override
	public <BoundType> IHolder<BoundType> bind(Function<ContainedType, IHolder<BoundType>> binder) {
		IList<IHolder<BoundType>> boundValues = heldValues.map(binder);

		return new BoundListHolder<>(boundValues);
	}

	@Override
	public <NewType> Function<ContainedType, IHolder<NewType>> lift(Function<ContainedType, NewType> func) {
		return (val) -> {
			return new ListHolder<>(new FunctionalList<>(func.apply(val)));
		};
	}

	@Override
	public <MappedType> IHolder<MappedType> map(Function<ContainedType, MappedType> mapper) {
		IList<MappedType> mappedValues = heldValues.map(mapper);

		return new ListHolder<>(mappedValues);
	}

	@Override
	public IHolder<ContainedType> transform(UnaryOperator<ContainedType> transformer) {
		heldValues = heldValues.map(transformer);

		return this;
	}

	@Override
	public <UnwrappedType> UnwrappedType unwrap(Function<ContainedType, UnwrappedType> unwrapper) {
		return unwrapper.apply(heldValues.randItem());
	}
}
