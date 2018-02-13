package bjc.utils.data.internals;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import bjc.utils.data.IHolder;
import bjc.utils.data.Lazy;

/**
 * A wrapped lazy value.
 *
 * @author Ben Culkin
 * @param <ContainedType>
 *        The type of the wrapped value.
 */
public class WrappedLazy<ContainedType> implements IHolder<ContainedType> {
	/* Held value. */
	private final IHolder<IHolder<ContainedType>> held;

	/**
	 * Create a new wrapped lazy value.
	 *
	 * @param wrappedHolder
	 *        The holder to make lazy.
	 */
	public WrappedLazy(final IHolder<ContainedType> wrappedHolder) {
		held = new Lazy<>(wrappedHolder);
	}

	/*
	 * This has an extra parameter, because otherwise it erases to the same
	 * as the public one.
	 *
	 * This is a case where reified generics would be useful, because then
	 * the compiler could know which one we meant without the dummy
	 * parameter.
	 */
	private WrappedLazy(final IHolder<IHolder<ContainedType>> wrappedHolder,
			@SuppressWarnings("unused") final boolean dummy) {
		held = wrappedHolder;
	}

	@Override
	public <BoundType> IHolder<BoundType> bind(final Function<ContainedType, IHolder<BoundType>> binder) {
		final IHolder<IHolder<BoundType>> newHolder = held.map((containedHolder) -> {
			return containedHolder.bind(binder);
		});

		return new WrappedLazy<>(newHolder, false);
	}

	@Override
	public <NewType> Function<ContainedType, IHolder<NewType>> lift(final Function<ContainedType, NewType> func) {
		return (val) -> {
			return new Lazy<>(func.apply(val));
		};
	}

	@Override
	public <MappedType> IHolder<MappedType> map(final Function<ContainedType, MappedType> mapper) {
		final IHolder<IHolder<MappedType>> newHolder = held.map((containedHolder) -> {
			return containedHolder.map(mapper);
		});

		return new WrappedLazy<>(newHolder, false);
	}

	@Override
	public IHolder<ContainedType> transform(final UnaryOperator<ContainedType> transformer) {
		held.transform((containedHolder) -> {
			return containedHolder.transform(transformer);
		});

		return this;
	}

	@Override
	public <UnwrappedType> UnwrappedType unwrap(final Function<ContainedType, UnwrappedType> unwrapper) {
		return held.unwrap((containedHolder) -> {
			return containedHolder.unwrap(unwrapper);
		});
	}
}
