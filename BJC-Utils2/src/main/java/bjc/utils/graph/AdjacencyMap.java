package bjc.utils.graph;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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
		Scanner inputSource = new Scanner(stream);
		inputSource.useDelimiter("\n");

		// First, read in number of vertices
		int numVertices = Integer.parseInt(inputSource.next());

		Set<Integer> vertices = new HashSet<>();
		IntStream.range(0, numVertices)
				.forEach(element -> vertices.add(element));

		// Create the adjacency map
		AdjacencyMap<Integer> adjacencyMap = new AdjacencyMap<>(vertices);

		GenHolder<Integer> row = new GenHolder<>(0);

		inputSource.forEachRemaining((strang) -> {
			String[] parts = strang.split(" ");
			int column = 0;

			for (String part : parts) {
				adjacencyMap.setWeight(row.unwrap(number -> number),
						column, Integer.parseInt(part));

				column++;
			}

			row.transform((number) -> number + 1);
		});

		inputSource.close();

		return adjacencyMap;
	}

	/**
	 * The backing storage of the map
	 */
	private Map<T, Map<T, Integer>> adjacencyMap = new HashMap<>();

	/**
	 * Create a new map from a set of vertices
	 * 
	 * @param vertices
	 *            The set of vertices to create a map from
	 */
	public AdjacencyMap(Set<T> vertices) {
		vertices.forEach(vertex -> {
			Map<T, Integer> vertexRow = new HashMap<>();

			vertices.forEach(
					targetVertex -> vertexRow.put(targetVertex, 0));

			adjacencyMap.put(vertex, vertexRow);
		});
	}

	/**
	 * Check if the graph is directed
	 * 
	 * @return Whether or not the graph is directed
	 */
	public boolean isDirected() {
		GenHolder<Boolean> result = new GenHolder<>(true);

		adjacencyMap.entrySet().forEach(mapEntry -> {
			Set<Entry<T, Integer>> entryVertices =
					mapEntry.getValue().entrySet();
			entryVertices.forEach(targetVertex -> {
				int leftValue = targetVertex.getValue();
				int rightValue = adjacencyMap.get(targetVertex.getKey())
						.get(mapEntry.getKey());

				if (leftValue != rightValue) {
					result.transform((bool) -> false);
				}
			});
		});

		return result.unwrap(bool -> bool);
	}

	/**
	 * Set the weight of an edge
	 * 
	 * @param sourceVertex
	 *            The source node of the edge
	 * @param targetVertex
	 *            The target node of the edge
	 * @param edgeWeight
	 *            The weight of the edge
	 */
	public void setWeight(T sourceVertex, T targetVertex, int edgeWeight) {
		adjacencyMap.get(sourceVertex).put(targetVertex, edgeWeight);
	}

	/**
	 * Convert this to a different graph representation
	 * 
	 * @return The new representation of this graph
	 */
	public Graph<T> toGraph() {
		Graph<T> returnedGraph = new Graph<>();

		adjacencyMap.entrySet().forEach(sourceVertex -> sourceVertex
				.getValue().entrySet()
				.forEach(targetVertex -> returnedGraph.addEdge(
						sourceVertex.getKey(), targetVertex.getKey(),
						targetVertex.getValue())));

		return returnedGraph;
	}

	/**
	 * Convert an adjacency map back into a stream
	 * 
	 * @param outputSource
	 *            The stream to convert to
	 */
	public void toStream(OutputStream outputSource) {
		PrintStream outputPrinter = new PrintStream(outputSource);

		adjacencyMap.entrySet().forEach(sourceVertex -> {
			sourceVertex.getValue().entrySet()
					.forEach(targetVertex -> outputPrinter.printf("%d ",
							targetVertex.getValue()));
			outputPrinter.println();
		});

		outputPrinter.close();
	}
}
