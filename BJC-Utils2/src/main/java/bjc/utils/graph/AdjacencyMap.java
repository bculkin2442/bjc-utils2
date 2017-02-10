package bjc.utils.graph;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Scanner;

import bjc.utils.data.IHolder;
import bjc.utils.data.Identity;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.IList;
import bjc.utils.funcdata.IMap;
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
		AdjacencyMap<Integer> adjacency;

		try (Scanner input = new Scanner(stream)) {
			input.useDelimiter("\n");

			int vertexCount;

			String possible = input.next();

			try {
				// First, read in number of vertices
				vertexCount = Integer.parseInt(possible);
			} catch (NumberFormatException nfex) {
				InputMismatchException imex = new InputMismatchException(
						"The first line must contain the number of vertices. "
								+ possible
								+ " is not a valid number");

				imex.initCause(nfex);

				throw imex;
			}

			if (vertexCount <= 0) {
				throw new InputMismatchException(
						"The number of vertices must be greater than 0");
			}

			IList<Integer> vertices = new FunctionalList<>();

			FuncUtils.doTimes(vertexCount, (vertexNo) -> vertices.add(vertexNo));

			adjacency = new AdjacencyMap<>(vertices);

			IHolder<Integer> row = new Identity<>(0);

			input.forEachRemaining((strang) -> {
				readRow(adjacency, vertexCount, row, strang);
			});
		}

		return adjacency;
	}

	private static void readRow(AdjacencyMap<Integer> adjacency,
			int vertexCount, IHolder<Integer> row, String strang) {
		String[] parts = strang.split(" ");

		if (parts.length != vertexCount) {
			throw new InputMismatchException(
					"Must specify a weight for all " + vertexCount
							+ " vertices");
		}

		int column = 0;

		for (String part : parts) {
			int weight;

			try {
				weight = Integer.parseInt(part);
			} catch (NumberFormatException nfex) {
				InputMismatchException imex = new InputMismatchException(
						"" + part + " is not a valid weight.");

				imex.initCause(nfex);

				throw imex;
			}

			adjacency.setWeight(row.getValue(), column, weight);

			column++;
		}

		row.transform((rowNumber) -> rowNumber + 1);
	}

	/**
	 * The backing storage of the map
	 */
	private IMap<T, IMap<T, Integer>> adjacency = new FunctionalMap<>();

	/**
	 * Create a new map from a set of vertices
	 * 
	 * @param vertices
	 *            The set of vertices to create a map from
	 */
	public AdjacencyMap(IList<T> vertices) {
		if (vertices == null) {
			throw new NullPointerException("Vertices must not be null");
		}

		vertices.forEach(vertex -> {
			IMap<T, Integer> row = new FunctionalMap<>();

			vertices.forEach(target -> {
				row.put(target, 0);
			});

			adjacency.put(vertex, row);
		});
	}

	/**
	 * Check if the graph is directed
	 * 
	 * @return Whether or not the graph is directed
	 */
	public boolean isDirected() {
		IHolder<Boolean> result = new Identity<>(true);

		adjacency.forEach((sourceKey, sourceValue) -> {
			sourceValue.forEach((targetKey, targetValue) -> {
				int inverseValue = adjacency.get(targetKey).get(sourceKey);

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
	 * @param source
	 *            The source node of the edge
	 * @param target
	 *            The target node of the edge
	 * @param weight
	 *            The weight of the edge
	 */
	public void setWeight(T source, T target, int weight) {
		if (source == null) {
			throw new NullPointerException(
					"Source vertex must not be null");
		} else if (target== null) {
			throw new NullPointerException(
					"Target vertex must not be null");
		}

		if (!adjacency.containsKey(source)) {
			throw new IllegalArgumentException("Source vertex "
					+ source + " isn't present in map");
		} else if (!adjacency.containsKey(target)) {
			throw new IllegalArgumentException("Target vertex "
					+ target+ " isn't present in map");
		}

		adjacency.get(source).put(target, weight);
	}

	/**
	 * Convert this to a different graph representation
	 * 
	 * @return The new representation of this graph
	 */
	public Graph<T> toGraph() {
		Graph<T> ret = new Graph<>();

		adjacency.forEach((sourceKey, sourceValue) -> {
			sourceValue.forEach((targetKey, targetValue) -> {
				ret.addEdge(sourceKey, targetKey, targetValue, true);
			});
		});

		return ret;
	}

	/**
	 * Convert an adjacency map back into a stream
	 * 
	 * @param sink
	 *            The stream to convert to
	 */
	public void toStream(OutputStream sink) {
		if (sink == null) {
			throw new NullPointerException(
					"Output source must not be null");
		}

		PrintStream outputPrinter = new PrintStream(sink);

		adjacency.forEach((sourceKey, sourceValue) -> {
			sourceValue.forEach((targetKey, targetValue) -> {
				outputPrinter.printf("%d", targetValue);
			});

			outputPrinter.println();
		});

		outputPrinter.close();
	}
}
