package bjc.utils.graph;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.IntStream;

import bjc.utils.data.GenHolder;

/**
 * An adjacency map representing a graph
 * 
 * @author ben
 *
 * @param <T>
 *            The type of the nodes in the graph
 */
public class AdjacencyMap<T> {
	/**
	 * Create an adjacency map from a stream of text
	 * 
	 * @param stream
	 *            The stream of text to read in
	 * @return An adjacency map defined by the text
	 */
	public static AdjacencyMap<Integer> fromStream(InputStream stream) {
		Scanner scn = new Scanner(stream);
		scn.useDelimiter("\n");

		// First, read in number of vertices
		int numVertices = Integer.parseInt(scn.next());

		Set<Integer> vertices = new HashSet<>();
		IntStream.range(0, numVertices).forEach(e -> vertices.add(e));

		// Create the adjacency map
		AdjacencyMap<Integer> aMap = new AdjacencyMap<>(vertices);

		GenHolder<Integer> row = new GenHolder<>(0);

		scn.forEachRemaining((strang) -> {
			String[] parts = strang.split(" ");
			int col = 0;

			for (String part : parts) {
				aMap.setWeight(row.held, col, Integer.parseInt(part));

				col++;
			}

			row.held++;
		});

		scn.close();

		return aMap;
	}

	/**
	 * The backing storage of the map
	 */
	private Map<T, Map<T, Integer>> adjMap;

	/**
	 * Create a new map from a set of vertices
	 * 
	 * @param vertices
	 *            The set of vertices to create a map from
	 */
	public AdjacencyMap(Set<T> vertices) {
		vertices.forEach(src -> {
			Map<T, Integer> srcRow = new HashMap<>();

			vertices.forEach(tgt -> srcRow.put(tgt, 0));

			adjMap.put(src, srcRow);
		});
	}

	/**
	 * Check if the graph is directed
	 * 
	 * @return Whether or not the graph is directed
	 */
	public boolean isDirected() {
		GenHolder<Boolean> res = new GenHolder<>(true);

		adjMap.entrySet()
				.forEach(src -> src.getValue().entrySet().forEach(tgt -> {
					int lhs = tgt.getValue();
					int rhs = adjMap.get(tgt.getKey()).get(src.getKey());

					if (lhs != rhs) {
						res.held = false;
					}
				}));

		return res.held;
	}

	/**
	 * Set the weight of an edge
	 * 
	 * @param src
	 *            The source node of the edge
	 * @param tgt
	 *            The target node of the edge
	 * @param weight
	 *            The weight of the edge
	 */
	public void setWeight(T src, T tgt, int weight) {
		adjMap.get(src).put(tgt, weight);
	}

	/**
	 * Convert this to a different graph representation
	 * 
	 * @return The new representation of this graph
	 */
	public Graph<T> toGraph() {
		Graph<T> ret = new Graph<>();

		adjMap.entrySet()
				.forEach(src -> src.getValue().entrySet()
						.forEach(tgt -> ret.addEdge(src.getKey(),
								tgt.getKey(), tgt.getValue())));

		return ret;
	}

	/**
	 * Convert an adjacency map back into a stream
	 * 
	 * @param os
	 *            The stream to convert to
	 */
	public void toStream(OutputStream os) {
		PrintStream ps = new PrintStream(os);

		adjMap.entrySet().forEach(src -> {
			src.getValue().entrySet()
					.forEach(tgt -> ps.printf("%d ", tgt.getValue()));
			ps.println();
		});
		ps.close();
	}
}
