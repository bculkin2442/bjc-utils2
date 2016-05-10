package bjc.utils.funcdata.bst;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

/**
 * A binary search tree, with some mild support for functional traversal.
 * 
 * @author ben
 *
 * @param <T>
 *            The data type stored in the node.
 */
public class BinarySearchTree<T> {
	/**
	 * The comparator for use in ordering items
	 */
	private Comparator<T>	comparator;

	/**
	 * The current count of elements in the tree
	 */
	private int				elementCount;

	/**
	 * The root element of the tree
	 */
	private ITreePart<T>	rootElement;

	/**
	 * Create a new tree using the specified way to compare elements.
	 * 
	 * @param cmp
	 *            The thing to use for comparing elements
	 */
	public BinarySearchTree(Comparator<T> cmp) {
		if (cmp == null) {
			throw new NullPointerException("Comparator must not be null");
		}

		elementCount = 0;
		comparator = cmp;
	}

	/**
	 * Add a node to the binary search tree.
	 * 
	 * @param element
	 *            The data to add to the binary search tree.
	 */
	public void addNode(T element) {
		elementCount++;

		if (rootElement == null) {
			rootElement = new BinarySearchTreeNode<>(element, null, null);
		} else {
			rootElement.add(element, comparator);
		}
	}

	/**
	 * Check if an adjusted pivot falls with the bounds of a list
	 * 
	 * @param elements
	 *            The list to get bounds from
	 * @param pivot
	 *            The pivot
	 * @param pivotAdjustment
	 *            The distance from the pivot
	 * @return Whether the adjusted pivot is with the list
	 */
	private boolean adjustedPivotInBounds(IList<T> elements,
			int pivot, int pivotAdjustment) {
		return (pivot - pivotAdjustment) >= 0
				&& (pivot + pivotAdjustment) < elements.getSize();
	}

	/**
	 * Balance the tree, and remove soft-deleted nodes for free. Takes O(N)
	 * time, but also O(N) space.
	 */
	public void balance() {
		IList<T> elements = new FunctionalList<>();

		// Add each element to the list in sorted order
		rootElement.forEach(TreeLinearizationMethod.INORDER,
				element -> elements.add(element));

		// Clear the tree
		rootElement = null;

		// Set up the pivot and adjustment for readding elements
		int pivot = elements.getSize() / 2;
		int pivotAdjustment = 0;

		// Add elements until there aren't any left
		while (adjustedPivotInBounds(elements, pivot, pivotAdjustment)) {
			if (rootElement == null) {
				// Create a new root element
				rootElement = new BinarySearchTreeNode<>(
						elements.getByIndex(pivot), null, null);
			} else {
				// Add the left and right elements in a balanced manner
				rootElement.add(
						elements.getByIndex(pivot + pivotAdjustment),
						comparator);

				rootElement.add(
						elements.getByIndex(pivot - pivotAdjustment),
						comparator);
			}

			// Increase the distance from the pivot
			pivotAdjustment++;
		}

		// Add any trailing unbalanced elements
		if ((pivot - pivotAdjustment) >= 0) {
			rootElement.add(elements.getByIndex(pivot - pivotAdjustment),
					comparator);
		} else if ((pivot + pivotAdjustment) < elements.getSize()) {
			rootElement.add(elements.getByIndex(pivot + pivotAdjustment),
					comparator);
		}
	}

	/**
	 * Soft-delete a node from the tree. Soft-deleted nodes stay in the
	 * tree until trim()/balance() is invoked, and are not included in
	 * traversals/finds.
	 * 
	 * @param element
	 *            The node to delete
	 */
	public void deleteNode(T element) {
		elementCount--;

		rootElement.delete(element, comparator);
	}

	/**
	 * Get the root of the tree.
	 * 
	 * @return The root of the tree.
	 */
	public ITreePart<T> getRoot() {
		return rootElement;
	}

	/**
	 * Check if a node is in the tree
	 * 
	 * @param element
	 *            The node to check the presence of for the tree.
	 * @return Whether or not the node is in the tree.
	 */
	public boolean isInTree(T element) {
		return rootElement.contains(element, comparator);
	}

	/**
	 * Traverse the tree in a specified way until the function fails
	 * 
	 * @param linearizationMethod
	 *            The way to linearize the tree for traversal
	 * @param traversalPredicate
	 *            The function to use until it fails
	 */
	public void traverse(TreeLinearizationMethod linearizationMethod,
			Predicate<T> traversalPredicate) {
		if (linearizationMethod == null) {
			throw new NullPointerException(
					"Linearization method must not be null");
		} else if (traversalPredicate == null) {
			throw new NullPointerException("Predicate must not be nulls");
		}

		rootElement.forEach(linearizationMethod, traversalPredicate);
	}

	/**
	 * Remove all soft-deleted nodes from the tree.
	 */
	public void trim() {
		List<T> nodes = new ArrayList<>(elementCount);

		// Add all non-soft deleted nodes to the tree in insertion order
		traverse(TreeLinearizationMethod.PREORDER, node -> {
			nodes.add(node);
			return true;
		});

		// Clear the tree
		rootElement = null;

		// Add the nodes to the tree in the order they were inserted
		nodes.forEach(node -> addNode(node));
	}
}