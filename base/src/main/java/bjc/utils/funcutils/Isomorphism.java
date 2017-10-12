package bjc.utils.funcutils;

import java.util.function.Function;

/**
 * A pair of functions to transform between a pair of types.
 * 
 * @author bjculkin
 * 
 * @param <S>
 * 	The source type of the isomorphism.
 * 
 * @param <D>
 * 	The destination type of isomorphism.
 */
public class Isomorphism<S, D> {
	/* The function to the destination type. */
	private Function<S, D>	toFunc;
	/* The function to the source type. */
	private Function<D, S>	fromFunc;

	/**
	 * Create a new isomorphism.
	 * 
	 * @param to
	 * 	The 'forward' function, from the source to the definition.
	 * 
	 * @param from
	 * 	The 'backward' function, from the definition to the source.
	 */
	public Isomorphism(Function<S, D> to, Function<D, S> from) {
		toFunc = to;
		fromFunc = from;
	}

	/**
	 * Apply the isomorphism forward.
	 * 
	 * @param val
	 * 	The source value.
	 * 
	 * @return
	 * 	The destination value.
	 */
	public D to(S val) {
		return toFunc.apply(val);
	}

	/**
	 * Apply the isomorphism backward.
	 * 
	 * @param val
	 * 	The destination value.
	 * 
	 * @return
	 * 	The source value.
	 */
	public S from(D val) {
		return fromFunc.apply(val);
	}
}
