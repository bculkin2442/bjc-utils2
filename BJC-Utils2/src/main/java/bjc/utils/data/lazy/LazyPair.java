package bjc.utils.data.lazy;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
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
public class LazyPair<L, R> implements IPair<L, R>, ILazy {
	/**
	 * The backing store for this pair
	 */
	protected IHolder<IPair<L, R>>	delegatePair;

	private boolean					materialized	= false;
	private boolean					pendingActions	= false;

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
		materialized = true;

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
		if (leftValueSource == null || rightValueSource == null) {
			throw new NullPointerException("Sources must be non-null");
		}

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
	private LazyPair(IHolder<IPair<L, R>> delegate, boolean mater,
			boolean pend) {
		materialized = mater;
		pendingActions = pend;

		delegatePair = delegate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IPair#doWith(java.util.function.BiConsumer)
	 */
	@Override
	public void doWith(BiConsumer<L, R> action) {
		if (action == null) {
			throw new NullPointerException("Action must be non-null");
		}

		pendingActions = true;

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
		if (merger == null) {
			throw new NullPointerException("Merger must be non-null");
		}

		materialized = true;
		pendingActions = false;

		return delegatePair
				.unwrap((currentPair) -> currentPair.merge(merger));
	}

	@Override
	public boolean isMaterialized() {
		return materialized;
	}

	@Override
	public boolean hasPendingActions() {
		return pendingActions;
	}

	/*
	 * Note: Materializing will also apply all currently pending actions
	 */
	@Override
	public void materialize() {
		merge((left, right) -> null);

		materialized = true;
		pendingActions = false;
	}

	@Override
	public void applyPendingActions() {
		merge((left, right) -> null);

		materialized = true;
		pendingActions = false;
	}

	@Override
	public <L2, R2> IPair<L2, R2> bind(
			BiFunction<L, R, IPair<L2, R2>> binder) {
		IHolder<IPair<L2, R2>> newDelegate = delegatePair
				.map((pairVal) -> {
					return pairVal.bind(binder);
				});

		return new LazyPair<>(newDelegate, isMaterialized(),
				hasPendingActions());
	}

	@Override
	public String toString() {
		return delegatePair.toString();
	}
}