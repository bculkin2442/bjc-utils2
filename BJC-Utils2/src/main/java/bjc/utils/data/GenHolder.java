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
	private T held;

	/**
	 * Creates a new empty holder, with its state set to null
	 */
	public GenHolder() {
		held = null;
	}

	/**
	 * Creates a new holder, with its state initialized to the provided
	 * value
	 * 
	 * @param hld
	 *            The state to initialize this holder to.
	 */
	public GenHolder(T hld) {
		held = hld;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IHolder#doWith(java.util.function.Consumer)
	 */
	@Override
	public void doWith(Consumer<T> f) {
		f.accept(held);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IHolder#map(java.util.function.Function)
	 */
	@Override
	public <NewT> IHolder<NewT> map(Function<T, NewT> f) {
		return new GenHolder<>(f.apply(held));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IHolder#transform(java.util.function.Function)
	 */
	@Override
	public IHolder<T> transform(Function<T, T> f) {
		held = f.apply(held);

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IHolder#unwrap(java.util.function.Function)
	 */
	@Override
	public <E> E unwrap(Function<T, E> f) {
		return f.apply(held);
	}

	@Override
	public String toString() {
		return held.toString();
	}
}
