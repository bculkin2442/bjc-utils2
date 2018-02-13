package bjc.utils.data;

import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Simple implementation of IHolder that has no hidden behavior.
 *
 * @author ben
 *
 * @param <ContainedType>
 *        The type contained in the holder.
 */
public class Identity<ContainedType> implements IHolder<ContainedType> {
	/* The held value. */
	private ContainedType heldValue;

	/** Create a holder holding null */
	public Identity() {
		heldValue = null;
	}

	/**
	 * Create a holder holding the specified value.
	 *
	 * @param value
	 *        The value to hold.
	 */
	public Identity(final ContainedType value) {
		heldValue = value;
	}

	@Override
	public <BoundType> IHolder<BoundType> bind(final Function<ContainedType, IHolder<BoundType>> binder) {
		return binder.apply(heldValue);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + (heldValue == null ? 0 : heldValue.hashCode());

		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof Identity)) return false;

		final Identity<?> other = (Identity<?>) obj;

		if(heldValue == null) {
			if(other.heldValue != null) return false;
		} else if(!heldValue.equals(other.heldValue)) return false;

		return true;
	}

	@Override
	public <NewType> Function<ContainedType, IHolder<NewType>> lift(final Function<ContainedType, NewType> func) {
		return (val) -> {
			return new Identity<>(func.apply(val));
		};
	}

	@Override
	public <MappedType> IHolder<MappedType> map(final Function<ContainedType, MappedType> mapper) {
		return new Identity<>(mapper.apply(heldValue));
	}

	@Override
	public String toString() {
		return String.format("Identity [heldValue=%s]", heldValue);
	}

	@Override
	public IHolder<ContainedType> transform(final UnaryOperator<ContainedType> transformer) {
		heldValue = transformer.apply(heldValue);

		return this;
	}

	@Override
	public <UnwrappedType> UnwrappedType unwrap(final Function<ContainedType, UnwrappedType> unwrapper) {
		return unwrapper.apply(heldValue);
	}

	/**
	 * Create a new identity container.
	 *
	 * @param val
	 *        The contained value.
	 *
	 * @return A new identity container.
	 */
	public static <ContainedType> Identity<ContainedType> id(final ContainedType val) {
		return new Identity<>(val);
	}

	/**
	 * Create a new empty identity container.
	 *
	 * @return A new empty identity container.
	 */
	public static <ContainedType> Identity<ContainedType> id() {
		return new Identity<>();
	}
}
