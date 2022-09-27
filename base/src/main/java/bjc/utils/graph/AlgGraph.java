package bjc.utils.graph;

import bjc.functypes.Container;

/**
 * A directed algebraic graph
 * 
 * (ref. Algebraic Graphs with Class, Andrey Mokhov}
 * 
 * @author bjcul
 *
 * @param <Vertex> The type of the vertexes
 * @param <PGraph> Containing type parameter
 */
public interface AlgGraph<Vertex, PGraph extends AlgGraph<?, PGraph>> extends Container<Vertex, AlgGraph<?, PGraph>> {
	/**
	 * Create a empty algebraic graph.
	 * 
	 * @param <B> The type of the vertices
	 * 
	 * @return A new empty graph.
	 */
	<B> AlgGraph<B, PGraph> empty();
	
	/**
	 * Create a algebraic graph with a single vertex
	 * 
	 * @param val The value for the vertex
	 * 
	 * @return A new graph with the given vertex
	 */
	<B> AlgGraph<B, PGraph> vertex(B val);
	
	/**
	 * Overlay a graph onto this one, adding its vertices and edges.
	 * 
	 * @param other The graph to overlay
	 */
	void overlay(AlgGraph<Vertex, PGraph> other);

	/**
	 * Connect a graph to this one, adding its vertices and edges; as well as
	 * establishing an edge from each node in this graph, to each node in the other
	 * graph.
	 * 
	 * @param other The graph to connect
	 */
	void connect(AlgGraph<Vertex, PGraph> other);
	
	/**
	 * Overlay two graphs into a new graph.
	 * 
	 * @param <Vertex> The type of the vertices.
	 * 
	 * @param left     The graph to overlay onto
	 * @param right    The graph being overlaid
	 * 
	 * @return The overlaid graph.
	 */
	public static <Vertex, G extends AlgGraph<Vertex, G>> G overlay(G left, G right) {
		@SuppressWarnings("unchecked")
		// We know this cast is good, java just can't tell
		G result = (G) left.empty();
		result.overlay(left);
		result.overlay(right);

		return result;
	}

	/**
	 * Connect two graphs into a new graph.
	 * 
	 * @param <Vertex> The type of the vertices.
	 * 
	 * @param left     The graph to connect to
	 * @param right    The graph being connected
	 * 
	 * @return The connected graph.
	 */
	public static <Vertex, G extends AlgGraph<Vertex, G>> G connect(G left, G right) {
		@SuppressWarnings("unchecked")
		// We know this cast is good, java just can't tell
		G result = (G) left.empty();
		result.overlay(left);
		result.connect(right);
		return result;
	}
}