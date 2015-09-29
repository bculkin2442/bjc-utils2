package bjc.utils.data;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Holds a pair of values of two different types.
 * @author ben
 *
 * @param <L> The type of the thing held on the left (first)
 * @param <R> The type of the thing held on the right (second)
 */
public class Pair<L, R> {
	public L	l;
	public R	r;

	/**
	 * Create a new pair that holds two nulls.
	 */
	public Pair() {
		
	}
	
	/**
	 * Create a new pair holding the specified values.
	 * @param left The value to hold on the left.
	 * @param right The value to hold on the right.
	 */
	public Pair(L left, R right) {
		l = left;
		r = right;
	}
	
	/**
	 * Create a new pair by applying the given functions to the left/right.
	 * 		Does not change the internal contents of this pair.
	 * @param lf The function to apply to the left value.
	 * @param rf The function to apply to the right value.
	 * @return A new pair containing the two modified values.
	 */
	public <L2, R2> Pair<L2, R2> apply(Function<L, L2> lf, Function<R, R2> rf) {
		return new Pair<L2, R2>(lf.apply(l), rf.apply(r));
	}
	
	/**
	 * Collapse this pair to a single value.
	 * 		Does not change the internal contents of this pair.
	 * @param bf The function to use to collapse the pair.
	 * @return The collapsed value.
	 */
	public <E> E merge(BiFunction<L, R, E> bf) {
		return bf.apply(l, r);
	}
}
