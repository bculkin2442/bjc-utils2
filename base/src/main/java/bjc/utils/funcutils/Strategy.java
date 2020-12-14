package bjc.utils.funcutils;

import java.util.concurrent.*;
import java.util.function.*;

/**
 * Strategy for dealing with parallel execution.
 * 
 * @author Ben Culkin
 * 
 * @param <Output> The type returned by the tasks.
 *
 */
public interface Strategy<Output> extends Function<Callable<Output>, Future<Output>>
{
	/**
	 * Convert a function into one which operates concurrently, using this strategy.
	 * 
	 * @param <Input> The type of the function argument.
	 * 
	 * @param func The type of the function.
	 * 
	 * @return A function which executes concurrently.
	 */
	public default <Input> Function<Input, Future<Output>> using(Function<Input, Output> func)
	{
		return (input) -> this.apply(() -> func.apply(input));
	}
	
	/**
	 * A strategy which will run tasks in serial.
	 * 
	 * @param <Output> The type returned by the task.
	 * 
	 * @return A strategy which executes things serially.
	 */
	public static <Output> Strategy<Output> serial()
	{
		return (call) -> {
			FutureTask<Output> task = new FutureTask<>(call);
			task.run();
			return task;
		};
	}
	/**
	 * A strategy which creates a fresh thread to execute a task on.
	 * 
	 * @param <Output> The type returned by the task.
	 * 
	 * @return A strategy which uses threads to create tasks.
	 */
	public static <Output> Strategy<Output> simpleThread()
	{
		// I leave this as an example as of what is possible with combinators.
		// return (call) -> invoke(introducing(
		// 		() -> new FutureTask<>(call),
		// 		(task, input) -> doWith(
		// 				(FutureTask<Output> tsk) ->
		// 					new Thread(task).start()).apply(task)
		// ));
		return (call) -> {
			FutureTask<Output> task = new FutureTask<>(call);
			new Thread(task).start();
			return task;
		};
	}
	
	/**
	 * A strategy that uses an executor service.
	 * 
	 * @param <Output> The type returned by the task.
	 * 
	 * @param svc The executor service to use.
	 * 
	 * @return A strategy which uses the provided executor.
	 */
	public static <Output> Strategy<Output> executorService(ExecutorService svc)
	{
		return svc::submit;
	}
}
