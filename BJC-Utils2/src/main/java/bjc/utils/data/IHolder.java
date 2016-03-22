package bjc.utils.data;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Generic interface for things that store a single value in a roughly
 * monadic fashion
 * 
 * @author ben
 *
 * @param <T>
 *            The type of data being stored
 */
public interface IHolder<T> {

	/**
	 * Call a provided function with the value being held
	 * 
	 * @param f
	 *            The function to call
	 */
	public void doWith(Consumer<T> f);

	/**
	 * Return the result of applying the given transformation to the held
	 * value. Doesn't change the held value.
	 * 
	 * @param <NewT>
	 *            The new type of the held value
	 * 
	 * @param f
	 *            The transformation to apply
	 * @return A holder with the transformed value
	 */
	public <NewT> IHolder<NewT> map(Function<T, NewT> f);

	/**
	 * Apply the given transformation to the held value. Returns the holder
	 * for allowing chaining of transforms
	 * 
	 * @param f
	 *            The transform to apply to the value
	 * @return The holder
	 */
	public IHolder<T> transform(Function<T, T> f);

	/**
	 * Returns a raw mapped value, not contained in a GenHolder
	 * 
	 * @param <E>
	 *            The type of the value that is the end result
	 * 
	 * @param f
	 *            The function to use for mapping the value
	 * @return The mapped value outside of a GenHolder
	 */
	public <E> E unwrap(Function<T, E> f);
}