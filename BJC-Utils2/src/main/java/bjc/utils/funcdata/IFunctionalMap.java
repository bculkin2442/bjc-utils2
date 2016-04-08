package bjc.utils.funcdata;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Functional wrapper over map providing some useful things
 * 
 * @author ben
 * 
 * @param <K>
 *            The type of this map's keys
 * @param <V>
 *            The type of this map's values
 *
 */
public interface IFunctionalMap<K, V> {

	/**
	 * Add an entry to the map
	 * 
	 * @param key
	 *            The key to put the value under
	 * @param val
	 *            The value to add
	 * @return The previous value of the key in the map, or null if the key
	 *         wasn't in the map. However, note that it may also return
	 *         null if the key was set to null.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the map implementation doesn't support modifying the
	 *             map
	 */
	V put(K key, V val);

	/**
	 * Get the value assigned to the given key
	 * 
	 * @param key
	 *            The key to look for a value under
	 * @return The value of the key
	 * 
	 * 
	 */
	V get(K key);

	/**
	 * Transform the values returned by this map.
	 * 
	 * NOTE: This transform is applied once for each lookup of a value, so
	 * the transform passed should be a proper function, or things will
	 * likely not work as expected.
	 * 
	 * @param <V2>
	 *            The new type of returned values
	 * @param transformer
	 *            The function to use to transform values
	 * @return The map where each value will be transformed after lookup
	 */
	<V2> IFunctionalMap<K, V2> mapValues(Function<V, V2> transformer);

	/**
	 * Check if this map contains the specified key
	 * 
	 * @param key
	 *            The key to check
	 * @return Whether or not the map contains the key
	 */
	boolean containsKey(K key);

	/**
	 * Get a list of all the keys in this map
	 * 
	 * @return A list of all the keys in this map
	 */
	IFunctionalList<K> keyList();

	/**
	 * Execute an action for each entry in the map
	 * 
	 * @param action
	 *            the action to execute for each entry in the map
	 */
	void forEach(BiConsumer<K, V> action);

	/**
	 * Remove the value bound to the key
	 * 
	 * @param key
	 *            The key to remove from the map
	 * @return The previous value for the key in the map, or null if the
	 *         key wasn't in the class. NOTE: Just because you recieved
	 *         null, doesn't mean the map wasn't changed. It may mean that
	 *         someone put a null value for that key into the map
	 */
	V remove(K key);

	/**
	 * Get the number of entries in this map
	 * 
	 * @return The number of entries in this map
	 */
	int getSize();
}