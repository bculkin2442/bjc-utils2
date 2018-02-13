package bjc.utils.funcdata.bst;

import static bjc.utils.funcdata.bst.DirectedWalkFunction.DirectedWalkResult.FAILURE;
import static bjc.utils.funcdata.bst.DirectedWalkFunction.DirectedWalkResult.LEFT;
import static bjc.utils.funcdata.bst.DirectedWalkFunction.DirectedWalkResult.RIGHT;
import static bjc.utils.funcdata.bst.DirectedWalkFunction.DirectedWalkResult.SUCCESS;

import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A binary node in a tree.
 *
 * @author ben
 *
 * @param <T>
 *        The data type stored in the tree.
 */
public class BinarySearchTreeNode<T> extends BinarySearchTreeLeaf<T> {
	/* The left child of this node */
	private ITreePart<T> left;

	/* The right child of this node */
	private ITreePart<T> right;

	/**
	 * Create a new node with the specified data and children.
	 *
	 * @param element
	 *        The data to store in this node.
	 *
	 * @param lft
	 *        The left child of this node.
	 *
	 * @param rght
	 *        The right child of this node.
	 */
	public BinarySearchTreeNode(final T element, final ITreePart<T> lft, final ITreePart<T> rght) {
		super(element);
		this.left = lft;
		this.right = rght;
	}

	@Override
	public void add(final T element, final Comparator<T> comparator) {
		if(comparator == null) throw new NullPointerException("Comparator must not be null");

		switch(comparator.compare(data, element)) {
		case -1:
			if(left == null) {
				left = new BinarySearchTreeNode<>(element, null, null);
			} else {
				left.add(element, comparator);
			}
			break;
		case 0:
			if(isDeleted) {
				isDeleted = false;
			} else
				throw new IllegalArgumentException("Can't add duplicate values");
			break;
		case 1:
			if(right == null) {
				right = new BinarySearchTreeNode<>(element, null, null);
			} else {
				right.add(element, comparator);
			}
			break;
		default:
			throw new IllegalStateException("Error: Comparator yielded invalid value");
		}
	}

	@Override
	public <E> E collapse(final Function<T, E> nodeCollapser, final BiFunction<E, E, E> branchCollapser) {
		if(nodeCollapser == null || branchCollapser == null)
			throw new NullPointerException("Collapser must not be null");

		final E collapsedNode = nodeCollapser.apply(data);

		if(left != null) {
			final E collapsedLeftBranch = left.collapse(nodeCollapser, branchCollapser);

			if(right != null) {
				final E collapsedRightBranch = right.collapse(nodeCollapser, branchCollapser);

				final E collapsedBranches = branchCollapser.apply(collapsedLeftBranch,
						collapsedRightBranch);

				return branchCollapser.apply(collapsedNode, collapsedBranches);
			}

			return branchCollapser.apply(collapsedNode, collapsedLeftBranch);
		}

		if(right != null) {
			final E collapsedRightBranch = right.collapse(nodeCollapser, branchCollapser);

			return branchCollapser.apply(collapsedNode, collapsedRightBranch);
		}

		return collapsedNode;
	}

	@Override
	public boolean contains(final T element, final Comparator<T> comparator) {
		if(comparator == null) throw new NullPointerException("Comparator must not be null");

		return directedWalk(currentElement -> {
			switch(comparator.compare(element, currentElement)) {
			case -1:
				return LEFT;
			case 0:
				return isDeleted ? FAILURE : SUCCESS;
			case 1:
				return RIGHT;
			default:
				return FAILURE;
			}
		});
	}

	@Override
	public void delete(final T element, final Comparator<T> comparator) {
		if(comparator == null) throw new NullPointerException("Comparator must not be null");

		directedWalk(currentElement -> {
			switch(comparator.compare(data, element)) {
			case -1:
				return left == null ? FAILURE : LEFT;
			case 0:
				isDeleted = true;
				return FAILURE;
			case 1:
				return right == null ? FAILURE : RIGHT;
			default:
				return FAILURE;
			}
		});
	}

	@Override
	public boolean directedWalk(final DirectedWalkFunction<T> treeWalker) {
		if(treeWalker == null) throw new NullPointerException("Walker must not be null");

		switch(treeWalker.walk(data)) {
		case SUCCESS:
			return true;
		case LEFT:
			return left.directedWalk(treeWalker);
		case RIGHT:
			return right.directedWalk(treeWalker);
		case FAILURE:
		default:
			return false;
		}
	}

	@Override
	public boolean forEach(final TreeLinearizationMethod linearizationMethod,
			final Predicate<T> traversalPredicate) {
		if(linearizationMethod == null) {
			throw new NullPointerException("Linearization method must not be null");
		} else if(traversalPredicate == null) {
			throw new NullPointerException("Predicate must not be null");
		}

		switch(linearizationMethod) {
		case PREORDER:
			return preorderTraverse(linearizationMethod, traversalPredicate);
		case INORDER:
			return inorderTraverse(linearizationMethod, traversalPredicate);
		case POSTORDER:
			return postorderTraverse(linearizationMethod, traversalPredicate);
		default:
			String msg = String.format("Passed an incorrect TreeLinearizationMethod %s. WAT",
					linearizationMethod);

			throw new IllegalArgumentException(msg);
		}
	}

	/* Do an in-order traversal. */
	private boolean inorderTraverse(final TreeLinearizationMethod linearizationMethod,
			final Predicate<T> traversalPredicate) {
		if(!traverseLeftBranch(linearizationMethod, traversalPredicate)) return false;

		if(!traverseElement(traversalPredicate)) return false;

		if(!traverseRightBranch(linearizationMethod, traversalPredicate)) return false;

		return true;
	}

	/* Do a post-order traversal. */
	private boolean postorderTraverse(final TreeLinearizationMethod linearizationMethod,
			final Predicate<T> traversalPredicate) {
		if(!traverseLeftBranch(linearizationMethod, traversalPredicate)) return false;

		if(!traverseRightBranch(linearizationMethod, traversalPredicate)) return false;

		if(!traverseElement(traversalPredicate)) return false;

		return true;

	}

	/* Do a pre-order traversal. */
	private boolean preorderTraverse(final TreeLinearizationMethod linearizationMethod,
			final Predicate<T> traversalPredicate) {
		if(!traverseElement(traversalPredicate)) return false;

		if(!traverseLeftBranch(linearizationMethod, traversalPredicate)) return false;

		if(!traverseRightBranch(linearizationMethod, traversalPredicate)) return false;

		return true;
	}

	/* Traverse an element. */
	private boolean traverseElement(final Predicate<T> traversalPredicate) {
		boolean nodeSuccesfullyTraversed;

		if(isDeleted) {
			nodeSuccesfullyTraversed = true;
		} else {
			nodeSuccesfullyTraversed = traversalPredicate.test(data);
		}

		return nodeSuccesfullyTraversed;
	}

	/* Traverse the left branch of a tree. */
	private boolean traverseLeftBranch(final TreeLinearizationMethod linearizationMethod,
			final Predicate<T> traversalPredicate) {
		boolean leftSuccesfullyTraversed;

		if(left == null) {
			leftSuccesfullyTraversed = true;
		} else {
			leftSuccesfullyTraversed = left.forEach(linearizationMethod, traversalPredicate);
		}

		return leftSuccesfullyTraversed;
	}

	/* Traverse the right branch of a tree. */
	private boolean traverseRightBranch(final TreeLinearizationMethod linearizationMethod,
			final Predicate<T> traversalPredicate) {
		boolean rightSuccesfullyTraversed;

		if(right == null) {
			rightSuccesfullyTraversed = true;
		} else {
			rightSuccesfullyTraversed = right.forEach(linearizationMethod, traversalPredicate);
		}

		return rightSuccesfullyTraversed;
	}

	@Override
	public String toString() {
		return String.format("BinarySearchTreeNode [left='%s', right='%s']", left, right);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (left == null ? 0 : left.hashCode());
		result = prime * result + (right == null ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if(this == obj) return true;
		if(!super.equals(obj)) return false;
		if(!(obj instanceof BinarySearchTreeNode<?>)) return false;

		final BinarySearchTreeNode<?> other = (BinarySearchTreeNode<?>) obj;

		if(left == null) {
			if(other.left != null) return false;
		} else if(!left.equals(other.left)) return false;

		if(right == null) {
			if(other.right != null) return false;
		} else if(!right.equals(other.right)) return false;

		return true;
	}
}
