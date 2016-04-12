package bjc.utils.data.experimental;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A holder of a single value.
 * 
 * @author ben
 *
 * @param <ContainedType>
 *            The type of value held
 */
public interface IHolder<ContainedType> {
	/**
	 * Bind a function across the value in this container
	 * 
	 * @param <BoundType>
	 *            The type of value in this container
	 * @param binder
	 *            The function to bind to the value
	 * @return A holder from binding the value
	 */
	public <BoundType> IHolder<BoundType> bind(
			Function<ContainedType, IHolder<BoundType>> binder);

	/**
	 * Apply an action to the value
	 * 
	 * @param action
	 *            The action to apply to the value
	 */
	public default void doWith(Consumer<ContainedType> action) {
		transform((value) -> {
			action.accept(value);

			return value;
		});
	}

	/**
	 * Get the value contained in this holder without changing it.
	 * 
	 * @return The value held in this holder
	 */
	public default ContainedType getValue() {
		return unwrap((value) -> value);
	}

	/**
	 * Create a new holder with a mapped version of the value in this
	 * holder.
	 * 
	 * Does not change the internal state of this holder
	 * 
	 * @param <MappedType>
	 *            The type of the mapped value
	 * @param mapper
	 *            The function to do mapping with
	 * @return A holder with the mapped value
	 */
	public <MappedType> IHolder<MappedType> map(
			Function<ContainedType, MappedType> mapper);

	/**
	 * Transform the value held in this holder
	 * 
	 * @param transformer
	 *            The function to transform the value with
	 * @return The holder itself, for easy chaining
	 */
	public IHolder<ContainedType> transform(
			UnaryOperator<ContainedType> transformer);

	/**
	 * Unwrap the value contained in this holder so that it is no longer
	 * held
	 * 
	 * @param <UnwrappedType>
	 *            The type of the unwrapped value
	 * @param unwrapper
	 *            The function to use to unwrap the value
	 * @return The unwrapped held value
	 */
	public <UnwrappedType> UnwrappedType unwrap(
			Function<ContainedType, UnwrappedType> unwrapper);

	/**
	 * Replace the held value with a new one
	 * 
	 * @param newValue
	 *            The value to hold instead
	 * @return The holder itself
	 */
	public default IHolder<ContainedType> replace(ContainedType newValue) {
		return transform((oldValue) -> newValue);
	}
}
