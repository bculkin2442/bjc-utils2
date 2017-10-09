package bjc.utils.data;

/**
 * Represents the results for doing a top-down transform of a tree
 *
 * @author ben
 *
 */
public enum TopDownTransformResult {
	/**
	 * Do not do anything to this node, and ignore its children
	 */
	SKIP,
	/**
	 * Transform this node, and don't touch its children
	 */
	TRANSFORM,
	/**
	 * Transform this node, then do a top-down transform on the result
	 */
	RTRANSFORM,
	/**
	 * Ignore this node, and traverse its children
	 */
	PASSTHROUGH,
	/**
	 * Traverse the nodes of this children, then transform it
	 */
	PUSHDOWN,
	/**
	 * Transform this node, then traverse its children
	 */
	PULLUP;
}
