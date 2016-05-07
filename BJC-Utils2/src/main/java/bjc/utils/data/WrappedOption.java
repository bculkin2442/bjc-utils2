package bjc.utils.data;

import java.util.function.Function;
import java.util.function.UnaryOperator;

class WrappedOption<ContainedType> implements IHolder<ContainedType> {
	private IHolder<IHolder<ContainedType>> held;

	private WrappedOption(IHolder<IHolder<ContainedType>> toHold,
			@SuppressWarnings("unused") boolean dummy) {
		held = toHold;
	}

	public WrappedOption(IHolder<ContainedType> seedValue) {
		held = new Option<>(seedValue);
	}

	@Override
	public <BoundType> IHolder<BoundType> bind(
			Function<ContainedType, IHolder<BoundType>> binder) {
		IHolder<IHolder<BoundType>> newHolder = held
				.map((containedHolder) -> {
					return containedHolder.bind((containedValue) -> {
						if (containedValue == null) {
							return new Option<>(null);
						}

						return binder.apply(containedValue);
					});
				});

		return new WrappedOption<>(newHolder, false);
	}

	@Override
	public <MappedType> IHolder<MappedType> map(
			Function<ContainedType, MappedType> mapper) {
		IHolder<IHolder<MappedType>> newHolder = held
				.map((containedHolder) -> {
					return containedHolder.map((containedValue) -> {
						if (containedValue == null) {
							return null;
						}

						return mapper.apply(containedValue);
					});
				});

		return new WrappedOption<>(newHolder, false);
	}

	@Override
	public IHolder<ContainedType> transform(
			UnaryOperator<ContainedType> transformer) {
		held.transform((containedHolder) -> {
			return containedHolder.transform((containedValue) -> {
				if (containedValue == null) {
					return null;
				}

				return transformer.apply(containedValue);
			});
		});

		return this;
	}

	@Override
	public <UnwrappedType> UnwrappedType unwrap(
			Function<ContainedType, UnwrappedType> unwrapper) {
		return held.unwrap((containedHolder) -> {
			return containedHolder.unwrap((containedValue) -> {
				if (containedValue == null) {
					return null;
				}

				return unwrapper.apply(containedValue);
			});
		});
	}

	@Override
	public <NewType> Function<ContainedType, IHolder<NewType>> lift(
			Function<ContainedType, NewType> func) {
		return (val) -> {
			return new Option<>(func.apply(val));
		};
	}
}
