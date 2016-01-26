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

public class AdjacencyMap<T> {
	private Map<T, Map<T, Integer>> adjMap;

	public AdjacencyMap(Set<T> vertices) {
		vertices.forEach(src -> {
			Map<T, Integer> srcRow = new HashMap<>();

			vertices.forEach(tgt -> srcRow.put(tgt, 0));

			adjMap.put(src, srcRow);
		});
	}

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

	public void setWeight(T src, T tgt, int weight) {
		adjMap.get(src).put(tgt, weight);
	}

	public Graph<T> toGraph() {
		Graph<T> ret = new Graph<>();

		adjMap.entrySet()
				.forEach(src -> src.getValue().entrySet()
						.forEach(tgt -> ret.addEdge(src.getKey(),
								tgt.getKey(), tgt.getValue())));

		return ret;
	}

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
