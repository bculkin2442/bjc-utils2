package bjc.utils.data.internals;

import bjc.utils.data.IHolder;
import bjc.utils.data.ListHolder;
import bjc.utils.funcdata.IList;

import java.util.function.Function;
import java.util.function.UnaryOperator;

/*
 * Holds a list, converted into a holder
 */
public class BoundListHolder<ContainedType> implements IHolder<ContainedType> {
	private IList<IHolder<ContainedType>> heldHolders;

	public BoundListHolder(IList<IHolder<ContainedType>> toHold) {
		heldHolders = toHold;
	}

	@Override
	public <BoundType> IHolder<BoundType> bind(Function<ContainedType, IHolder<BoundType>> binder) {
		if(binder == null) throw new NullPointerException("Binder must not be null");

		IList<IHolder<BoundType>> boundHolders = heldHolders.map((containedHolder) -> {
			return containedHolder.bind(binder);
		});

		return new BoundListHolder<>(boundHolders);
	}

	@Override
	public <NewType> Function<ContainedType, IHolder<NewType>> lift(Function<ContainedType, NewType> func) {
		if(func == null) throw new NullPointerException("Function to lift must not be null");

		return (val) -> {
			return new ListHolder<>(func.apply(val));
		};
	}

	@Override
	public <MappedType> IHolder<MappedType> map(Function<ContainedType, MappedType> mapper) {
		if(mapper == null) throw new NullPointerException("Mapper must not be null");

		IList<IHolder<MappedType>> mappedHolders = heldHolders.map((containedHolder) -> {
			return containedHolder.map(mapper);
		});

		return new BoundListHolder<>(mappedHolders);
	}

	@Override
	public IHolder<ContainedType> transform(UnaryOperator<ContainedType> transformer) {
		if(transformer == null) throw new NullPointerException("Transformer must not be null");

		heldHolders.forEach((containedHolder) -> {
			containedHolder.transform(transformer);
		});

		return this;
	}

	@Override
	public <UnwrappedType> UnwrappedType unwrap(Function<ContainedType, UnwrappedType> unwrapper) {
		if(unwrapper == null) throw new NullPointerException("Unwrapper must not be null");

		return heldHolders.randItem().unwrap(unwrapper);
	}
}