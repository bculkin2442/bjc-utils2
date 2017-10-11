package bjc.utils.funcdata.bst;

import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A leaf in a tree.
 *
 * @author ben
 *
 * @param <T>
 * 	The data stored in the tree.
 */
public class BinarySearchTreeLeaf<T> implements ITreePart<T> {
	/** The data held in this tree leaf */
	protected T data;

	/** Whether this node is soft-deleted or not */
	protected boolean isDeleted;

	/**
	 * Create a new leaf holding the specified data.
	 *
	 * @param element
	 * 	The data for the leaf to hold.
	 */
	public BinarySearchTreeLeaf(final T element) {
		data = element;
	}

	@Override
	public void add(final T element, final Comparator<T> comparator) {
		throw new IllegalArgumentException("Can't add to a leaf.");
	}

	@Override
	public <E> E collapse(final Function<T, E> leafTransformer, final BiFunction<E, E, E> branchCollapser) {
		if (leafTransformer == null) throw new NullPointerException("Transformer must not be null");

		return leafTransformer.apply(data);
	}

	@Override
	public boolean contains(final T element, final Comparator<T> comparator) {
		return this.data.equals(element);
	}

	@Override
	public T data() {
		return data;
	}

	@Override
	public void delete(final T element, final Comparator<T> comparator) {
		if (data.equals(element)) {
			isDeleted = true;
		}
	}

	@Override
	public boolean directedWalk(final DirectedWalkFunction<T> treeWalker) {
		if (treeWalker == null) throw new NullPointerException("Tree walker must not be null");

		switch (treeWalker.walk(data)) {
		case SUCCESS:
			return true;
		/* We don't have any children to care about. */
		case FAILURE:
		case LEFT:
		case RIGHT:
		default:
			return false;
		}
	}

	@Override
	public boolean forEach(final TreeLinearizationMethod linearizationMethod,
			final Predicate<T> traversalPredicate) {
		if (traversalPredicate == null) throw new NullPointerException("Predicate must not be null");

		return traversalPredicate.test(data);
	}

	@Override
	public String toString() {
		return String.format("BinarySearchTreeLeaf [data='%s', isDeleted=%s]", data, isDeleted);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (data == null ? 0 : data.hashCode());
		result = prime * result + (isDeleted ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof BinarySearchTreeLeaf<?>)) return false;

		final BinarySearchTreeLeaf<?> other = (BinarySearchTreeLeaf<?>) obj;

		if (data == null) {
			if (other.data != null) return false;
		} else if (!data.equals(other.data)) return false;
		if (isDeleted != other.isDeleted) return false;

		return true;
	}
}
