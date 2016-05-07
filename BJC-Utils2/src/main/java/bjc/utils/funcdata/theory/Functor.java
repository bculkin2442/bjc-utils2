package bjc.utils.funcdata.theory;

import java.util.function.Function;

/**
 * Represents a container or context some sort usually, but the precise
 * definition is that it represents exactly what it is defined as
 * 
 * @author ben
 * @param <ContainedType>
 *            The value inside the functor
 */
public interface Functor<ContainedType> {
	/**
	 * Converts a normal function to operate over values in a functor.
	 * 
	 * N.B: Even though the type signature implies that you can apply the
	 * resulting function to any type of functor, it is only safe to call
	 * it on instances of the type of functor you called fmap on.
	 * 
	 * @param <ArgType>
	 *            The argument of the function
	 * @param <ReturnType>
	 *            The return type of the function
	 * @param func
	 *            The function to convert
	 * @return The passed in function converted to work over a particular
	 *         type of functors
	 */
	public <ArgType, ReturnType> Function<Functor<ArgType>, Functor<ReturnType>> fmap(
			Function<ArgType, ReturnType> func);

	/**
	 * Retrieve the thing inside this functor
	 * 
	 * @return The thing inside this functor
	 */
	public ContainedType getValue();
}
