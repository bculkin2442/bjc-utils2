package bjc.utils.data;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.theory.Functor;

/**
 * A holder of a single value.
 * 
 * @author ben
 *
 * @param <ContainedType>
 *            The type of value held
 */
public interface IHolder<ContainedType> extends Functor<ContainedType> {
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
	public default void doWith(Consumer<? super ContainedType> action) {
		transform((value) -> {
			action.accept(value);

			return value;
		});
	}

	@Override
	default <ArgType, ReturnType> Function<Functor<ArgType>, Functor<ReturnType>> fmap(
			Function<ArgType, ReturnType> func) {
		return (argumentFunctor) -> {
			if (!(argumentFunctor instanceof IHolder<?>)) {
				throw new IllegalArgumentException(
						"This functor only supports mapping over instances of IHolder");
			}

			IHolder<ArgType> holder = (IHolder<ArgType>) argumentFunctor;

			return holder.map(func);
		};
	}

	@Override
	public default ContainedType getValue() {
		return unwrap((value) -> value);
	}

	/**
	 * Lifts a function to bind over this holder
	 * 
	 * @param <NewType>
	 *            The type of the functions return
	 * @param func
	 *            The function to lift over the holder
	 * @return The function lifted over the holder
	 */
	public <NewType> Function<ContainedType, IHolder<NewType>> lift(
			Function<ContainedType, NewType> func);

	/**
	 * Make this holder lazy
	 * 
	 * @return A lazy version of this holder
	 */
	public default IHolder<ContainedType> makeLazy() {
		return new WrappedLazy<>(this);
	}

	/**
	 * Make this holder a list
	 * 
	 * @return A list version of this holder
	 */
	public default IHolder<ContainedType> makeList() {
		return new BoundListHolder<>(new FunctionalList<>(this));
	}

	/**
	 * Make this holder optional
	 * 
	 * @return An optional version of this holder
	 */
	public default IHolder<ContainedType> makeOptional() {
		return new WrappedOption<>(this);
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
	 * Replace the held value with a new one
	 * 
	 * @param newValue
	 *            The value to hold instead
	 * @return The holder itself
	 */
	public default IHolder<ContainedType> replace(ContainedType newValue) {
		return transform((oldValue) -> {
			return newValue;
		});
	}

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
}
