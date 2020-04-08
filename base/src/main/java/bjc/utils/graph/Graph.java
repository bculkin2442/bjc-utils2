package bjc.utils.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import bjc.data.IHolder;
import bjc.data.Identity;
import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.IList;
import bjc.utils.funcdata.IMap;

/**
 * A directed weighted graph, where the vertices have some arbitrary label.
 *
 * @author ben
 *
 * @param <T>
 *        The label for vertices.
 */
public class Graph<T> {
	/**
	 * Create a graph from a list of edges.
	 *
	 * @param <E>
	 *        The type of data stored in the edges.
	 *
	 * @param edges
	 *        The list of edges to build from.
	 *
	 * @return A graph built from the provided edge-list.
	 */
	public static <E> Graph<E> fromEdgeList(final List<Edge<E>> edges) {
		final Graph<E> g = new Graph<>();

		edges.forEach(edge -> {
			g.addEdge(edge.getSource(), edge.getTarget(), edge.getDistance(), true);
		});

		return g;
	}

	/** The backing representation of the graph. */
	private final IMap<T, IMap<T, Integer>> backing;

	/** Create a new empty graph. */
	public Graph() {
		backing = new FunctionalMap<>();
	}

	/**
	 * Add a edge to the graph.
	 *
	 * @param source
	 *        The source vertex for this edge.
	 *
	 * @param target
	 *        The target vertex for this edge.
	 *
	 * @param distance
	 *        The distance from the source vertex to the target vertex.
	 *
	 * @param directed
	 *        Whether or not the edge is directed or not.
	 */
	public void addEdge(final T source, final T target, final int distance, final boolean directed) {
		/* Can't add edges with a null source or target. */
		if(source == null) {
			throw new NullPointerException("The source vertex cannot be null");
		} else if(target == null) {
			throw new NullPointerException("The target vertex cannot be null");
		}

		/* Initialize adjacency list for vertices if necessary. */
		if(!backing.containsKey(source)) {
			backing.put(source, new FunctionalMap<T, Integer>());
		}

		/* Add the edge to the graph. */
		backing.get(source).put(target, distance);

		/* Handle possible directed edges. */
		if(!directed) {
			if(!backing.containsKey(target)) {
				backing.put(target, new FunctionalMap<T, Integer>());
			}

			backing.get(target).put(source, distance);
		}
	}

	/**
	 * Execute an action for all edges of a specific vertex matching
	 * conditions.
	 *
	 * @param source
	 *        The vertex to test edges for.
	 *
	 * @param matcher
	 *        The conditions an edge must match.
	 *
	 * @param action
	 *        The action to execute for matching edges.
	 */
	public void forAllEdgesMatchingAt(final T source, final BiPredicate<T, Integer> matcher,
			final BiConsumer<T, Integer> action) {
		if(matcher == null) {
			throw new NullPointerException("Matcher must not be null");
		} else if(action == null) {
			throw new NullPointerException("Action must not be null");
		}

		getEdges(source).forEach((target, weight) -> {
			if(matcher.test(target, weight)) {
				action.accept(target, weight);
			}
		});
	}

	/**
	 * Get all the edges that begin at a particular source vertex.
	 *
	 * @param source
	 *        The vertex to use as a source.
	 * @return All of the edges with the specified vertex as a source.
	 */
	public IMap<T, Integer> getEdges(final T source) {
		/* Can't find edges for a null source. */
		if(source == null)
			throw new NullPointerException("The source cannot be null.");
		else if(!backing.containsKey(source))
			throw new IllegalArgumentException("Vertex " + source + " is not in graph");

		return backing.get(source);
	}

	/**
	 * Get the initial vertex of the graph.
	 *
	 * @return The initial vertex of the graph.
	 */
	public T getInitial() {
		return backing.keyList().first();
	}

	/**
	 * Uses Prim's algorothm to calculate a MST for the graph.
	 *
	 * If the graph is non-connected, this will lead to unpredictable
	 * results.
	 *
	 * @return A list of edges that constitute the MST.
	 */
	public List<Edge<T>> getMinimumSpanningTree() {
		/* Set of all of the currently available edges. */
		final Queue<Edge<T>> available = new PriorityQueue<>(10,
				(left, right) -> left.getDistance() - right.getDistance());

		/* The MST of the graph. */
		final List<Edge<T>> minimums = new ArrayList<>();

		/* The set of all of the visited vertices. */
		final Set<T> visited = new HashSet<>();

		/* Start at the initial vertex and visit it */
		final IHolder<T> source = new Identity<>(getInitial());

		visited.add(source.getValue());

		/* Make sure we visit all the nodes. */
		while(visited.size() != getVertexCount()) {
			/* Grab all edges adjacent to the provided edge. */

			forAllEdgesMatchingAt(source.getValue(), (target, weight) -> {
				return !visited.contains(target);
			}, (target, weight) -> {
				final T vert = source.unwrap(vertex -> vertex);

				available.add(new Edge<>(vert, target, weight));
			});

			/* Get the edge with the minimum distance. */
			final IHolder<Edge<T>> minimum = new Identity<>(available.poll());

			/*
			 * Only consider edges where we haven't visited the
			 * target of the edge.
			 */
			while(visited.contains(minimum.getValue().getTarget())) {
				minimum.transform((edge) -> available.poll());
			}

			/* Add it to our MST. */
			minimums.add(minimum.getValue());

			/* Advance to the next node. */
			source.transform((vertex) -> minimum.unwrap(edge -> edge.getTarget()));

			/* Visit this node. */
			visited.add(source.getValue());
		}

		return minimums;
	}

	/**
	 * Get the count of the vertices in this graph.
	 *
	 * @return A count of the vertices in this graph.
	 */
	public int getVertexCount() {
		return backing.size();
	}

	/**
	 * Get all of the vertices in this graph.
	 *
	 * @return A unmodifiable set of all the vertices in the graph.
	 */
	public IList<T> getVertices() {
		return backing.keyList();
	}

	/**
	 * Remove the edge starting at the source and ending at the target.
	 *
	 * @param source
	 *        The source vertex for the edge.
	 *
	 * @param target
	 *        The target vertex for the edge.
	 */
	public void removeEdge(final T source, final T target) {
		/* Can't remove things w/ null vertices. */
		if(source == null) {
			throw new NullPointerException("The source vertex cannot be null");
		} else if(target == null) {
			throw new NullPointerException("The target vertex cannot be null");
		}

		/* Can't remove if one vertice doesn't exists. */
		if(!backing.containsKey(source)) {
			String msg = String.format("vertex %s does not exist", source);

			throw new NoSuchElementException(msg);
		}

		if(!backing.containsKey(target)) {
			String msg = String.format("vertex %s does not exist", target);

			throw new NoSuchElementException(msg);
		}

		backing.get(source).remove(target);

		/*
		 * Uncomment this to turn the graph undirected
		 *
		 * graph.get(target).remove(source);
		 */
	}

	/**
	 * Convert a graph into a adjacency map/matrix.
	 *
	 * @return A adjacency map representing this graph.
	 */
	public AdjacencyMap<T> toAdjacencyMap() {
		final AdjacencyMap<T> adjacency = new AdjacencyMap<>(backing.keyList());

		backing.forEach((sourceKey, sourceValue) -> {
			sourceValue.forEach((targetKey, targetValue) -> {
				adjacency.setWeight(sourceKey, targetKey, targetValue);
			});
		});

		return adjacency;
	}
}
