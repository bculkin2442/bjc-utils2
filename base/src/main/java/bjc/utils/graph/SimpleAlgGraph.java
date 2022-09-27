package bjc.utils.graph;

import java.util.*;

/**
 * A basic implementation of a directed algebraic graph.
 * 
 * @author bjcul
 *
 * @param <Vertex> The type of the vertices
 */
public class SimpleAlgGraph<Vertex> implements AlgGraph<Vertex, SimpleAlgGraph<?>> {
	// TODO: consider if some function for labeling edges would make sense
	private Set<Vertex> vertices;
	private Map<Vertex, Vertex> edges;

	/**
	 * Create a new empty graph.
	 */
	public SimpleAlgGraph() {
		vertices = new HashSet<>();
		edges = new HashMap<>();
	}

	/**
	 * Create a new graph with the given vertices, but no edges.
	 * 
	 * @param vertexes The vertices for the graph.
	 */
	@SafeVarargs
	public SimpleAlgGraph(Vertex... vertexes) {
		this();

		for (Vertex vertex : vertexes)
			vertices.add(vertex);
	}

	@Override
	public <B> SimpleAlgGraph<B> empty() {
		return new SimpleAlgGraph<>();
	}
	
	@Override
	public <B> SimpleAlgGraph<B> vertex(B val) {
		return new SimpleAlgGraph<>();
	}
	
	/**
	 * Overlay a graph onto this one, adding its vertices and edges.
	 * 
	 * @param other The graph to overlay
	 */
	@Override
	public void overlay(AlgGraph<Vertex, SimpleAlgGraph<?>> other) {
		SimpleAlgGraph<Vertex> graph = (SimpleAlgGraph<Vertex>) other;
		
		vertices.addAll(graph.vertices);
		edges.putAll(graph.edges);
	}

	/**
	 * Connect a graph to this one, adding its vertices and edges; as well as
	 * establishing an edge from each node in this graph, to each node in the other
	 * graph.
	 * 
	 * @param other The graph to connect
	 */
	@Override
	public void connect(AlgGraph<Vertex, SimpleAlgGraph<?>> other) {
		SimpleAlgGraph<Vertex> graph = (SimpleAlgGraph<Vertex>) other;
	
		// note: this could be inefficent for large graphs
		for (Vertex left : vertices) {
			for (Vertex right : graph.vertices) {
				edges.put(left, right);
			}
		}

		overlay(other);
	}


}