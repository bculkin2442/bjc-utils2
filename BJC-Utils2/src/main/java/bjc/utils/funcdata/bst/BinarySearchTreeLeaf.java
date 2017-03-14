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
 *                The data stored in the tree.
 */
public class BinarySearchTreeLeaf<T> implements ITreePart<T> {
	/**
	 * The data held in this tree leaf
	 */
	protected T data;

	/**
	 * Whether this node is soft-deleted or not
	 */
	protected boolean isDeleted;

	/**
	 * Create a new leaf holding the specified data.
	 *
	 * @param element
	 *                The data for the leaf to hold.
	 */
	public BinarySearchTreeLeaf(T element) {
		data = element;
	}

	@Override
	public void add(T element, Comparator<T> comparator) {
		throw new IllegalArgumentException("Can't add to a leaf.");
	}

	@Override
	public <E> E collapse(Function<T, E> leafTransformer, BiFunction<E, E, E> branchCollapser) {
		if(leafTransformer == null) throw new NullPointerException("Transformer must not be null");

		return leafTransformer.apply(data);
	}

	@Override
	public boolean contains(T element, Comparator<T> comparator) {
		return this.data.equals(element);
	}

	@Override
	public T data() {
		return data;
	}

	@Override
	public void delete(T element, Comparator<T> comparator) {
		if(data.equals(element)) {
			isDeleted = true;
		}
	}

	@Override
	public boolean directedWalk(DirectedWalkFunction<T> treeWalker) {
		if(treeWalker == null) throw new NullPointerException("Tree walker must not be null");

		switch(treeWalker.walk(data)) {
		case SUCCESS:
			return true;
		// We don't have any children to care about
		case FAILURE:
		case LEFT:
		case RIGHT:
		default:
			return false;
		}
	}

	@Override
	public boolean forEach(TreeLinearizationMethod linearizationMethod, Predicate<T> traversalPredicate) {
		if(traversalPredicate == null) throw new NullPointerException("Predicate must not be null");

		return traversalPredicate.test(data);
	}
}
