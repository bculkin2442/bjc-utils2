package bjc.utils.funcutils;

/**
 * Generic interface for objects that implement the builder pattern.
 *
 * @author ben
 *
 * @param <E>
 * 	The type of object being built.
 */
public interface IBuilder<E> {
	/**
	 * Build the object this builder is building.
	 *
	 * @return
	 * 	The built object.
	 *
	 * @throws IllegalStateException
	 * 	If the data in the builder cannot be built into its
	 * 	corresponding object at this point in time.
	 */
	public E build();

	/**
	 * Reset the state of this builder to its initial state.
	 *
	 * @throws UnsupportedOperationException
	 * 	If the builder doesn't support resetting its state.
	 */
	public default void reset() {
		throw new UnsupportedOperationException("Builder doesn't support state resetting");
	}
}
