package bjc.utils.data;

import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A holder that may or may not contain a value
 * 
 * @author ben
 *
 * @param <ContainedType>
 *            The type of the value that may or may not be held
 */
public class Option<ContainedType> implements IHolder<ContainedType> {
	private ContainedType held;

	/**
	 * Create a new optional, using the given initial value
	 * 
	 * @param seedValue
	 *            The initial value for the optional
	 */
	public Option(ContainedType seedValue) {
		held = seedValue;
	}

	@Override
	public <BoundType> IHolder<BoundType> bind(
			Function<ContainedType, IHolder<BoundType>> binder) {
		if (held == null) {
			return new Option<>(null);
		}

		return binder.apply(held);
	}

	@Override
	public <NewType> Function<ContainedType, IHolder<NewType>> lift(
			Function<ContainedType, NewType> func) {
		return (val) -> {
			return new Option<>(func.apply(val));
		};
	}

	@Override
	public <MappedType> IHolder<MappedType> map(
			Function<ContainedType, MappedType> mapper) {
		if (held == null) {
			return new Option<>(null);
		}

		return new Option<>(mapper.apply(held));
	}

	@Override
	public IHolder<ContainedType> transform(
			UnaryOperator<ContainedType> transformer) {
		if (held != null) {
			held = transformer.apply(held);
		}

		return this;
	}

	@Override
	public <UnwrappedType> UnwrappedType unwrap(
			Function<ContainedType, UnwrappedType> unwrapper) {
		if (held == null) {
			return null;
		}

		return unwrapper.apply(held);
	}
}
