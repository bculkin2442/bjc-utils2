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
	public <L2, R2> IPair<L2, R2> apply(Function<L, L2> lf,
			Function<R, R2> rf);

	/**
	 * Collapse this pair to a single value. Does not change the internal
	 * contents of this pair.
	 * 
	 * @param bf
	 *            The function to use to collapse the pair.
	 * @return The collapsed value.
	 */
	public <E> E merge(BiFunction<L, R, E> bf);

	/**
	 * Execute an action with the values of this pair. Has no effect on the
	 * internal contents
	 * 
	 * @param bc
	 *            The action to execute on the values
	 */
	public void doWith(BiConsumer<L, R> bc);
}