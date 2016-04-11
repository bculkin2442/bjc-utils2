package bjc.utils.data.experimental;

import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author ben
 *
 * @param <ContainedType>
 */
/**
 * Simple implementation of IHolder that has no hidden behavior
 * 
 * @author ben
 *
 * @param <ContainedType>
 *            The type contained in the holder
 */
public class Identity<ContainedType> implements IHolder<ContainedType> {
	private ContainedType heldValue;

	/**
	 * Create a holder holding null
	 */
	public Identity() {
		heldValue = null;
	}

	/**
	 * Create a holder holding the specified value
	 * 
	 * @param value
	 *            The value to hold
	 */
	public Identity(ContainedType value) {
		heldValue = value;
	}

	@Override
	public <BoundType> IHolder<BoundType> bind(
			Function<ContainedType, IHolder<BoundType>> binder) {
		return binder.apply(heldValue);
	}

	@Override
	public <MappedType> IHolder<MappedType> map(
			Function<ContainedType, MappedType> mapper) {
		return new Identity<>(mapper.apply(heldValue));
	}

	@Override
	public IHolder<ContainedType> transform(
			UnaryOperator<ContainedType> transformer) {
		heldValue = transformer.apply(heldValue);

		return this;
	}

	@Override
	public <UnwrappedType> UnwrappedType unwrap(
			Function<ContainedType, UnwrappedType> unwrapper) {
		return unwrapper.apply(heldValue);
	}

	@Override
	public String toString() {
		return "holding[v=" + heldValue + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;

		int result = 1;

		int fieldHash = (heldValue == null) ? 0 : heldValue.hashCode();

		result = prime * result + fieldHash;

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (getClass() != obj.getClass()) {
			return false;
		}

		Identity<?> other = (Identity<?>) obj;

		if (heldValue == null) {
			if (other.heldValue != null) {
				return false;
			}
		} else if (!heldValue.equals(other.heldValue)) {
			return false;
		}

		return true;
	}
}