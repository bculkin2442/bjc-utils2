package bjc.utils.data.lazy;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import bjc.utils.data.IPair;

/**
 * A lazy holder of two values
 * 
 * Lazy variant of {@link IPair}
 * 
 * @author ben
 *
 * @param <L>
 *            The type of value stored on the left side of the pair
 * @param <R>
 *            The type of value stored on the right side of the pair
 */
public class LazyPair<L, R> implements IPair<L, R> {
	/**
	 * The backing store for this pair
	 */
	protected LazyHolder<IPair<L, R>> del;

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IPair#apply(java.util.function.Function,
	 * java.util.function.Function)
	 */
	@Override
	public <L2, R2> IPair<L2, R2> apply(Function<L, L2> lf,
			Function<R, R2> rf) {
		return del.unwrap((par) -> par.apply(lf, rf));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IPair#doWith(java.util.function.BiConsumer)
	 */
	@Override
	public void doWith(BiConsumer<L, R> bc) {
		del.doWith((par) -> {
			par.doWith(bc);
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IPair#merge(java.util.function.BiFunction)
	 */
	@Override
	public <E> E merge(BiFunction<L, R, E> bf) {
		return del.unwrap((par) -> par.merge(bf));
	}
}
