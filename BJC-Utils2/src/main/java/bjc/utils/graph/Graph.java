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

import bjc.utils.data.IHolder;
import bjc.utils.data.Identity;
import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.IList;
import bjc.utils.funcdata.IMap;

/**
 * A directed weighted graph, where the vertices have some arbitrary label
 * 
 * @author ben
 *
 * @param <T>
 *            The label for vertices
 */
public class Graph<T> {
	/**
	 * Create a graph from a list of edges
	 * 
	 * @param <E>
	 *            The type of data stored in the edges
	 * 
	 * @param edges
	 *            The list of edges to build from
	 * @return A graph built from the provided edge-list
	 */
	public static <E> Graph<E> fromEdgeList(List<Edge<E>> edges) {
		Graph<E> g = new Graph<>();

		edges.forEach(edge -> {
			g.addEdge(edge.getSource(), edge.getTarget(),
					edge.getDistance(), true);
		});

		return g;
	}

	/**
	 * The backing representation of the graph
	 */
	private final IMap<T, IMap<T, Integer>> backingGraph;

	/**
	 * Create a new graph
	 */
	public Graph() {
		backingGraph = new FunctionalMap<>();
	}

	/**
	 * Add a edge to the graph
	 * 
	 * @param sourceVertex
	 *            The source vertex for this edge
	 * @param targetVertex
	 *            The target vertex for this edge
	 * @param distance
	 *            The distance from the source vertex to the target vertex
	 * @param directed
	 *            Whether or not
	 */
	public void addEdge(T sourceVertex, T targetVertex, int distance,
			boolean directed) {
		// Can't add edges with a null source or target
		if (sourceVertex == null) {
			throw new NullPointerException(
					"The source vertex cannot be null");
		} else if (targetVertex == null) {
			throw new NullPointerException(
					"The target vertex cannot be null");
		}

		// Initialize adjacency list for vertices if necessary
		if (!backingGraph.containsKey(sourceVertex)) {
			backingGraph.put(sourceVertex,
					new FunctionalMap<T, Integer>());
		}

		// Add the edge to the graph
		backingGraph.get(sourceVertex).put(targetVertex, distance);

		// Handle possible directed edges
		if (!directed) {
			if (!backingGraph.containsKey(targetVertex)) {
				backingGraph.put(targetVertex,
						new FunctionalMap<T, Integer>());
			}

			backingGraph.get(targetVertex).put(sourceVertex, distance);
		}
	}

	/**
	 * Execute an action for all edges of a specific vertex matching
	 * conditions
	 * 
	 * @param sourceVertex
	 *            The vertex to test edges for
	 * @param edgeMatcher
	 *            The conditions an edge must match
	 * @param edgeAction
	 *            The action to execute for matching edges
	 */
	public void forAllEdgesMatchingAt(T sourceVertex,
			BiPredicate<T, Integer> edgeMatcher,
			BiConsumer<T, Integer> edgeAction) {
		if (edgeMatcher == null) {
			throw new NullPointerException("Matcher must not be null");
		} else if (edgeAction == null) {
			throw new NullPointerException("Action must not be null");
		}

		getEdges(sourceVertex).forEach((targetVertex, vertexWeight) -> {
			if (edgeMatcher.test(targetVertex, vertexWeight)) {
				edgeAction.accept(targetVertex, vertexWeight);
			}
		});
	}

	/**
	 * Get all the edges that begin at a particular source vertex
	 * 
	 * @param source
	 *            The vertex to use as a source
	 * @return All of the edges with the specified vertex as a source
	 */
	public IMap<T, Integer> getEdges(T source) {
		// Can't find edges for a null source
		if (source == null) {
			throw new NullPointerException("The source cannot be null.");
		} else if (!backingGraph.containsKey(source)) {
			throw new IllegalArgumentException(
					"Vertex " + source + " is not in graph");
		}

		return backingGraph.get(source);
	}

	/**
	 * Get the initial vertex of the graph
	 * 
	 * @return The initial vertex of the graph
	 */
	public T getInitial() {
		return backingGraph.keyList().first();
	}

	/**
	 * Uses Prim's algorothm to calculate a MST for the graph. If the graph
	 * is non-connected, this will lead to unpredictable results.
	 * 
	 * @return a list of edges that constitute the MST
	 */
	public List<Edge<T>> getMinimumSpanningTree() {
		// Set of all of the currently available edges
		Queue<Edge<T>> availableEdges = new PriorityQueue<>(10,
				(leftEdge, rightEdge) -> leftEdge.getDistance()
						- rightEdge.getDistance());

		// The MST of the graph
		List<Edge<T>> minimumEdges = new ArrayList<>();

		// The set of all of the visited vertices.
		Set<T> visitedVertexes = new HashSet<>();

		// Start at the initial vertex and visit it
		IHolder<T> sourceVertex = new Identity<>(getInitial());

		visitedVertexes.add(sourceVertex.getValue());

		// Make sure we visit all the nodes
		while (visitedVertexes.size() != getVertexCount()) {
			// Grab all edges adjacent to the provided edge

			forAllEdgesMatchingAt(sourceVertex.getValue(),
					(targetVertex, vertexWeight) -> {
						return !visitedVertexes.contains(targetVertex);
					}, (targetVertex, vertexWeight) -> {
						availableEdges.add(new Edge<>(
								sourceVertex.unwrap(vertex -> vertex),
								targetVertex, vertexWeight));
					});

			// Get the edge with the minimum distance
			IHolder<Edge<T>> minimumEdge = new Identity<>(
					availableEdges.poll());

			// Only consider edges where we haven't visited the target of
			// the edge
			while (visitedVertexes.contains(minimumEdge.getValue())) {
				minimumEdge.transform((edge) -> availableEdges.poll());
			}

			// Add it to our MST
			minimumEdges.add(minimumEdge.getValue());

			// Advance to the next node
			sourceVertex.transform((vertex) -> minimumEdge
					.unwrap(edge -> edge.getTarget()));

			// Visit this node
			visitedVertexes.add(sourceVertex.getValue());
		}

		return minimumEdges;
	}

	/**
	 * Get the count of the vertices in this graph
	 * 
	 * @return A count of the vertices in this graph
	 */
	public int getVertexCount() {
		return backingGraph.getSize();
	}

	/**
	 * Get all of the vertices in this graph.
	 * 
	 * @return A unmodifiable set of all the vertices in the graph.
	 */
	public IList<T> getVertices() {
		return backingGraph.keyList();
	}

	/**
	 * Remove the edge starting at the source and ending at the target
	 * 
	 * @param sourceVertex
	 *            The source vertex for the edge
	 * @param targetVertex
	 *            The target vertex for the edge
	 */
	public void removeEdge(T sourceVertex, T targetVertex) {
		// Can't remove things w/ null vertices
		if (sourceVertex == null) {
			throw new NullPointerException(
					"The source vertex cannot be null");
		} else if (targetVertex == null) {
			throw new NullPointerException(
					"The target vertex cannot be null");
		}

		// Can't remove if one vertice doesn't exists
		if (!backingGraph.containsKey(sourceVertex)) {
			throw new NoSuchElementException(
					"vertex " + sourceVertex + " does not exist.");
		}

		if (!backingGraph.containsKey(targetVertex)) {
			throw new NoSuchElementException(
					"vertex " + targetVertex + " does not exist.");
		}

		backingGraph.get(sourceVertex).remove(targetVertex);

		// Uncomment this to turn the graph undirected
		// graph.get(target).remove(source);
	}

	/**
	 * Convert a graph into a adjacency map/matrix
	 * 
	 * @return A adjacency map representing this graph
	 */
	public AdjacencyMap<T> toAdjacencyMap() {
		AdjacencyMap<T> adjacencyMap = new AdjacencyMap<>(
				backingGraph.keyList());

		backingGraph.forEach((key, value) -> {
			value.forEach((targetKey, targetValue) -> {
				adjacencyMap.setWeight(key, targetKey, targetValue);
			});
		});

		return adjacencyMap;
	}
}