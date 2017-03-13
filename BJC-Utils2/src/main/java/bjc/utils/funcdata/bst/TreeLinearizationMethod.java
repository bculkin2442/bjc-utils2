package bjc.utils.funcdata.bst;

/**
 * Represents the ways to linearize a tree for traversal.
 * 
 * @author ben
 *
 */
public enum TreeLinearizationMethod {
	/**
	 * Visit the left side of this tree part, the tree part itself, and then
	 * the right part.
	 */
	INORDER,
	/**
	 * Visit the left side of this tree part, the right side, and then the
	 * tree part itself.
	 */
	POSTORDER,
	/**
	 * Visit the tree part itself, then the left side of tthis tree part and
	 * then the right part.
	 */
	PREORDER
}