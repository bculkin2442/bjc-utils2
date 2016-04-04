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
 *            The data stored in the tree.
 */
public class BinarySearchTreeLeaf<T> implements ITreePart<T> {
	/**
	 * The data held in this tree leaf
	 */
	protected T			data;

	/**
	 * Whether this node is soft-deleted or not
	 */
	protected boolean	isDeleted;

	/**
	 * Create a new leaf holding the specified data.
	 * 
	 * @param element
	 *            The data for the leaf to hold.
	 */
	public BinarySearchTreeLeaf(T element) {
		data = element;
	}

	/*
	 * Can't add things to a leaf. (non-Javadoc)
	 * 
	 * @see bjc.utils.data.bst.ITreePart#add(java.lang.Object,
	 * java.util.Comparator)
	 */
	@Override
	public void add(T element, Comparator<T> comparator) {
		throw new IllegalArgumentException("Can't add to a leaf.");
	}

	/*
	 * Just transform our data. (non-Javadoc)
	 * 
	 * @see
	 * bjc.utils.data.bst.ITreePart#collapse(java.util.function.Function,
	 * java.util.function.BiFunction)
	 */
	@Override
	public <E> E collapse(Function<T, E> leafTransformer,
			BiFunction<E, E, E> branchCollapser) {
		if (leafTransformer == null) {
			throw new NullPointerException("Transformer must not be null");
		}

		return leafTransformer.apply(data);
	}

	/*
	 * Only check our data. (non-Javadoc)
	 * 
	 * @see bjc.utils.data.bst.ITreePart#contains(java.lang.Object,
	 * java.util.Comparator)
	 */
	@Override
	public boolean contains(T element, Comparator<T> comparator) {
		return this.data.equals(element);
	}

	/*
	 * Just get the data (non-Javadoc)
	 * 
	 * @see bjc.utils.data.bst.ITreePart#data()
	 */
	@Override
	public T data() {
		return data;
	}

	/*
	 * Just mark ourselves as "not here" (non-Javadoc)
	 * 
	 * @see bjc.utils.data.bst.ITreePart#delete(java.lang.Object,
	 * java.util.Comparator)
	 */
	@Override
	public void delete(T element, Comparator<T> comparator) {
		if (data.equals(element)) {
			isDeleted = true;
		}
	}

	/*
	 * Just walk our data and only succede if the walk does, because
	 * there's nowhere left to go. (non-Javadoc)
	 * 
	 * @see bjc.utils.data.bst.ITreePart#directedWalk(bjc.utils.data.bst.
	 * DirectedWalkFunction)
	 */
	@Override
	public boolean directedWalk(DirectedWalkFunction<T> treeWalker) {
		if (treeWalker == null) {
			throw new NullPointerException("Tree walker must not be null");
		}

		switch (treeWalker.walk(data)) {
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

	/*
	 * Just check our data. (non-Javadoc)
	 * 
	 * @see
	 * bjc.utils.data.bst.ITreePart#forEach(bjc.utils.data.bst.ITreePart.
	 * TreeLinearizationMethod, java.util.function.Predicate)
	 */
	@Override
	public boolean forEach(TreeLinearizationMethod linearizationMethod,
			Predicate<T> traversalPredicate) {
		if (traversalPredicate == null) {
			throw new NullPointerException("Predicate must not be null");
		}

		return traversalPredicate.test(data);
	}
}
