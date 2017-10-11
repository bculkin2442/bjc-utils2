package bjc.utils.data.internals;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import bjc.utils.data.IHolder;
import bjc.utils.data.ListHolder;
import bjc.utils.funcdata.IList;

/**
 * Holds a list, converted into a holder.
 *
 * @author Ben Culkin
 */
public class BoundListHolder<ContainedType> implements IHolder<ContainedType> {
	/* The list of contained holders. */
	private final IList<IHolder<ContainedType>> heldHolders;

	/**
	 * Create a new list of holders.
	 *
	 * @param toHold
	 * 	The list of holders to, well, hold.
	 */
	public BoundListHolder(final IList<IHolder<ContainedType>> toHold) {
		heldHolders = toHold;
	}

	@Override
	public <BoundType> IHolder<BoundType> bind(final Function<ContainedType, IHolder<BoundType>> binder) {
		if (binder == null) throw new NullPointerException("Binder must not be null");

		final IList<IHolder<BoundType>> boundHolders = heldHolders.map((containedHolder) -> {
			return containedHolder.bind(binder);
		});

		return new BoundListHolder<>(boundHolders);
	}

	@Override
	public <NewType> Function<ContainedType, IHolder<NewType>> lift(final Function<ContainedType, NewType> func) {
		if (func == null) throw new NullPointerException("Function to lift must not be null");

		return (val) -> {
			return new ListHolder<>(func.apply(val));
		};
	}

	@Override
	public <MappedType> IHolder<MappedType> map(final Function<ContainedType, MappedType> mapper) {
		if (mapper == null) throw new NullPointerException("Mapper must not be null");

		final IList<IHolder<MappedType>> mappedHolders = heldHolders.map((containedHolder) -> {
			return containedHolder.map(mapper);
		});

		return new BoundListHolder<>(mappedHolders);
	}

	@Override
	public IHolder<ContainedType> transform(final UnaryOperator<ContainedType> transformer) {
		if (transformer == null) throw new NullPointerException("Transformer must not be null");

		heldHolders.forEach((containedHolder) -> {
			containedHolder.transform(transformer);
		});

		return this;
	}

	@Override
	public <UnwrappedType> UnwrappedType unwrap(final Function<ContainedType, UnwrappedType> unwrapper) {
		if (unwrapper == null) throw new NullPointerException("Unwrapper must not be null");

		/*
		 * @NOTE
		 * 	Is there another way we could want to do this?
		 */
		return heldHolders.randItem().unwrap(unwrapper);
	}
}
