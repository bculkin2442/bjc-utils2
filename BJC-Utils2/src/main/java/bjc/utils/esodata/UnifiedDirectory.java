package bjc.utils.esodata;

import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.IMap;

/**
 * Simple implementation of {@link Directory}.
 *
 * Has a unified namespace for data and children.
 *
 * @author EVE
 *
 * @param <K>
 *                The key type of the directory.
 * @param <V>
 *                The value type of the directory.
 */
public class UnifiedDirectory<K, V> implements Directory<K, V> {
	private IMap<K, Directory<K, V>> children;

	private IMap<K, V> data;

	/**
	 * Create a new directory.
	 */
	public UnifiedDirectory() {
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
		if(data.containsKey(key))
			throw new IllegalArgumentException("Key " + key + " is already used for data.");

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
		if(children.containsKey(key))
			throw new IllegalArgumentException("Key " + key + " is already used for sub-directories.");

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
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;

		UnifiedDirectory<?, ?> other = (UnifiedDirectory<?, ?>) obj;

		if(children == null) {
			if(other.children != null) return false;
		} else if(!children.equals(other.children)) return false;

		if(data == null) {
			if(other.data != null) return false;
		} else if(!data.equals(other.data)) return false;

		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("UnifiedDirectory [children=");
		builder.append(children);
		builder.append(", data=");
		builder.append(data);
		builder.append("]");

		return builder.toString();
	}
}