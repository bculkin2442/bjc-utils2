package bjc.utils.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import bjc.utils.data.GenHolder;
import bjc.utils.data.IHolder;

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
	 * The backing representation of the graph
	 */
	private final Map<T, Map<T, Integer>> graph;

	/**
	 * Create a new graph
	 */
	public Graph() {
		graph = new HashMap<T, Map<T, Integer>>();
	}

	/**
	 * Add a edge to the graph
	 * 
	 * @param source
	 *            The source vertex for this edge
	 * @param target
	 *            The target vertex for this edge
	 * @param distance
	 *            The distance from the source vertex to the target vertex
	 */
	public void addEdge(T source, T target, int distance) {
		// Can't add edges with a null source or target
		if (source == null) {
			throw new NullPointerException("The vertex 1 cannot be null");
		}
		if (target == null) {
			throw new NullPointerException("The vertex 2 cannot be null");
		}

		// Initialize adjacency list for vertices if necessary
		if (!graph.containsKey(source)) {
			graph.put(source, new HashMap<T, Integer>());
		}
		if (!graph.containsKey(target)) {
			graph.put(target, new HashMap<T, Integer>());
		}

		// Add the edge to the graph
		graph.get(source).put(target, distance);

		// Uncomment this to make the graph undirected
		// graph.get(target).put(source, distance);
	}

	/**
	 * Execute an action for all edges of a specific vertex matching
	 * conditions
	 * 
	 * @param source
	 *            The vertex to test edges for
	 * @param bp
	 *            The conditions an edge must match
	 * @param bc
	 *            The action to execute for matching edges
	 */
	public void forAllEdgesMatchingAt(T source, BiPredicate<T, Integer> bp,
			BiConsumer<T, Integer> bc) {
		getEdges(source).forEach((tgt, weight) -> {
			if (bp.test(tgt, weight)) {
				bc.accept(tgt, weight);
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
	public Map<T, Integer> getEdges(T source) {
		// Can't find edges for a null source
		if (source == null) {
			throw new NullPointerException("The source cannot be null.");
		}

		return Collections.unmodifiableMap(graph.get(source));
	}

	/**
	 * Get the initial vertex of the graph
	 * 
	 * @return The initial vertex of the graph
	 */
	public T getInitial() {
		return graph.keySet().iterator().next();
	}

	/**
	 * Uses Prim's algorothm to calculate a MST for the graph. If the graph
	 * is non-connected, this will lead to unpredictable results.
	 * 
	 * @param graph
	 *            a connected graph.
	 * @return a list of edges that constitute the MST
	 */
	public List<Edge<T>> getMinSpanTree() {
		// Set of all of the currently available edges
		Queue<Edge<T>> availEdges = new PriorityQueue<Edge<T>>(10,
				(e1, e2) -> e1.getDistance() - e2.getDistance());

		// The MST of the graph
		List<Edge<T>> minEdges = new ArrayList<Edge<T>>();

		// The set of all of the visited vertices.
		Set<T> visited = new HashSet<T>();

		// Start at the initial vertex and visit it
		IHolder<T> src = new GenHolder<>(getInitial());
		visited.add(src.unwrap(vl -> vl));

		// Make sure we visit all the nodes
		while (visited.size() != getVertexCount()) {
			// Grab all edges adjacent to the provided edge

			forAllEdgesMatchingAt(src.unwrap(vl -> vl),
					(tgt, weight) -> !visited.contains(tgt),
					(tgt, weight) -> availEdges.add(new Edge<T>(
							src.unwrap(vl -> vl), tgt, weight)));

			// Get the edge with the minimum distance
			IHolder<Edge<T>> minEdge = new GenHolder<>(
					availEdges.poll());

			// Only consider edges where we haven't visited the target of
			// the edge
			while (visited
					.contains(minEdge.unwrap(vl -> vl.getTarget()))) {
				minEdge.transform((vl) -> availEdges.poll());
			}

			// Add it to our MST
			minEdges.add(minEdge.unwrap(vl -> vl));

			// Advance to the next node
			src.transform((vl) -> minEdge.unwrap(vl1 -> vl1.getTarget()));

			// Visit this node
			visited.add(src.unwrap(vl -> vl));
		}

		return minEdges;
	}

	/**
	 * Get the count of the vertices in this graph
	 * 
	 * @return A count of the vertices in this graph
	 */
	public int getVertexCount() {
		return graph.size();
	}

	/**
	 * Get all of the vertices in this graph.
	 * 
	 * @return A unmodifiable set of all the vertices in the graph.
	 */
	public Set<T> getVertices() {
		return Collections.unmodifiableSet(graph.keySet());
	}

	/**
	 * Remove the edge starting at the source and ending at the target
	 * 
	 * @param source
	 *            The source vertex for the edge
	 * @param target
	 *            The target vertex for the edge
	 */
	public void removeEdge(T source, T target) {
		// Can't remove things w/ null vertices
		if (source == null) {
			throw new NullPointerException("The vertex 1 cannot be null");
		}
		if (target == null) {
			throw new NullPointerException("The vertex 2 cannot be null");
		}

		// Can't remove if one vertice doesn't exists
		if (!graph.containsKey(source)) {
			throw new NoSuchElementException(
					"vertex " + source + " does not exist.");
		}
		if (!graph.containsKey(target)) {
			throw new NoSuchElementException(
					"vertex " + target + " does not exist.");
		}

		graph.get(source).remove(target);

		// Uncomment this to turn the graph undirected
		// graph.get(target).remove(source);
	}

	/**
	 * Convert a graph into a adjacency map/matrix
	 * 
	 * @return A adjacency map representing this graph
	 */
	public AdjacencyMap<T> toMap() {
		AdjacencyMap<T> aMap = new AdjacencyMap<>(graph.keySet());

		graph.entrySet().forEach(src -> src.getValue().forEach((tgt,
				weight) -> aMap.setWeight(src.getKey(), tgt, weight)));

		return aMap;
	}

	/**
	 * Create a graph from a list of edges
	 * 
	 * @param edges
	 *            The list of edges to build from
	 * @return A graph built from the provided edge-list
	 */
	public static <E> Graph<E> fromEdgeList(List<Edge<E>> edges) {
		Graph<E> g = new Graph<>();

		edges.forEach(edge -> g.addEdge(edge.getSource(), edge.getTarget(),
				edge.getDistance()));

		return g;
	}
}
