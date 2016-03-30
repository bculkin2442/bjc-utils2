package bjc.utils.data.lazy;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import bjc.utils.data.IHolder;
import bjc.utils.data.IPair;
import bjc.utils.data.Pair;

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
	protected IHolder<IPair<L, R>> del;

	/**
	 * Create a new blank lazy pair
	 */
	public LazyPair() {
		del = new LazyHolder<>(new Pair<>());
	}

	/**
	 * Create a new lazy pair with the specified initial values
	 * 
	 * @param leftVal
	 *            The initial value for the left side of the pair
	 * @param rightVal
	 *            The initial value for the right side of the pair
	 */
	public LazyPair(L leftVal, R rightVal) {
		del = new LazyHolder<>(new Pair<>(leftVal, rightVal));
	}

	/**
	 * Create a new lazy pair with the specified sources for initial values
	 * 
	 * @param leftValSrc
	 *            The function to call for the left initial value
	 * @param rightValSrc
	 *            The function to call for the right initial value
	 */
	public LazyPair(Supplier<L> leftValSrc, Supplier<R> rightValSrc) {
		del = new LazyHolder<>(() -> {
			return new Pair<>(leftValSrc.get(), rightValSrc.get());
		});
	}

	/**
	 * Create a new lazy pair with a specified internal delegate
	 * 
	 * @param deleg
	 *            The internal delegate for the pair
	 */
	private LazyPair(IHolder<IPair<L, R>> deleg) {
		del = deleg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IPair#apply(java.util.function.Function,
	 * java.util.function.Function)
	 */
	@Override
	public <L2, R2> IPair<L2, R2> apply(Function<L, L2> lf,
			Function<R, R2> rf) {
		IHolder<IPair<L2, R2>> newPair =
				del.map(par -> par.apply(lf, rf));

		return new LazyPair<>(newPair);
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
