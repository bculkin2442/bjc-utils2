package bjc.utils.funcutils;

import java.util.concurrent.*;
import java.util.function.*;

/**
 * Utility function for dealing with callables and other things.
 * 
 * @author Ben Culkin
 *
 */
public class Callables
{
	/**
	 * Perform a 'bind' that appends a function to a callable.
	 * 
	 * @param <Input> The type originally returned by the callable.
	 * @param <Output> The type returned by the function.
	 * 
	 * @param call The original callable.
	 * @param func The function to use to transform the result.
	 * 
	 * @return A callable which applies the given function to the result of them.
	 */
	public static <Input, Output> Callable<Output> bind(
			Callable<Input> call, Function<Input, Callable<Output>> func)
	{
		return () -> func.apply(call.call()).call();
	}
	
	/**
	 * Convert a normal function to a function on callables.
	 * 
	 * @param <Input> The input to the function.
	 * @param <Output> The output from the function.
	 * 
	 * @param func The function to convert.
	 * 
	 * @return The function, made to work over callables.
	 */
	public static <Input, Output> Function<Callable<Input>, Callable<Output>>
	fmap(Function<Input, Output> func)
	{
		return (inp) -> () -> func.apply(inp.call());
	}
	
	/**
	 * Convert a future into a callable.
	 * 
	 * @param <Output> The type returned by the future.
	 * 
	 * @param fut The future to convert.
	 * 
	 * @return A future which yields that value.
	 */
	public static <Output> Callable<Output> obtain(Future<Output> fut)
	{
		return () -> fut.get();
	}
}
