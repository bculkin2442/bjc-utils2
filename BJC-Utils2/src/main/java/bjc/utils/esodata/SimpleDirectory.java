package bjc.utils.esodata;

import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.IMap;

/**
 * Simple implementation of {@link Directory}.
 *
 * Has a split namespace for data and children.
 *
 * @author EVE
 *
 * @param <K>
 *                The key type of the directory.
 * @param <V>
 *                The value type of the directory.
 */
public class SimpleDirectory<K, V> implements Directory<K, V> {
	private IMap<K, Directory<K, V>> children;

	private IMap<K, V> data;

	/**
	 * Create a new directory.
	 */
	public SimpleDirectory() {
		children = new FunctionalMap<>();
		data = new FunctionalMap<>();
	}

	@Override
	public Directory<K, V> getSubdirectory(K key) {
		return children.get(key);
	}

	@Override
	public boolean hasSubdirectory(K key) {
		return children.containsKey(key);
	}

	@Override
	public Directory<K, V> putSubdirectory(K key, Directory<K, V> val) {
		return children.put(key, val);
	}

	@Override
	public boolean containsKey(K key) {
		return data.containsKey(key);
	}

	@Override
	public V getKey(K key) {
		return data.get(key);
	}

	@Override
	public V putKey(K key, V val) {
		return data.put(key, val);
	}

	@Override
	public int hashCode() {
		final int prime = 31;

		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SimpleDirectory<?, ?>))
			return false;

		SimpleDirectory<?, ?> other = (SimpleDirectory<?, ?>) obj;

		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;

		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return String.format("SimpleDirectory [children=%s, data=%s]", children, data);
	}
}