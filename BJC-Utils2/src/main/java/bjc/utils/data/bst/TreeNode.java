package bjc.utils.data.bst;

import static bjc.utils.data.bst.DirectedWalkFunction.DirectedWalkResult.*;

import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A binary node in a tree.
 * @author ben
 *
 * @param <T> The data type stored in the tree.
 */
public class TreeNode<T> extends TreeLeaf<T> {
	private ITreePart<T>	left;
	private ITreePart<T>	right;

	/**
	 * Create a new node with the specified data and children.
	 * @param data The data to store in this node.
	 * @param left The left child of this node.
	 * @param right The right child of this node.
	 */
	public TreeNode(T data, ITreePart<T> left, ITreePart<T> right) {
		super(data);
		this.left = left;
		this.right = right;
	}

	/*
	 * Either adds it to the left/right, or undeletes itself.
	 * (non-Javadoc)
	 * @see bjc.utils.data.bst.TreeLeaf#add(java.lang.Object, java.util.Comparator)
	 */
	@Override
	public void add(T dat, Comparator<T> comp) {
		switch (comp.compare(data, dat)) {
			case -1:
				if (left == null) {
					left = new TreeNode<T>(dat, null, null);
				} else {
					left.add(dat, comp);
				}
			case 0:
				if (deleted) {
					deleted = false;
				} else {
					throw new IllegalArgumentException(
							"Can't add duplicate values");
				}
			case 1:
				if (right == null) {
					right = new TreeNode<T>(dat, null, null);
				} else {
					right.add(dat, comp);
				}
		}
	}

	@Override
	public <E> E collapse(Function<T, E> f, BiFunction<E, E, E> bf) {
		E tm = f.apply(data);

		if (left != null) {
			if (right != null) {
				return bf.apply(tm, bf.apply(left.collapse(f, bf),
						right.collapse(f, bf)));
			} else {
				return bf.apply(tm, left.collapse(f, bf));
			}
		} else {
			if(right != null) {
				return bf.apply(tm, right.collapse(f, bf));
			} else {
				return tm;
			}
		}
	}

	@Override
	public boolean contains(T data, Comparator<T> cmp) {
		return directedWalk(ds -> {
			switch (cmp.compare(data, ds)) {
				case -1:
					return LEFT;
				case 0:
					return deleted ? FAILURE : SUCCESS;
				case 1:
					return RIGHT;
				default:
					return FAILURE;
			}
		});
	}

	@Override
	public void delete(T dat, Comparator<T> cmp) {
		directedWalk(new DirectedWalkFunction<T>() {
			@Override
			public DirectedWalkResult walk(T ds) {
				switch (cmp.compare(data, dat)) {
					case -1:
						return left == null ? FAILURE : LEFT;
					case 0:
						deleted = true;
						return FAILURE;
					case 1:
						return right == null ? FAILURE : RIGHT;
					default:
						return DirectedWalkResult.FAILURE;
				}
			}
		});
	}

	@Override
	public boolean directedWalk(DirectedWalkFunction<T> ds) {
		switch (ds.walk(data)) {
			case SUCCESS:
				return true;
			case LEFT:
				return left.directedWalk(ds);
			case RIGHT:
				return right.directedWalk(ds);
			case FAILURE:
				return false;
			default:
				return false;
		}
	}

	@Override
	public boolean forEach(TreeLinearizationMethod tlm, Predicate<T> c) {
		switch (tlm) {
			case PREORDER:
				return preorderTraverse(tlm, c);
			case INORDER:
				return inorderTraverse(tlm, c);
			case POSTORDER:
				return postorderTraverse(tlm, c);
			default:
				throw new IllegalArgumentException(
						"Passed an incorrect TreeLinearizationMethod.");
		}
	}

	private boolean inorderTraverse(TreeLinearizationMethod tlm,
			Predicate<T> c) {

		return ((left == null ? true : left.forEach(tlm, c))
				&& (deleted ? true : c.test(data))
				&& (right == null ? true : right.forEach(tlm, c)));
	}

	private boolean postorderTraverse(TreeLinearizationMethod tlm,
			Predicate<T> c) {

		return ((left == null ? true : left.forEach(tlm, c))
				&& (right == null ? true : right.forEach(tlm, c))
				&& (deleted ? true : c.test(data)));

	}

	private boolean preorderTraverse(TreeLinearizationMethod tlm,
			Predicate<T> c) {

		return ((deleted ? true : c.test(data))
				&& (left == null ? true : left.forEach(tlm, c))
				&& (right == null ? true : right.forEach(tlm, c)));
	}
}
