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
public class TreeLeaf<T> implements ITreePart<T> {
	/**
	 * The data held in this tree leaf
	 */
	protected T			data;

	/**
	 * Whether this node is soft-deleted or not
	 */
	protected boolean	deleted;

	/**
	 * Create a new leaf holding the specified data.
	 * 
	 * @param dat
	 *            The data for the leaf to hold.
	 */
	public TreeLeaf(T dat) {
		data = dat;
	}

	/*
	 * Can't add things to a leaf. (non-Javadoc)
	 * 
	 * @see bjc.utils.data.bst.ITreePart#add(java.lang.Object,
	 * java.util.Comparator)
	 */
	@Override
	public void add(T dat, Comparator<T> comp) {
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
	public <E> E collapse(Function<T, E> f, BiFunction<E, E, E> bf) {
		return f.apply(data);
	}

	/*
	 * Only check our data. (non-Javadoc)
	 * 
	 * @see bjc.utils.data.bst.ITreePart#contains(java.lang.Object,
	 * java.util.Comparator)
	 */
	@Override
	public boolean contains(T data, Comparator<T> cmp) {
		return this.data.equals(data);
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
	public void delete(T dat, Comparator<T> cmp) {
		if (data.equals(dat)) {
			deleted = true;
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
	public boolean directedWalk(DirectedWalkFunction<T> ds) {
		switch (ds.walk(data)) {
			case SUCCESS:
				return true;
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
	public boolean forEach(TreeLinearizationMethod tlm, Predicate<T> c) {
		return c.test(data);
	}
}
