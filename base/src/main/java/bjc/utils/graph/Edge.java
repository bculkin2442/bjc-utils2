package bjc.utils.graph;

import java.util.Objects;

/**
 * An edge in a weighted graph.
 *
 * @author ben
 *
 * @param <T> The type of the nodes in the graph.
 * @param <W> The type of the weight
 */
public class Edge<T, W> {
	/* The distance from initial to terminal node. */
	private final W distance;

	/* The initial and terminal nodes of this edge. */
	private final T source, target;

	/**
	 * Create a new edge with set parameters.
	 *
	 * @param initial  The initial node of the edge.
	 *
	 * @param terminal The terminal node of the edge.
	 *
	 * @param distance The distance between initial and terminal edge.
	 */
	public Edge(final T initial, final T terminal, final W distance) {
		if (initial == null)
			throw new NullPointerException("Initial node must not be null");
		else if (terminal == null)
			throw new NullPointerException("Terminal node must not be null");

		this.source = initial;
		this.target = terminal;
		this.distance = distance;
	}

	/**
	 * Get the distance in this edge.
	 *
	 * @return The distance between the initial and terminal nodes of this edge.
	 */
	public W getDistance() {
		return distance;
	}

	/**
	 * Get the initial node of an edge.
	 *
	 * @return The initial node of this edge.
	 */
	public T getSource() {
		return source;
	}

	/**
	 * Get the target node of an edge.
	 *
	 * @return The target node of this edge.
	 */
	public T getTarget() {
		return target;
	}

	@Override
	public String toString() {
		String msg = String.format("source vertex %s to target vertex %s with distance: %s", source, target, distance);

		return msg;
	}

	@Override
	public int hashCode() {
		return Objects.hash(distance, source, target);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge<?, ?> other = (Edge<?, ?>) obj;
		return Objects.equals(distance, other.distance) && Objects.equals(source, other.source)
				&& Objects.equals(target, other.target);
	}
}
