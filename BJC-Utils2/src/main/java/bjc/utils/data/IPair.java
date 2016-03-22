package bjc.utils.data;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * An interface representing a pair of values
 * 
 * @author ben
 *
 * @param <L>
 *            The type stored in the left side of the pair
 * @param <R>
 *            The type stored in the right side of the pair
 */
public interface IPair<L, R> {

	/**
	 * Create a new pair by applying the given functions to the left/right.
	 * Does not change the internal contents of this pair.
	 * 
	 * @param <L2>
	 *            The new left type of the pair
	 * @param <R2>
	 *            The new right type of the pair
	 * 
	 * @param lf
	 *            The function to apply to the left value.
	 * @param rf
	 *            The function to apply to the right value.
	 * @return A new pair containing the two modified values.
	 */
	public <L2, R2> IPair<L2, R2> apply(Function<L, L2> lf,
			Function<R, R2> rf);

	/**
	 * Execute an action with the values of this pair. Has no effect on the
	 * internal contents
	 * 
	 * @param bc
	 *            The action to execute on the values
	 */
	public void doWith(BiConsumer<L, R> bc);

	/**
	 * Collapse this pair to a single value. Does not change the internal
	 * contents of this pair.
	 * @param <E> The resulting type after merging
	 * 
	 * @param bf
	 *            The function to use to collapse the pair.
	 * @return The collapsed value.
	 */
	public <E> E merge(BiFunction<L, R, E> bf);
}