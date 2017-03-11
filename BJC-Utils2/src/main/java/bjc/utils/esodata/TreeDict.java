package bjc.utils.esodata;

/**
 * Represents a hierarchical map.
 *
 * What's useful about this is that you can hand sub-directories to people and
 * be able to ensure that they can't write outside of it.
 *
 * @param K The key type of the map.
 * @param V The value type of the map.
 */
public class TreeDict<K, V> {
	private Map<K, TreeDict<K, V>> children;

	private Map<K, V> data;

	public T
}
