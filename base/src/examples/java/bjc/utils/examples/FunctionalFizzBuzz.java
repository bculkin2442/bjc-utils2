package bjc.utils.examples;

import java.util.function.*;
import bjc.data.*;

import static bjc.functypes.Combinators.*;

/**
 * @author Ben Culkin
 *
 */
public class FunctionalFizzBuzz {	
	interface FizzBuzzFunc 
		extends Function<UnaryOperator<String>, UnaryOperator<String>> {
		// Alias type
	}
	
	/**
	 * Calculate fizz-buzz in an un-functional functional way.
	 * 
	 * @param args Ignored CLI args
	 */
	public static void main(String[] args) {
		// Do something, at some point
		times(
				100,
				andThen(
  				invoke(
      			introducing(
      					() -> new IntHolder(),
      					(holder, arg) -> {			
      							Consumer<Integer>   numSetter2 = (num) -> holder.set(num);        		
      							
      							return (num) -> beforeThis(
      									numSetter2,
      									compose(
      											input  -> Integer.toString(input),
      											strang -> 
      													fbMaker(3, "Fizz", "", holder)
      														.apply(
      															fbMaker(5, "Buzz", "", holder)
      																.apply(ignore -> ignore)
      														)
      														.apply(strang)
      									)
      							).apply(num);
      					}
      			)
      		),
  				System.out::println
  			)
		);
	}
	
	private static FizzBuzzFunc fbMaker(
			int cond, String initial, String interleave, IntHolder var) {
		return func -> invoke(
				iftt(
						ignored1 -> var.get() % cond == 0,
						arg      -> initial + func.apply(interleave),
						func
				)
		);
	}
	
	private static <Input, Output> Function<Input, Output> iftt(Predicate<Input> in,
			Output ifTrue,
			Output ifFalse) {
		return (arg) -> in.test(arg) ? ifTrue : ifFalse;
	}
}