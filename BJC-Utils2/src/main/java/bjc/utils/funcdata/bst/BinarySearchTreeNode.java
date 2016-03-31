package bjc.utils.funcdata.bst;

import static bjc.utils.funcdata.bst.DirectedWalkFunction.DirectedWalkResult.*;

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
 *            The data type stored in the tree.
 */
public class BinarySearchTreeNode<T> extends BinarySearchTreeLeaf<T> {
	/**
	 * The left child of this node
	 */
	private ITreePart<T>	leftBranch;

	/**
	 * The right child of this node
	 */
	private ITreePart<T>	rightBranch;

	/**
	 * Create a new node with the specified data and children.
	 * 
	 * @param element
	 *            The data to store in this node.
	 * @param left
	 *            The left child of this node.
	 * @param right
	 *            The right child of this node.
	 */
	public BinarySearchTreeNode(T element, ITreePart<T> left,
			ITreePart<T> right) {
		super(element);
		this.leftBranch = left;
		this.rightBranch = right;
	}

	/*
	 * Either adds it to the left/right, or undeletes itself. (non-Javadoc)
	 * 
	 * @see bjc.utils.data.bst.TreeLeaf#add(java.lang.Object,
	 * java.util.Comparator)
	 */
	@Override
	public void add(T element, Comparator<T> comparator) {
		switch (comparator.compare(data, element)) {
			case -1:
				if (leftBranch == null) {
					leftBranch = new BinarySearchTreeNode<>(element, null,
							null);
				} else {
					leftBranch.add(element, comparator);
				}
			case 0:
				if (isDeleted) {
					isDeleted = false;
				} else {
					throw new IllegalArgumentException(
							"Can't add duplicate values");
				}
			case 1:
				if (rightBranch == null) {
					rightBranch = new BinarySearchTreeNode<>(element, null,
							null);
				} else {
					rightBranch.add(element, comparator);
				}
		}
	}

	@Override
	public <E> E collapse(Function<T, E> nodeCollapser,
			BiFunction<E, E, E> branchCollapser) {
		E collapsedNode = nodeCollapser.apply(data);

		if (leftBranch != null) {
			E collapsedLeftBranch =
					leftBranch.collapse(nodeCollapser, branchCollapser);
			if (rightBranch != null) {
				E collapsedRightBranch = rightBranch
						.collapse(nodeCollapser, branchCollapser);

				E collapsedBranches = branchCollapser
						.apply(collapsedLeftBranch, collapsedRightBranch);

				return branchCollapser.apply(collapsedNode,
						collapsedBranches);
			} else {
				return branchCollapser.apply(collapsedNode,
						collapsedLeftBranch);
			}
		} else {
			if (rightBranch != null) {
				E collapsedRightBranch = rightBranch
						.collapse(nodeCollapser, branchCollapser);

				return branchCollapser.apply(collapsedNode,
						collapsedRightBranch);
			} else {
				return collapsedNode;
			}
		}
	}

	@Override
	public boolean contains(T element, Comparator<T> comparator) {
		return directedWalk(currentElement -> {
			switch (comparator.compare(element, currentElement)) {
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
	public void delete(T element, Comparator<T> comparator) {
		directedWalk(currentElement -> {
			switch (comparator.compare(data, element)) {
				case -1:
					return leftBranch == null ? FAILURE : LEFT;
				case 0:
					isDeleted = true;
					return FAILURE;
				case 1:
					return rightBranch == null ? FAILURE : RIGHT;
				default:
					return FAILURE;
			}
		});
	}

	@Override
	public boolean directedWalk(DirectedWalkFunction<T> treeWalker) {
		switch (treeWalker.walk(data)) {
			case SUCCESS:
				return true;
			case LEFT:
				return leftBranch.directedWalk(treeWalker);
			case RIGHT:
				return rightBranch.directedWalk(treeWalker);
			case FAILURE:
				return false;
			default:
				return false;
		}
	}

	@Override
	public boolean forEach(TreeLinearizationMethod linearizationMethod,
			Predicate<T> traversalPredicate) {
		switch (linearizationMethod) {
			case PREORDER:
				return preorderTraverse(linearizationMethod,
						traversalPredicate);
			case INORDER:
				return inorderTraverse(linearizationMethod,
						traversalPredicate);
			case POSTORDER:
				return postorderTraverse(linearizationMethod,
						traversalPredicate);
			default:
				throw new IllegalArgumentException(
						"Passed an incorrect TreeLinearizationMethod "
								+ linearizationMethod + ". WAT");
		}
	}

	private boolean inorderTraverse(
			TreeLinearizationMethod linearizationMethod,
			Predicate<T> traversalPredicate) {

		if (!traverseLeftBranch(linearizationMethod, traversalPredicate)) {
			return false;
		}

		if (!traverseElement(traversalPredicate)) {
			return false;
		}

		if (!traverseRightBranch(linearizationMethod,
				traversalPredicate)) {
			return false;
		}

		return true;
	}

	private boolean postorderTraverse(
			TreeLinearizationMethod linearizationMethod,
			Predicate<T> traversalPredicate) {

		if (!traverseLeftBranch(linearizationMethod, traversalPredicate)) {
			return false;
		}

		if (!traverseRightBranch(linearizationMethod,
				traversalPredicate)) {
			return false;
		}

		if (!traverseElement(traversalPredicate)) {
			return false;
		}

		return true;

	}

	private boolean preorderTraverse(
			TreeLinearizationMethod linearizationMethod,
			Predicate<T> traversalPredicate) {

		if (!traverseElement(traversalPredicate)) {
			return false;
		}

		if (!traverseLeftBranch(linearizationMethod, traversalPredicate)) {
			return false;
		}

		if (!traverseRightBranch(linearizationMethod,
				traversalPredicate)) {
			return false;
		}

		return true;
	}

	private boolean traverseElement(Predicate<T> traversalPredicate) {
		boolean nodeSuccesfullyTraversed;
		if (isDeleted) {
			nodeSuccesfullyTraversed = true;
		} else {
			nodeSuccesfullyTraversed = traversalPredicate.test(data);
		}
		return nodeSuccesfullyTraversed;
	}

	private boolean traverseLeftBranch(
			TreeLinearizationMethod linearizationMethod,
			Predicate<T> traversalPredicate) {
		boolean leftSuccesfullyTraversed;
		if (leftBranch == null) {
			leftSuccesfullyTraversed = true;
		} else {
			leftSuccesfullyTraversed = leftBranch
					.forEach(linearizationMethod, traversalPredicate);
		}
		return leftSuccesfullyTraversed;
	}

	private boolean traverseRightBranch(
			TreeLinearizationMethod linearizationMethod,
			Predicate<T> traversalPredicate) {
		boolean rightSuccesfullyTraversed;
		if (rightBranch == null) {
			rightSuccesfullyTraversed = true;
		} else {
			rightSuccesfullyTraversed = rightBranch
					.forEach(linearizationMethod, traversalPredicate);
		}
		return rightSuccesfullyTraversed;
	}
}