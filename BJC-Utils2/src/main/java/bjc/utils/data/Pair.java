package bjc.utils.data;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Holds a pair of values of two different types.
 * 
 * Is an eager variant of {@link IPair}
 * 
 * @author ben
 *
 * @param <L>
 *            The type of the thing held on the left (first)
 * @param <R>
 *            The type of the thing held on the right (second)
 */
public class Pair<L, R> implements IPair<L, R> {
	/**
	 * The left value of the pair
	 */
	protected L	leftValue;

	/**
	 * The right value of the pair
	 */
	protected R	rightValue;

	/**
	 * Create a new pair that holds two nulls.
	 */
	public Pair() {

	}

	/**
	 * Create a new pair holding the specified values.
	 * 
	 * @param left
	 *            The value to hold on the left.
	 * @param right
	 *            The value to hold on the right.
	 */
	public Pair(L left, R right) {
		leftValue = left;
		rightValue = right;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.data.IPair#apply(java.util.function.Function,
	 * java.util.function.Function)
	 */
	@Override
	public <L2, R2> IPair<L2, R2> apply(Function<L, L2> leftTransformer,
			Function<R, R2> rightTransformer) {
		if (leftTransformer == null || rightTransformer == null) {
			throw new NullPointerException(
					"Transformers must be non-null");
		}

		return new Pair<>(leftTransformer.apply(leftValue),
				rightTransformer.apply(rightValue));
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

		action.accept(leftValue, rightValue);
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

		return merger.apply(leftValue, rightValue);
	}
}
