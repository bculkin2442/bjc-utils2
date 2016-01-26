package bjc.utils.graph;

import java.util.function.Function;

/**
 * Holds a single value of a specific type. This is used for indirect
 * references to data, and more specifically for accessing non-final
 * variables from a lambda. AKA the identity monad
 * 
 * @author ben
 *
 * @param <T>
 *            The type of the data being held
 */
public class GenHolder<T> {
	/**
	 * The state this holder is responsible for.
	 */
	public T held;

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
	 * @param held
	 *            The state to initialize this holder to.
	 */
	public GenHolder(T hld) {
		held = hld;
	}

	/**
	 * Apply the given transformation to the held value. Returns the holder
	 * for allowing chaining of transforms
	 * 
	 * @param f
	 *            The transform to apply to the value
	 * @return The holder
	 */
	public GenHolder<T> transform(Function<T, T> f) {
		held = f.apply(held);

		return this;
	}

	/**
	 * Return the result of applying the given transformation to the held
	 * value Doesn't change the held value
	 * 
	 * @param f
	 *            The transformation to apply
	 * @return A holder with the transformed value
	 */
	public <NewT> GenHolder<NewT> map(Function<T, NewT> f) {
		return new GenHolder<NewT>(f.apply(held));
	}

	/**
	 * Returns a raw mapped value, not contained in a GenHolder
	 * 
	 * @param f
	 *            The function to use for mapping the value
	 * @return The mapped value outside of a GenHolder
	 */
	public <E> E unwrap(Function<T, E> f) {
		return f.apply(held);
	}
}
