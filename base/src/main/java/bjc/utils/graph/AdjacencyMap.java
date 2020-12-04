package bjc.utils.graph;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Scanner;

import bjc.data.Holder;
import bjc.data.Identity;
import bjc.funcdata.FunctionalList;
import bjc.funcdata.FunctionalMap;
import bjc.funcdata.ListEx;
import bjc.funcdata.MapEx;
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
	 *               The stream of text to read in
	 *
	 * @return An adjacency map defined by the text
	 */
	public static AdjacencyMap<Integer> fromStream(final InputStream stream) {
		if (stream == null) throw new NullPointerException("Input source must not be null");

		/* Create the adjacency map. */
		AdjacencyMap<Integer> adjacency;

		try (Scanner input = new Scanner(stream)) {
			input.useDelimiter("\n");

			int vertexCount;

			final String possible = input.next();

			try {
				/* First, read in number of vertices. */
				vertexCount = Integer.parseInt(possible);
			} catch (final NumberFormatException nfex) {
				String msg = String.format(
						"The first line must contain the number of vertices. %s is not a valid number",
						possible);

				final InputMismatchException imex = new InputMismatchException(msg);

				imex.initCause(nfex);

				throw imex;
			}

			if (vertexCount <= 0) throw new InputMismatchException(
						"The number of vertices must be greater than 0");

			final ListEx<Integer> vertices = new FunctionalList<>();

			FuncUtils.doTimes(vertexCount, vertexNo -> vertices.add(vertexNo));

			adjacency = new AdjacencyMap<>(vertices);

			final Holder<Integer> row = new Identity<>(0);

			input.forEachRemaining(strang -> {
				readRow(adjacency, vertexCount, row, strang);
			});
		}

		return adjacency;
	}

	/* Read a row of edges. */
	private static void readRow(final AdjacencyMap<Integer> adjacency,
			final int vertexCount, final Holder<Integer> row, final String strang) {
		final String[] parts = strang.split(" ");

		if (parts.length != vertexCount) {
			String msg = String.format("Must specify a weight for all %d vertices",
					vertexCount);

			throw new InputMismatchException(msg);
		}

		int column = 0;

		for (final String part : parts) {
			int weight;

			try {
				weight = Integer.parseInt(part);
			} catch (final NumberFormatException nfex) {
				String msg = String.format("%s is not a valid weight.", part);

				final InputMismatchException imex = new InputMismatchException(msg);
				imex.initCause(nfex);

				throw imex;
			}

			adjacency.setWeight(row.getValue(), column, weight);

			column++;
		}

		row.transform(rowNumber -> rowNumber + 1);
	}

	/** The backing storage of the map */
	private final MapEx<T, MapEx<T, Integer>> adjacency = new FunctionalMap<>();

	/**
	 * Create a new map from a set of vertices
	 *
	 * @param vertices
	 *                 The set of vertices to create a map from
	 */
	public AdjacencyMap(final ListEx<T> vertices) {
		if (vertices == null) throw new NullPointerException("Vertices must not be null");

		vertices.forEach(vertex -> {
			final MapEx<T, Integer> row = new FunctionalMap<>();

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
		final Holder<Boolean> result = new Identity<>(true);

		adjacency.forEach((sourceKey, sourceValue) -> {
			sourceValue.forEach((targetKey, targetValue) -> {
				final int inverseValue = adjacency.get(targetKey).get().get(sourceKey).get();

				if (targetValue != inverseValue) result.replace(false);
			});
		});

		return result.getValue();
	}

	/**
	 * Set the weight of an edge.
	 *
	 * @param source
	 *               The source node of the edge.
	 * @param target
	 *               The target node of the edge.
	 * @param weight
	 *               The weight of the edge.
	 */
	public void setWeight(final T source, final T target, final int weight) {
		if (source == null)      throw new NullPointerException("Source vertex must not be null");
		else if (target == null) throw new NullPointerException("Target vertex must not be null");

		if (!adjacency.containsKey(source)) {
			String msg = String.format("Source vertex %s isn't present in map", source);

			throw new IllegalArgumentException(msg);
		} else if (!adjacency.containsKey(target)) {
			String msg = String.format("Target vertex %s isn't present in map", target);

			throw new IllegalArgumentException(msg);
		}

		adjacency.get(source).get().put(target, weight);
	}

	/**
	 * Convert this to a different graph representation.
	 *
	 * @return The new representation of this graph.
	 */
	public Graph<T> toGraph() {
		final Graph<T> ret = new Graph<>();

		adjacency.forEach((sourceKey, sourceValue) -> {
			sourceValue.forEach((targetKey, targetValue) -> {
				ret.addEdge(sourceKey, targetKey, targetValue, true);
			});
		});

		return ret;
	}

	/**
	 * Convert an adjacency map back into a stream.
	 *
	 * @param sink
	 *             The stream to convert to.
	 */
	public void toStream(final OutputStream sink) {
		if (sink == null) throw new NullPointerException("Output source must not be null");

		final PrintStream outputPrinter = new PrintStream(sink);

		adjacency.forEach((sourceKey, sourceValue) -> {
			sourceValue.forEach((targetKey, targetValue) -> {
				outputPrinter.printf("%d", targetValue);
			});

			outputPrinter.println();
		});

		outputPrinter.close();
	}
}
