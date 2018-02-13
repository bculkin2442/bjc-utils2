package bjc.utils.funcutils;

import java.util.LinkedList;
import java.util.function.Predicate;

import bjc.utils.data.ITree;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

/**
 * Implements various utilities for trees.
 *
 * @author Benjamin Culkin
 */
public class TreeUtils {
	/*
	 * Convert a tree into a list of outline nodes that match a certain
	 * path.
	 */
	public static <T> IList<IList<T>> outlineTree(ITree<T> tre, Predicate<T> leafMarker) {
		IList<IList<T>> paths = new FunctionalList<>();

		LinkedList<T> path = new LinkedList<>();
		path.add(tre.getHead());

		tre.doForChildren((child) -> findPath(child, path, leafMarker, paths));

		return paths;
	}

	/* Find a path in a tree. */
	private static <T> void findPath(ITree<T> subtree, LinkedList<T> path, Predicate<T> leafMarker,
			IList<IList<T>> paths) {
		if(subtree.getChildrenCount() == 0 && leafMarker.test(subtree.getHead())) {
			/* We're at a matching leaf node. Add it. */
			IList<T> finalPath = new FunctionalList<>();

			for(T ePath : path) {
				finalPath.add(ePath);
			}

			finalPath.add(subtree.getHead());

			paths.add(finalPath);
		} else {
			/* Check the children of this node. */
			path.add(subtree.getHead());

			subtree.doForChildren((child) -> findPath(child, path, leafMarker, paths));

			path.removeLast();
		}
	}
}
