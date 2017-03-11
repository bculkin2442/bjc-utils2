package bjc.utils.esodata;

import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.IMap;

/**
 * Represents a hierarchical map.
 *
 * What's useful about this is that you can hand sub-directories to people and
 * be able to ensure that they can't write outside of it.
 *
 * @param K The key type of the map.
 * @param V The value type of the map.
 */
public class Directory<K, V> {
	private IMap<K, Directory<K, V>> children;

	private IMap<K, V> data;

	/**
	 * Create a new directory.
	 */
	public Directory() {
		children = new FunctionalMap<>();
		data     = new FunctionalMap<>();
	}

	/**
	 * Create a new sub-directory.
	 *
	 * Will fail if a sub-directory of that name already exists.
	 *
	 * @param key The name of the new sub-directory.
	 *
	 * @return The new sub-directory, or null if one by that name already
	 * exists.
	 */
	public Directory<K, V> newSubdirectory(K key) {
		if(children.containsKey(key)) return null;

		Directory<K, V> kid = new Directory<>();
		children.put(key, kid);
		return kid;
	}

	/**
	 * Check if a given sub-directory exists.
	 *
	 * @param key The key to look for the sub-directory under.
	 *
	 * @return Whether or not a sub-directory of that name exists.
	 */
	public boolean hasSubdirectory(K key) {
		return children.containsKey(key);
	}

	/**
	 * Retrieves a given sub-directory.
	 *
	 * @param key The key to retrieve the sub-directory for.
	 *
	 * @return The sub-directory under that name.
	 *
	 * @throws IllegalArgumentException If the given sub-directory doesn't
	 * exist.
	 */
	public Directory<K, V> getSubdirectory(K key) {
		return children.get(key);
	}

	/**
	 * Insert a data-item into the directory.
	 *
	 * @param key The key to insert into.
	 * @param val The value to insert.
	 *
	 * @return The old value of key, or null if such a value didn't exist.
	 */
	public V put(K key, V val) {
		return data.put(key, val);
	}

	/**
	 * Check if the directory contains a data-item under the given key.
	 *
	 * @param key The key to check for.
	 *
	 * @return Whether or not there is a data item for the given key.
	 */
	public boolean containsKey(K key) {
		return data.containsKey(key);
	}

	/**
	 * Retrive a given data-item from the directory.
	 *
	 * @param key The key to retrieve data for.
	 *
	 * @return The value for the given key.
	 *
	 * @throws IllegalArgumentException If no value exists for the given
	 * key.
	 */
	public V get(K key) {
		return data.get(key);
	}
}
