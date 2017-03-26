package bjc.utils.data.internals;

import bjc.utils.data.IHolder;
import bjc.utils.data.Lazy;

import java.util.function.Function;
import java.util.function.UnaryOperator;

@SuppressWarnings("javadoc")
public class WrappedLazy<ContainedType> implements IHolder<ContainedType> {
	private IHolder<IHolder<ContainedType>> held;

	public WrappedLazy(IHolder<ContainedType> wrappedHolder) {
		held = new Lazy<>(wrappedHolder);
	}

	// This has an extra parameter, because otherwise it erases to the same
	// as the public one
	private WrappedLazy(IHolder<IHolder<ContainedType>> wrappedHolder, boolean dummy) {
		held = wrappedHolder;
	}

	@Override
	public <BoundType> IHolder<BoundType> bind(Function<ContainedType, IHolder<BoundType>> binder) {
		IHolder<IHolder<BoundType>> newHolder = held.map((containedHolder) -> {
			return containedHolder.bind(binder);
		});

		return new WrappedLazy<>(newHolder, false);
	}

	@Override
	public <NewType> Function<ContainedType, IHolder<NewType>> lift(Function<ContainedType, NewType> func) {
		return (val) -> {
			return new Lazy<>(func.apply(val));
		};
	}

	@Override
	public <MappedType> IHolder<MappedType> map(Function<ContainedType, MappedType> mapper) {
		IHolder<IHolder<MappedType>> newHolder = held.map((containedHolder) -> {
			return containedHolder.map(mapper);
		});

		return new WrappedLazy<>(newHolder, false);
	}

	@Override
	public IHolder<ContainedType> transform(UnaryOperator<ContainedType> transformer) {
		held.transform((containedHolder) -> {
			return containedHolder.transform(transformer);
		});

		return this;
	}

	@Override
	public <UnwrappedType> UnwrappedType unwrap(Function<ContainedType, UnwrappedType> unwrapper) {
		return held.unwrap((containedHolder) -> {
			return containedHolder.unwrap(unwrapper);
		});
	}
}
