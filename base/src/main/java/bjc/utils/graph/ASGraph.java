package bjc.utils.graph;

import java.util.*;
import java.util.function.Function;

import bjc.data.Pair;
import bjc.esodata.*;
import bjc.funcdata.Freezable;
import bjc.funcdata.ObjectFrozen;
import bjc.functypes.Unit;

public class ASGraph<Node, Value, Label> implements Freezable<ASGraph<Node, Value, Label>> {
	private Set<Node> nodes;
	private Function<Node, Value> nodeValuer;
	private PairMap<Node, Node, Label> arcMap;

	// Used for implementation efficiency
	private Multimap<Node, Value> nodeToValue;
	private Multimap<Value, Node> valueToNode;

	private boolean frozen;
	private boolean deepFrozen;

	public ASGraph(Function<Node, Value> valuer) {
		this.nodes = new HashSet<>();
		this.nodeValuer = valuer;
		this.arcMap = new PairMap<>();

		this.nodeToValue = new TSetMultimap<>();
		this.valueToNode = new TSetMultimap<>();
	}

	/**
	 * Retrieve a read-only view of the nodes
	 * 
	 * @return A read-only set of the nodes.
	 */
	public Set<Node> getNodes() {
		return Collections.unmodifiableSet(nodes);
	}

	/**
	 * Get the value for a node in the graph.
	 * 
	 * @param nd The node to check.
	 * 
	 * @return The value for that node.
	 */
	public Value getValue(Node nd) {
		return nodeValuer.apply(nd);
	}

	/**
	 * Add a node to the graph.
	 * 
	 * @param nd The node to add.
	 */
	public void addNode(Node nd) {
		if (deepFrozen)
			throw new ObjectFrozen();

		this.nodes.add(nd);

		Value v = nodeValuer.apply(nd);
		this.nodeToValue.add(nd, v);
		this.valueToNode.add(v, nd);
	}

	/**
	 * Remove a node from the graph.
	 * 
	 * @param nd The node to remove
	 */
	public void removeNode(Node nd) {
		if (deepFrozen)
			throw new ObjectFrozen();
		this.nodes.remove(nd);

		Value v = nodeValuer.apply(nd);
		this.nodeToValue.remove(nd);
		this.valueToNode.remove(v, nd);
	}

	/**
	 * Add an arc to the graph.
	 * 
	 * Note that badness can happen if you add a arc that refers to nodes not in the
	 * graph.
	 * 
	 * @param nd1 The source node for the arc.
	 * @param nd2 The destination node for the arc.
	 * @param lab The label for the arc.
	 */
	public void addArc(Node nd1, Node nd2, Label lab) {
		if (deepFrozen)
			throw new ObjectFrozen();
		arcMap.put(Pair.pair(nd1, nd2), lab);
	}

	/**
	 * Remove an arc from the graph.
	 * 
	 * @param nd1 The source node for the arc.
	 * @param nd2 The destination node for the arc.
	 * @param lab The label for the arc.
	 */
	public void removeArc(Node nd1, Node nd2, Label lab) {
		if (deepFrozen)
			throw new ObjectFrozen();
		arcMap.remove(Pair.pair(nd1, nd2), lab);
	}

	/**
	 * Check if this graph is partitioned by the given graphs.
	 * 
	 * <ol>
	 * <li>Every node in nodes is contained in exactly one partition</li>
	 * <li>For every node in nodes, the value the partition assigns it is equal to
	 * the value from nodeValuer</li>
	 * <li>Every arc in a partition is also in arcMap</li>
	 * </ol>
	 * 
	 * @param partitions The graphs to check partitioning for.
	 * 
	 * @return Whether this graph is partitioned by the given nodes.
	 */
	public boolean partitionedBy(@SuppressWarnings("unchecked") ASGraph<Node, Value, Label>... partitions) {
		// TODO: Implement me
		return false;
	}

	/**
	 * Create a graph representing a given string
	 * 
	 * Note that because of the way the value function is implemented, modifying the
	 * graph will not work well.
	 * 
	 * @param strang The string to represent.
	 * 
	 * @return The graph representing the string.
	 */
	public static ASGraph<Integer, Character, Unit> fromString(String strang) {
		ASGraph<Integer, Character, Unit> ret = new ASGraph<>(strang::charAt);

		for (int i = 0; i < strang.length(); i++) {
			ret.addNode(i);
			if (i != 0)
				ret.addArc(i - 1, i, Unit.UNIT);
		}

		return ret;
	}

	@Override
	public boolean freeze() {
		frozen = true;
		return true;
	}

	@Override
	public boolean thaw() {
		if (deepFrozen)
			return false;

		return false;
	}

	@Override
	public boolean deepFreeze() {
		deepFrozen = true;
		frozen = true;
		return true;
	}

	@Override
	public boolean isFrozen() {
		return frozen;
	}
}
