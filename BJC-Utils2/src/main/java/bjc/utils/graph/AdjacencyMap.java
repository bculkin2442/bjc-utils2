package bjc.utils.graph;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.stream.IntStream;

import bjc.utils.data.IHolder;
import bjc.utils.data.Identity;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.IFunctionalList;
import bjc.utils.funcdata.IFunctionalMap;
import bjc.utils.funcutils.FuncUtils;

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
		if (stream == null) {
			throw new NullPointerException(
					"Input source must not be null");
		}

		// Create the adjacency map
		AdjacencyMap<Integer> adjacencyMap;

		try (Scanner inputSource = new Scanner(stream)) {
			inputSource.useDelimiter("\n");

			int numVertices;

			String possibleVertices = inputSource.next();

			try {
				// First, read in number of vertices
				numVertices = Integer.parseInt(possibleVertices);
			} catch (NumberFormatException nfex) {
				InputMismatchException imex = new InputMismatchException(
						"The first line must contain the number of vertices. "
								+ possibleVertices
								+ " is not a valid number");

				imex.initCause(nfex);

				throw imex;
			}

			if (numVertices <= 0) {
				throw new InputMismatchException(
						"The number of vertices must be greater than 0");
			}

			IFunctionalList<Integer> vertices = new FunctionalList<>();

			FuncUtils.doTimes(numVertices,
					(vertexNo) -> vertices.add(vertexNo));

			adjacencyMap = new AdjacencyMap<>(vertices);

			IHolder<Integer> row = new Identity<>(0);

			inputSource.forEachRemaining((strang) -> {
				readRow(adjacencyMap, numVertices, row, strang);
			});
		}

		return adjacencyMap;
	}

	private static void readRow(AdjacencyMap<Integer> adjacencyMap,
			int numVertices, IHolder<Integer> row, String strang) {
		String[] parts = strang.split(" ");

		if (parts.length != numVertices) {
			throw new InputMismatchException(
					"Must specify a weight for all " + numVertices
							+ " vertices");
		}

		int column = 0;

		for (String part : parts) {
			int columnWeight;

			try {
				columnWeight = Integer.parseInt(part);
			} catch (NumberFormatException nfex) {
				InputMismatchException imex = new InputMismatchException(
						"" + part + " is not a valid weight.");

				imex.initCause(nfex);

				throw imex;
			}

			adjacencyMap.setWeight(row.getValue(), column,
					columnWeight);

			column++;
		}

		row.transform((rowNumber) -> rowNumber + 1);
	}

	/**
	 * The backing storage of the map
	 */
	private IFunctionalMap<T, IFunctionalMap<T, Integer>> adjacencyMap =
			new FunctionalMap<>();

	/**
	 * Create a new map from a set of vertices
	 * 
	 * @param vertices
	 *            The set of vertices to create a map from
	 */
	public AdjacencyMap(IFunctionalList<T> vertices) {
		if (vertices == null) {
			throw new NullPointerException("Vertices must not be null");
		}

		vertices.forEach(vertex -> {
			IFunctionalMap<T, Integer> vertexRow = new FunctionalMap<>();

			vertices.forEach(targetVertex -> {
				vertexRow.put(targetVertex, 0);
			});

			adjacencyMap.put(vertex, vertexRow);
		});
	}

	/**
	 * Check if the graph is directed
	 * 
	 * @return Whether or not the graph is directed
	 */
	public boolean isDirected() {
		IHolder<Boolean> result = new Identity<>(true);

		adjacencyMap.forEach((key, value) -> {
			value.forEach((targetKey, targetValue) -> {
				int inverseValue = adjacencyMap.get(targetKey).get(key);

				if (targetValue != inverseValue) {
					result.replace(false);
				}
			});
		});

		return result.getValue();
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
		if (sourceVertex == null) {
			throw new NullPointerException(
					"Source vertex must not be null");
		} else if (targetVertex == null) {
			throw new NullPointerException(
					"Target vertex must not be null");
		}

		if (!adjacencyMap.containsKey(sourceVertex)) {
			throw new IllegalArgumentException("Source vertex "
					+ sourceVertex + " isn't present in map");
		} else if (!adjacencyMap.containsKey(targetVertex)) {
			throw new IllegalArgumentException("Target vertex "
					+ targetVertex + " isn't present in map");
		}

		adjacencyMap.get(sourceVertex).put(targetVertex, edgeWeight);
	}

	/**
	 * Convert this to a different graph representation
	 * 
	 * @return The new representation of this graph
	 */
	public Graph<T> toGraph() {
		Graph<T> returnedGraph = new Graph<>();

		adjacencyMap.forEach((key, value) -> {
			value.forEach((targetKey, targetValue) -> {
				returnedGraph.addEdge(key, targetKey, targetValue, true);
			});
		});

		return returnedGraph;
	}

	/**
	 * Convert an adjacency map back into a stream
	 * 
	 * @param outputSink
	 *            The stream to convert to
	 */
	public void toStream(OutputStream outputSink) {
		if (outputSink == null) {
			throw new NullPointerException(
					"Output source must not be null");
		}

		PrintStream outputPrinter = new PrintStream(outputSink);

		adjacencyMap.forEach((key, value) -> {
			value.forEach((targetKey, targetValue) -> {
				outputPrinter.printf("%d", targetValue);
			});

			outputPrinter.println();
		});

		outputPrinter.close();
	}
}
