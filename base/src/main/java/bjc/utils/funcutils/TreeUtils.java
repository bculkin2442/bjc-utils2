package bjc.utils.funcutils;

import java.util.LinkedList;
import java.util.function.*;

import bjc.data.*;
import bjc.funcdata.*;

/**
 * Implements various utilities for trees.
 *
 * @author Benjamin Culkin
 */
public class TreeUtils {
	/**
	 * Convert a tree into a list of outline nodes that match a certain path.
	 *
	 * @param tre
	 *                   The tree to outline.
	 * @param leafMarker
	 *                   The path to mark nodes with.
	 * @return The list of marked paths.
	 */
	public static <T> IList<IList<T>> outlineTree(ITree<T> tre, Predicate<T> leafMarker) {
		IList<IList<T>> paths = new FunctionalList<>();

		LinkedList<T> path = new LinkedList<>();
		path.add(tre.getHead());

		tre.doForChildren(child -> findPath(child, path, leafMarker, paths));

		return paths;
	}

	/* Find a path in a tree. */
	private static <T> void findPath(ITree<T> subtree, LinkedList<T> path,
			Predicate<T> leafMarker, IList<IList<T>> paths) {
		if (subtree.getChildrenCount() == 0 && leafMarker.test(subtree.getHead())) {
			/* We're at a matching leaf node. Add it. */
			IList<T> finalPath = new FunctionalList<>();

			for (T ePath : path) finalPath.add(ePath);

			finalPath.add(subtree.getHead());

			paths.add(finalPath);
		} else {
			/* Check the children of this node. */
			path.add(subtree.getHead());

			subtree.doForChildren(child -> findPath(child, path, leafMarker, paths));

			path.removeLast();
		}
	}
	
	/**
	 * Performs 'variable substitution' or something along those lines on a tree.
	 * 
	 * @param <ContainedType> The type of element contained in the tree.
	 * @param tree The tree to do expansion in.
	 * @param marker The function to mark which nodes should be expanded.
	 * @param expander The function to expand nodes.
	 * @return A transformed copy of the tree.
	 */
	public static <ContainedType> ITree<ContainedType> substitute(
			ITree<ContainedType> tree,
			Predicate<ContainedType> marker,
			Function<ContainedType, ITree<ContainedType>> expander) {
		tree.topDownTransform((contents) -> {
			if (marker.test(contents)) return TopDownTransformResult.TRANSFORM;
			else                       return TopDownTransformResult.PASSTHROUGH;
		}, (node) -> {
			return expander.apply(node.getHead());
		});
		return tree;
	}
	
	/**
	 * Performs 'variable substitution' or something along those lines on a tree.
	 * 
	 * @param <ContainedType> The type of element contained in the tree.
	 * @param tree The tree to do expansion in.
	 * @param environment A map which contains the variables to substitute.
	 * @return A transformed copy of the tree.
	 */
	public static <ContainedType> ITree<ContainedType> substitute(
			ITree<ContainedType> tree,
			IMap<ContainedType, ITree<ContainedType>> environment) {
		return substitute(
				tree,
				environment::containsKey,
				(element) -> environment.get(element).get());
	}
}
