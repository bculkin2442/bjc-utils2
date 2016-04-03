package bjc.utils.data;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Holds a single value of a specific type. This is used for indirect
 * references to data, and more specifically for accessing non-final
 * variables from a lambda. AKA the identity monad
 * 
 * This is an eager variant of {@link IHolder}
 * 
 * @author ben
 *
 * @param <T>
 *            The type of the data being held
 */
public class GenHolder<T> implements IHolder<T> {
	/**
	 * The state this holder is responsible for.
	 */
	private T heldValue;

	/**
	 * Creates a new empty holder, with its state set to null
	 */
	public GenHolder() {
		heldValue = null;
	}

	/**
	 * Creates a new holder, with its state initialized to the provided
	 * value
	 * 
	 * @param held
	 *            The state to initialize this holder to.
	 */
	public GenHolder(T held) {
		heldValue = held;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IHolder#doWith(java.util.function.Consumer)
	 */
	@Override
	public void doWith(Consumer<T> action) {
		if (action == null) {
			throw new NullPointerException("Action must be non-null");
		}

		action.accept(heldValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IHolder#map(java.util.function.Function)
	 */
	@Override
	public <NewT> IHolder<NewT> map(Function<T, NewT> transformer) {
		if (transformer == null) {
			throw new NullPointerException("Transformer must be non-null");
		}

		return new GenHolder<>(transformer.apply(heldValue));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IHolder#transform(java.util.function.Function)
	 */
	@Override
	public IHolder<T> transform(Function<T, T> transformer) {
		if (transformer == null) {
			throw new NullPointerException("Transformer must be non-null");
		}

		heldValue = transformer.apply(heldValue);

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IHolder#unwrap(java.util.function.Function)
	 */
	@Override
	public <E> E unwrap(Function<T, E> unwrapper) {
		if (unwrapper == null) {
			throw new NullPointerException("Unwrapper must be null");
		}

		return unwrapper.apply(heldValue);
	}

	@Override
	public String toString() {
		if (heldValue == null) {
			return "(null)";
		}

		return heldValue.toString();
	}
}
