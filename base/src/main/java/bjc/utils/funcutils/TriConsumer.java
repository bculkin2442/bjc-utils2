package bjc.utils.funcutils;

/**
 * Consumer that takes three arguments.
 *
 * @author EVE
 *
 * @param <A>
 * 	Type of the first argument.
 *
 * @param <B>
 * 	Type of the second argument.
 *
 * @param <C>
 * 	Type of the third argument.
 */
@FunctionalInterface
public interface TriConsumer<A, B, C> {
	/**
	 * Perform the action.
	 *
	 * @param a
	 * 	The first parameter.
	 *
	 * @param b
	 * 	The second parameter.
	 *
	 * @param c
	 * 	The third parameter.
	 */
	public void accept(A a, B b, C c);
}
