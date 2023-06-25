package bjc.utils.graph;

import java.util.*;

import bjc.data.Holder;
import bjc.data.Identity;

/**
 * General graph utilities
 * @author bjcul
 *
 */
public class Graphs {
	/**
	 * Uses Prim's algorithm to calculate a MST for the graph.
	 *
	 * If the graph is non-connected, this will lead to unpredictable results.
	 * 
	 * @param grap The graph to calculate MST for
	 * @param comp The comparator for the edges
	 * @param <T> The vertex type
	 * @param <L> The edge type
	 *
	 * @return A list of edges that constitute the MST.
	 */
	public static <T, L> List<Edge<T, L>> getMinimumSpanningTree(Graph<T, L> grap, Comparator<L> comp) {
		/* Set of all of the currently available edges. */
		final Queue<Edge<T, L>> available = new PriorityQueue<>(10,
				(left, right) -> comp.compare(left.getDistance(), right.getDistance()));

		/* The MST of the graph. */
		final List<Edge<T, L>> minimums = new ArrayList<>();

		/* The set of all of the visited vertices. */
		final Set<T> visited = new HashSet<>();

		/* Start at the initial vertex and visit it */
		final Holder<T> source = new Identity<>(grap.getInitial());

		visited.add(source.getValue());

		/* Make sure we visit all the nodes. */
		while (visited.size() != grap.getVertexCount()) {
			/* Grab all edges adjacent to the provided edge. */

			grap.forAllEdgesMatchingAt(source.getValue(),
					(target, weight) -> !visited.contains(target),
					(target, weight) -> {
						final T vert = source.unwrap(vertex -> vertex);

						available.add(new Edge<>(vert, target, weight));
					}
			);

			/* Get the edge with the minimum distance. */
			final Holder<Edge<T, L>> minimum = new Identity<>(available.poll());

			/*
			 * Only consider edges where we haven't visited the target of the edge.
			 */
			while (visited.contains(minimum.getValue().getTarget())) {
				minimum.transform(edge -> available.poll());
			}

			/* Add it to our MST. */
			minimums.add(minimum.getValue());

			/* Advance to the next node. */
			source.transform(vertex -> minimum.unwrap(edge -> edge.getTarget()));

			/* Visit this node. */
			visited.add(source.getValue());
		}

		return minimums;
	}
}
