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
	protected IHolder<IPair<L, R>> delegatePair;

	/**
	 * Create a new blank lazy pair
	 */
	public LazyPair() {
		delegatePair = new LazyHolder<>(new Pair<>());
	}

	/**
	 * Create a new lazy pair with the specified initial values
	 * 
	 * @param leftValue
	 *            The initial value for the left side of the pair
	 * @param rightValue
	 *            The initial value for the right side of the pair
	 */
	public LazyPair(L leftValue, R rightValue) {
		delegatePair = new LazyHolder<>(new Pair<>(leftValue, rightValue));
	}

	/**
	 * Create a new lazy pair with the specified sources for initial values
	 * 
	 * @param leftValueSource
	 *            The function to call for the left initial value
	 * @param rightValueSource
	 *            The function to call for the right initial value
	 */
	public LazyPair(Supplier<L> leftValueSource,
			Supplier<R> rightValueSource) {
		delegatePair = new LazyHolder<>(() -> {
			return new Pair<>(leftValueSource.get(),
					rightValueSource.get());
		});
	}

	/**
	 * Create a new lazy pair with a specified internal delegate
	 * 
	 * @param delegate
	 *            The internal delegate for the pair
	 */
	private LazyPair(IHolder<IPair<L, R>> delegate) {
		delegatePair = delegate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IPair#apply(java.util.function.Function,
	 * java.util.function.Function)
	 */
	@Override
	public <L2, R2> IPair<L2, R2> apply(Function<L, L2> leftTransform,
			Function<R, R2> rightTransform) {
		IHolder<IPair<L2, R2>> newPair = delegatePair
				.map((currentPair) -> currentPair.apply(leftTransform, rightTransform));

		return new LazyPair<>(newPair);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IPair#doWith(java.util.function.BiConsumer)
	 */
	@Override
	public void doWith(BiConsumer<L, R> action) {
		delegatePair.doWith((currentPair) -> {
			currentPair.doWith(action);
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IPair#merge(java.util.function.BiFunction)
	 */
	@Override
	public <E> E merge(BiFunction<L, R, E> merger) {
		return delegatePair.unwrap((currentPair) -> currentPair.merge(merger));
	}
}
