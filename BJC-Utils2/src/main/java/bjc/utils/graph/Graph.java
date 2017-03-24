package bjc.utils.graph;

import bjc.utils.data.IHolder;
import bjc.utils.data.Identity;
import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.IList;
import bjc.utils.funcdata.IMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * A directed weighted graph, where the vertices have some arbitrary label
 *
 * @author ben
 *
 * @param <T>
 *                The label for vertices
 */
public class Graph<T> {
	/**
	 * Create a graph from a list of edges
	 *
	 * @param <E>
	 *                The type of data stored in the edges
	 *
	 * @param edges
	 *                The list of edges to build from
	 * @return A graph built from the provided edge-list
	 */
	public static <E> Graph<E> fromEdgeList(List<Edge<E>> edges) {
		Graph<E> g = new Graph<>();

		edges.forEach(edge -> {
			g.addEdge(edge.getSource(), edge.getTarget(), edge.getDistance(), true);
		});

		return g;
	}

	/**
	 * The backing representation of the graph
	 */
	private final IMap<T, IMap<T, Integer>> backing;

	/**
	 * Create a new graph
	 */
	public Graph() {
		backing = new FunctionalMap<>();
	}

	/**
	 * Add a edge to the graph
	 *
	 * @param source
	 *                The source vertex for this edge
	 * @param target
	 *                The target vertex for this edge
	 * @param distance
	 *                The distance from the source vertex to the target
	 *                vertex
	 * @param directed
	 *                Whether or not
	 */
	public void addEdge(T source, T target, int distance, boolean directed) {
		// Can't add edges with a null source or target
		if(source == null)
			throw new NullPointerException("The source vertex cannot be null");
		else if(target == null) throw new NullPointerException("The target vertex cannot be null");

		// Initialize adjacency list for vertices if necessary
		if(!backing.containsKey(source)) {
			backing.put(source, new FunctionalMap<T, Integer>());
		}

		// Add the edge to the graph
		backing.get(source).put(target, distance);

		// Handle possible directed edges
		if(!directed) {
			if(!backing.containsKey(target)) {
				backing.put(target, new FunctionalMap<T, Integer>());
			}

			backing.get(target).put(source, distance);
		}
	}

	/**
	 * Execute an action for all edges of a specific vertex matching
	 * conditions
	 *
	 * @param source
	 *                The vertex to test edges for
	 * @param matcher
	 *                The conditions an edge must match
	 * @param action
	 *                The action to execute for matching edges
	 */
	public void forAllEdgesMatchingAt(T source, BiPredicate<T, Integer> matcher, BiConsumer<T, Integer> action) {
		if(matcher == null)
			throw new NullPointerException("Matcher must not be null");
		else if(action == null) throw new NullPointerException("Action must not be null");

		getEdges(source).forEach((target, weight) -> {
			if(matcher.test(target, weight)) {
				action.accept(target, weight);
			}
		});
	}

	/**
	 * Get all the edges that begin at a particular source vertex
	 *
	 * @param source
	 *                The vertex to use as a source
	 * @return All of the edges with the specified vertex as a source
	 */
	public IMap<T, Integer> getEdges(T source) {
		// Can't find edges for a null source
		if(source == null)
			throw new NullPointerException("The source cannot be null.");
		else if(!backing.containsKey(source))
			throw new IllegalArgumentException("Vertex " + source + " is not in graph");

		return backing.get(source);
	}

	/**
	 * Get the initial vertex of the graph
	 *
	 * @return The initial vertex of the graph
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
	 * @return a list of edges that constitute the MST
	 */
	public List<Edge<T>> getMinimumSpanningTree() {
		// Set of all of the currently available edges
		Queue<Edge<T>> available = new PriorityQueue<>(10,
				(left, right) -> left.getDistance() - right.getDistance());

		// The MST of the graph
		List<Edge<T>> minimums = new ArrayList<>();

		// The set of all of the visited vertices.
		Set<T> visited = new HashSet<>();

		// Start at the initial vertex and visit it
		IHolder<T> source = new Identity<>(getInitial());

		visited.add(source.getValue());

		// Make sure we visit all the nodes
		while(visited.size() != getVertexCount()) {
			// Grab all edges adjacent to the provided edge

			forAllEdgesMatchingAt(source.getValue(), (target, weight) -> {
				return !visited.contains(target);
			}, (target, weight) -> {
				T vert = source.unwrap(vertex -> vertex);

				available.add(new Edge<>(vert, target, weight));
			});

			// Get the edge with the minimum distance
			IHolder<Edge<T>> minimum = new Identity<>(available.poll());

			// Only consider edges where we haven't visited the
			// target of
			// the edge
			while(visited.contains(minimum.getValue())) {
				minimum.transform((edge) -> available.poll());
			}

			// Add it to our MST
			minimums.add(minimum.getValue());

			// Advance to the next node
			source.transform((vertex) -> minimum.unwrap(edge -> edge.getTarget()));

			// Visit this node
			visited.add(source.getValue());
		}

		return minimums;
	}

	/**
	 * Get the count of the vertices in this graph
	 *
	 * @return A count of the vertices in this graph
	 */
	public int getVertexCount() {
		return backing.getSize();
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
	 * Remove the edge starting at the source and ending at the target
	 *
	 * @param source
	 *                The source vertex for the edge
	 * @param target
	 *                The target vertex for the edge
	 */
	public void removeEdge(T source, T target) {
		// Can't remove things w/ null vertices
		if(source == null)
			throw new NullPointerException("The source vertex cannot be null");
		else if(target == null) throw new NullPointerException("The target vertex cannot be null");

		// Can't remove if one vertice doesn't exists
		if(!backing.containsKey(source))
			throw new NoSuchElementException("vertex " + source + " does not exist.");

		if(!backing.containsKey(target))
			throw new NoSuchElementException("vertex " + target + " does not exist.");

		backing.get(source).remove(target);

		// Uncomment this to turn the graph undirected
		// graph.get(target).remove(source);
	}

	/**
	 * Convert a graph into a adjacency map/matrix
	 *
	 * @return A adjacency map representing this graph
	 */
	public AdjacencyMap<T> toAdjacencyMap() {
		AdjacencyMap<T> adjacency = new AdjacencyMap<>(backing.keyList());

		backing.forEach((sourceKey, sourceValue) -> {
			sourceValue.forEach((targetKey, targetValue) -> {
				adjacency.setWeight(sourceKey, targetKey, targetValue);
			});
		});

		return adjacency;
	}
}
