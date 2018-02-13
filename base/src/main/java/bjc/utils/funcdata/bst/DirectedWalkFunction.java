package bjc.utils.funcdata.bst;

/**
 * Represents a function for doing a directed walk of a binary tree.
 *
 * @author ben
 *
 * @param <T>
 *        The type of element stored in the walked tree
 */
@FunctionalInterface
public interface DirectedWalkFunction<T> {
	/**
	 * Represents the results used to direct a walk in a binary tree.
	 *
	 * @author ben
	 */
	public enum DirectedWalkResult {
		/** Specifies that the function has failed. */
		FAILURE,
		/**
		 * Specifies that the function wants to move left in the tree
		 * next.
		 */
		LEFT,
		/**
		 * Specifies that the function wants to move right in the tree
		 * next.
		 */
		RIGHT,
		/** Specifies that the function has succesfully completed */
		SUCCESS
	}

	/**
	 * Perform a directed walk on a node of a tree.
	 *
	 * @param element
	 *        The data stored in the node currently being visited.
	 *
	 * @return The way the function wants the walk to go next.
	 */
	public DirectedWalkResult walk(T element);
}
