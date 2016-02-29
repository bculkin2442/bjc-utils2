package bjc.utils.data;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface IPair<L, R> {

	/**
	 * Create a new pair by applying the given functions to the left/right.
	 * Does not change the internal contents of this pair.
	 * 
	 * @param lf
	 *            The function to apply to the left value.
	 * @param rf
	 *            The function to apply to the right value.
	 * @return A new pair containing the two modified values.
	 */
	<L2, R2> IPair<L2, R2> apply(Function<L, L2> lf, Function<R, R2> rf);

	/**
	 * Collapse this pair to a single value. Does not change the internal
	 * contents of this pair.
	 * 
	 * @param bf
	 *            The function to use to collapse the pair.
	 * @return The collapsed value.
	 */
	<E> E merge(BiFunction<L, R, E> bf);

	void doWith(BiConsumer<L, R> bc);

}