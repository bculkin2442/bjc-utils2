package bjc.utils.graph;

public class Edge<T> {
	private final T		source, target;
	private final int	distance;

	public Edge(T node1, T node2, int distance) {
		this.source = node1;
		this.target = node2;
		this.distance = distance;
	}

	public T getSource() {
		return source;
	}

	public T getTarget() {
		return target;
	}

	public int getDistance() {
		return distance;
	}

	@Override
	public String toString() {
		return " first vertex " + source + " to vertex " + target
				+ " with distance: " + distance;
	}
}
