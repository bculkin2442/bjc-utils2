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
		if (initialNode == null) {
			throw new NullPointerException(
					"Initial node must not be null");
		} else if (terminalNode == null) {
			throw new NullPointerException(
					"Terminal node must not be null");
		}

		this.source = initialNode;
		this.target = terminalNode;
		this.distance = distance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (getClass() != obj.getClass()) {
			return false;
		} else {

			Edge<?> other = (Edge<?>) obj;

			if (distance != other.distance) {
				return false;
			} else if (source == null) {
				if (other.source != null) {
					return false;
				}
			} else if (!source.equals(other.source)) {
				return false;
			} else if (target == null) {
				if (other.target != null) {
					return false;
				}
			} else if (!target.equals(other.target)) {
				return false;
			}

			return true;
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + distance;
		result = prime * result
				+ ((source == null) ? 0 : source.hashCode());
		result = prime * result
				+ ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return " first vertex " + source + " to vertex " + target
				+ " with distance: " + distance;
	}
}
