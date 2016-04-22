package bjc.utils.funcdata;

/**
 * Represents the results for doing a top-down transform of a tree
 * 
 * @author ben
 *
 */
public enum TopDownTransformResult {
	/**
	 * Do not do anything to this node, and ignore it's children
	 */
	SKIP,
	/**
	 * Transform this node, and don't touch its children
	 */
	TRANSFORM,
	/**
	 * Ignore this node, and traverse its children
	 */
	PASSTHROUGH,
	/**
	 * Traverse this nodes children, then transform it
	 */
	PUSHDOWN,
	/**
	 * Transform this node, then traverse its children
	 */
	PULLUP;
}
