package bjc.utils.graph;

/**
 * An edge in a weighted graph.
 *
 * @author ben
 *
 * @param <T>
 * 	The type of the nodes in the graph.
 */
public class Edge<T> {
	/* The distance from initial to terminal node.  */
	private final int distance;

	/* The initial and terminal nodes of this edge. */
	private final T source, target;

	/**
	 * Create a new edge with set parameters.
	 *
	 * @param initial
	 * 	The initial node of the edge.
	 *
	 * @param terminal
	 * 	The terminal node of the edge.
	 *
	 * @param distance
	 * 	The distance between initial and terminal edge.
	 */
	public Edge(final T initial, final T terminal, final int distance) {
		if (initial == null) {
			throw new NullPointerException("Initial node must not be null");
		} else if (terminal == null) {
			throw new NullPointerException("Terminal node must not be null");
		}

		this.source = initial;
		this.target = terminal;
		this.distance = distance;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		else if (obj == null)
			return false;
		else if (getClass() != obj.getClass())
			return false;
		else {
			final Edge<?> other = (Edge<?>) obj;

			if (distance != other.distance)
				return false;
			else if (source == null) {
				if (other.source != null) return false;
			} else if (!source.equals(other.source))
				return false;
			else if (target == null) {
				if (other.target != null) return false;
			} else if (!target.equals(other.target)) return false;

			return true;
		}
	}

	/**
	 * Get the distance in this edge.
	 *
	 * @return 
	 * 	The distance between the initial and terminal nodes of this
	 * 	edge.
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * Get the initial node of an edge.
	 *
	 * @return
	 * 	The initial node of this edge.
	 */
	public T getSource() {
		return source;
	}

	/**
	 * Get the target node of an edge.
	 *
	 * @return
	 * 	The target node of this edge.
	 */
	public T getTarget() {
		return target;
	}

	@Override
	public int hashCode() {
		final int prime = 31;

		int result = 1;

		result = prime * result + distance;
		result = prime * result + (source == null ? 0 : source.hashCode());
		result = prime * result + (target == null ? 0 : target.hashCode());

		return result;
	}

	@Override
	public String toString() {
		String msg = String.format("source vertex %s to target vertex %s with distance: %s",
				source, target, distance);

		return msg;
	}
}
