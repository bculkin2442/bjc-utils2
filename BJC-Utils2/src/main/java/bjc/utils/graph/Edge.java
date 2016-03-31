package bjc.utils.graph;

/**
 * An edge in a weighted graph
 * 
 * @author ben
 *
 * @param <T>
 *            The type of the nodes in the graph
 */
public class Edge<T> {
	/**
	 * The distance from initial to terminal node
	 */
	private final int	distance;

	/**
	 * The initial and terminal nodes of this edge
	 */
	private final T		source, target;

	/**
	 * Create a new edge with set parameters
	 * 
	 * @param initialNode
	 *            The initial node of the edge
	 * @param terminalNode
	 *            The terminal node of the edge
	 * @param distance
	 *            The distance between initial and terminal edge
	 */
	public Edge(T initialNode, T terminalNode, int distance) {
		this.source = initialNode;
		this.target = terminalNode;
		this.distance = distance;
	}

	/**
	 * Get the distance in this edge
	 * 
	 * @return The distance between the initial and terminal nodes of this
	 *         edge
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * Get the initial node of an edge
	 * 
	 * @return The initial node of this edge
	 */
	public T getSource() {
		return source;
	}

	/**
	 * Get the target node of an edge
	 * 
	 * @return The target node of this edge
	 */
	public T getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return " first vertex " + source + " to vertex " + target
				+ " with distance: " + distance;
	}
}
