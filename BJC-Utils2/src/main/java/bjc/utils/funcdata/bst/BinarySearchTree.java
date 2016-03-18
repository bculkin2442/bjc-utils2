package bjc.utils.funcdata.bst;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.ITreePart;
import bjc.utils.funcdata.ITreePart.TreeLinearizationMethod;

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
	private Comparator<T>	comp;

	/**
	 * The current count of elements in the tree
	 */
	private int				nCount;

	/**
	 * The root element of the tree
	 */
	private ITreePart<T>	root;

	/**
	 * Create a new tree using the specified way to compare elements.
	 * 
	 * @param cmp
	 */
	public BinarySearchTree(Comparator<T> cmp) {
		nCount = 0;
		comp = cmp;
	}

	/**
	 * Add a node to the binary search tree.
	 * 
	 * @param dat
	 *            The data to add to the binary search tree.
	 */
	public void addNode(T dat) {
		nCount++;

		if (root == null) {
			root = new BinarySearchTreeNode<T>(dat, null, null);
		} else {
			root.add(dat, comp);
		}
	}

	/**
	 * Balance the tree, and remove soft-deleted nodes for free. Takes O(N)
	 * time, but also O(N) space.
	 */
	public void balance() {
		FunctionalList<T> elms = new FunctionalList<>();

		root.forEach(TreeLinearizationMethod.INORDER, e -> elms.add(e));

		root = null;

		int piv = elms.getSize() / 2;
		int adj = 0;

		while ((piv - adj) >= 0 && (piv + adj) < elms.getSize()) {
			if (root == null) {
				root = new BinarySearchTreeNode<T>(elms.getByIndex(piv), null, null);
			} else {
				root.add(elms.getByIndex(piv + adj), comp);
				root.add(elms.getByIndex(piv - adj), comp);
			}

			adj++;
		}

		if ((piv - adj) >= 0) {
			root.add(elms.getByIndex(piv - adj), comp);
		} else if ((piv + adj) < elms.getSize()) {
			root.add(elms.getByIndex(piv + adj), comp);
		}
	}

	/**
	 * Soft-delete a node from the tree. Soft-deleted nodes stay in the
	 * tree until trim()/balance() is invoked, and are not included in
	 * traversals/finds.
	 * 
	 * @param dat
	 */
	public void deleteNode(T dat) {
		nCount--;

		root.delete(dat, comp);
	}

	/**
	 * Get the root of the tree.
	 * 
	 * @return The root of the tree.
	 */
	public ITreePart<T> getRoot() {
		return root;
	}

	/**
	 * Check if a node is in the tree
	 * 
	 * @param dat
	 *            The node to check the presence of for the tree.
	 * @return Whether or not the node is in the tree.
	 */
	public boolean isInTree(T dat) {
		return root.contains(dat, comp);
	}

	/**
	 * Traverse the tree in a specified way until the function fails
	 * 
	 * @param tlm
	 *            The way to linearize the tree for traversal
	 * @param p
	 *            The function to use until it fails
	 */
	public void traverse(TreeLinearizationMethod tlm, Predicate<T> p) {
		root.forEach(tlm, p);
	}

	/**
	 * Remove all soft-deleted nodes from the tree.
	 */
	public void trim() {
		List<T> nds = new ArrayList<>(nCount);

		traverse(TreeLinearizationMethod.PREORDER, d -> {
			nds.add(d);
			return true;
		});

		root = null;

		nds.forEach(d -> addNode(d));
	}
}
