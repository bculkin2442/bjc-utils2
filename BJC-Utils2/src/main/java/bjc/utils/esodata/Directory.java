package bjc.utils.esodata;

/**
 * Represents a hierarchical map.
 *
 * What's useful about this is that you can hand sub-directories to people and
 * be able to ensure that they can't write outside of it.
 *
 * @param <K>
 *                The key type of the map.
 * @param <V>
 *                The value type of the map.
 */
public interface Directory<K, V> {
	/**
	 * Retrieves a given sub-directory.
	 *
	 * @param key
	 *                The key to retrieve the sub-directory for.
	 *
	 * @return The sub-directory under that name.
	 *
	 * @throws IllegalArgumentException
	 *                 If the given sub-directory doesn't exist.
	 */
	Directory<K, V> getSubdirectory(K key);

	/**
	 * Check if a given sub-directory exists.
	 *
	 * @param key
	 *                The key to look for the sub-directory under.
	 *
	 * @return Whether or not a sub-directory of that name exists.
	 */
	boolean hasSubdirectory(K key);

	/**
	 * Insert a sub-directory into the dictionary.
	 *
	 * @param key
	 *                The name of the new sub-directory
	 * @param value
	 *                The sub-directory to insert
	 *
	 * @return The old sub-directory attached to this key, or null if such a
	 *         sub-directory didn't exist
	 */
	Directory<K, V> putSubdirectory(K key, Directory<K, V> value);

	/**
	 * Create a new sub-directory.
	 *
	 * Will fail if a sub-directory of that name already exists.
	 *
	 * @param key
	 *                The name of the new sub-directory.
	 *
	 * @return The new sub-directory, or null if one by that name already
	 *         exists.
	 */
	default Directory<K, V> newSubdirectory(K key) {
		if(hasSubdirectory(key)) return null;

		Directory<K, V> dir = new SimpleDirectory<>();

		putSubdirectory(key, dir);

		return dir;
	}

	/**
	 * Check if the directory contains a data-item under the given key.
	 *
	 * @param key
	 *                The key to check for.
	 *
	 * @return Whether or not there is a data item for the given key.
	 */
	boolean containsKey(K key);

	/**
	 * Retrieve a given data-item from the directory.
	 *
	 * @param key
	 *                The key to retrieve data for.
	 *
	 * @return The value for the given key.
	 *
	 * @throws IllegalArgumentException
	 *                 If no value exists for the given key.
	 */
	V getKey(K key);

	/**
	 * Insert a data-item into the directory.
	 *
	 * @param key
	 *                The key to insert into.
	 * @param val
	 *                The value to insert.
	 *
	 * @return The old value of key, or null if such a value didn't exist.
	 */
	V putKey(K key, V val);
}