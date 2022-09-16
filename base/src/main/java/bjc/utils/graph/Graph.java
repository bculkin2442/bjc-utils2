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

import bjc.data.Holder;
import bjc.data.Identity;
import bjc.funcdata.FunctionalMap;
import bjc.funcdata.ListEx;
import bjc.funcdata.MapEx;

/**
 * A directed weighted graph, where the vertices have some arbitrary label.
 *
 * @author ben
 *
 * @param <VertexLabel>
 *            The label for vertices.
 */
public class Graph<VertexLabel, EdgeLabel> {
	/**
	 * Create a graph from a list of edges.
	 *
	 * @param <E>
	 *              The type of data stored in the edges.
	 *
	 * @param edges
	 *              The list of edges to build from.
	 *
	 * @return A graph built from the provided edge-list.
	 */
	public static <E, L> Graph<E, L> fromEdgeList(final List<Edge<E, L>> edges) {
		final Graph<E, L> g = new Graph<>();

		edges.forEach(edge -> {
			g.addEdge(edge.getSource(), edge.getTarget(), edge.getDistance(), true);
		});

		return g;
	}

	/** The backing representation of the graph. */
	private final MapEx<VertexLabel, MapEx<VertexLabel, EdgeLabel>> backing;

	/** Create a new empty graph. */
	public Graph() {
		backing = new FunctionalMap<>();
	}

	/**
	 * Add a edge to the graph.
	 *
	 * @param source
	 *                 The source vertex for this edge.
	 *
	 * @param target
	 *                 The target vertex for this edge.
	 *
	 * @param label
	 *                 The distance from the source vertex to the target vertex.
	 *
	 * @param directed
	 *                 Whether or not the edge is directed or not.
	 */
	public void addEdge(final VertexLabel source, final VertexLabel target, final EdgeLabel label,
			final boolean directed) {
		/* Can't add edges with a null source or target. */
		if (source == null)      throw new NullPointerException("The source vertex cannot be null");
		else if (target == null) throw new NullPointerException("The target vertex cannot be null");

		/* Initialize adjacency list for vertices if necessary. */
		if (!backing.containsKey(source)) backing.put(source, new FunctionalMap<VertexLabel, EdgeLabel>());

		/* Add the edge to the graph. */
		backing.get(source).get().put(target, label);

		/* Handle possible directed edges. */
		if (!directed) {
			if (!backing.containsKey(target)) {
				backing.put(target, new FunctionalMap<VertexLabel, EdgeLabel>());
			}

			backing.get(target).get().put(source, label);
		}
	}

	/**
	 * Execute an action for all edges of a specific vertex matching conditions.
	 *
	 * @param source
	 *                The vertex to test edges for.
	 *
	 * @param matcher
	 *                The conditions an edge must match.
	 *
	 * @param action
	 *                The action to execute for matching edges.
	 */
	public void forAllEdgesMatchingAt(final VertexLabel source,
			final BiPredicate<VertexLabel, EdgeLabel> matcher, final BiConsumer<VertexLabel, EdgeLabel> action) {
		if (matcher == null)     throw new NullPointerException("Matcher must not be null");
		else if (action == null) throw new NullPointerException("Action must not be null");

		getEdges(source).forEach((target, label) -> {
			if (matcher.test(target, label)) action.accept(target, label);
		});
	}

	/**
	 * Get all the edges that begin at a particular source vertex.
	 *
	 * @param source
	 *               The vertex to use as a source.
	 * @return All of the edges with the specified vertex as a source.
	 */
	public MapEx<VertexLabel, EdgeLabel> getEdges(final VertexLabel source) {
		/* Can't find edges for a null source. */
		if (source == null) {
			throw new NullPointerException("The source cannot be null.");
		} else if (!backing.containsKey(source)) {
			throw new IllegalArgumentException("Vertex " + source + " is not in graph");
		}

		return backing.get(source).get();
	}

	/**
	 * Get the initial vertex of the graph.
	 *
	 * @return The initial vertex of the graph.
	 */
	public VertexLabel getInitial() {
		return backing.keyList().first();
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
	public ListEx<VertexLabel> getVertices() {
		return backing.keyList();
	}

	/**
	 * Remove the edge starting at the source and ending at the target.
	 *
	 * @param source
	 *               The source vertex for the edge.
	 *
	 * @param target
	 *               The target vertex for the edge.
	 */
	public void removeEdge(final VertexLabel source, final VertexLabel target) {
		/* Can't remove things w/ null vertices. */
		if (source == null) {
			throw new NullPointerException("The source vertex cannot be null");
		} else if (target == null) {
			throw new NullPointerException("The target vertex cannot be null");
		}

		/* Can't remove if one vertice doesn't exists. */
		if (!backing.containsKey(source)) {
			String msg = String.format("vertex %s does not exist", source);

			throw new NoSuchElementException(msg);
		}

		if (!backing.containsKey(target)) {
			String msg = String.format("vertex %s does not exist", target);

			throw new NoSuchElementException(msg);
		}

		backing.get(source).get().remove(target);

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
	public AdjacencyMap<VertexLabel, EdgeLabel> toAdjacencyMap() {
		final AdjacencyMap<VertexLabel, EdgeLabel> adjacency = new AdjacencyMap<>(backing.keyList());

		backing.forEach((sourceKey, sourceValue) -> {
			sourceValue.forEach((targetKey, targetValue) -> {
				adjacency.setLabel(sourceKey, targetKey, targetValue);
			});
		});

		return adjacency;
	}
}
