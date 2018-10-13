package bjc.utils.esodata;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A string-keyed set of maps.
 * 
 * @author bjculkin
 *
 * @param <KeyType>
 *                The key type of the maps.
 * @param <ValueType>
 *                The value type of the maps.
 */
public class MapSet<KeyType, ValueType> extends AbstractMap<KeyType, ValueType> {
	private Map<String, Map<KeyType, ValueType>> backing;

	private Map<KeyType, ValueType> currentMap = null;

	/**
	 * Create a new set of maps.
	 */
	public MapSet() {
		backing = new HashMap<>();
	}

	/**
	 * Create a new set of maps, with the specified set of maps.
	 * 
	 * @param back
	 *                The set of maps to use.
	 */
	public MapSet(Map<String, Map<KeyType, ValueType>> back) {
		backing = back;
	}

	/**
	 * Add a keyed map.
	 * 
	 * @param key
	 *                The key for the map.
	 * @param map
	 *                The map itself.
	 */
	public void addMap(String key, Map<KeyType, ValueType> map) {
		backing.put(key, map);
	}

	/**
	 * Clear out the contents of the set
	 */
	public void clearMap() {
		currentMap = null;

		backing.clear();
	}

	/**
	 * Check if there is a map attached to the specified key.
	 * 
	 * @param key
	 *                The key to look for.
	 * @return Whether or not there is anything attached to the key.
	 */
	public boolean containsMap(String key) {
		return backing.containsKey(key);
	}

	/**
	 * Get the map attached to a specified key.
	 * 
	 * @param key
	 *                The key to look for.
	 * @return The map attached to the key.
	 */
	public Map<KeyType, ValueType> getMap(String key) {
		return backing.get(key);
	}

	/**
	 * Get all of the backing entries.
	 * 
	 * @return The backing entries.
	 */
	public Set<Map.Entry<String, Map<KeyType, ValueType>>> getMapEntries() {
		return backing.entrySet();
	}

	/**
	 * Get all of the keys.
	 * 
	 * @return The keys currently in use.
	 */
	public Set<String> getMapKeys() {
		return backing.keySet();
	}

	/**
	 * Get all of the keyed maps.
	 * 
	 * @return The keyed maps.
	 */
	public Collection<Map<KeyType, ValueType>> getMapValues() {
		return backing.values();
	}

	/**
	 * Set the current map.
	 * 
	 * @param key
	 *                The key to use as the current map.
	 * @return False if there is no map attached to the key, true otherwise.
	 */
	public boolean setMap(String key) {
		if (!backing.containsKey(key)) return false;

		currentMap = backing.get(key);

		return true;
	}

	/**
	 * Sets the current map, or creates a new one if there isn't one
	 * attached to that key.
	 * 
	 * @param key
	 *                The key to use as the current map.
	 */
	public void setCreateMap(String key) {
		if (!backing.containsKey(key)) {
			currentMap = new HashMap<>();

			backing.put(key, currentMap);

			return;
		}

		currentMap = backing.get(key);
	}

	/**
	 * Set the current map, or bind a map to it.
	 * 
	 * @param key
	 *                The key to set or bind.
	 * @param map
	 *                The map to bind to the key if it isn't present.
	 */
	public void setPutMap(String key, Map<KeyType, ValueType> map) {
		if (!backing.containsKey(key)) {
			currentMap = map;

			backing.put(key, map);

			return;
		}

		currentMap = backing.get(key);
	}

	@Override
	public Set<Map.Entry<KeyType, ValueType>> entrySet() {
		if (currentMap == null) throw new NullPointerException("Current map is not set");

		return currentMap.entrySet();
	}

	@Override
	public ValueType put(KeyType key, ValueType value) {
		if (currentMap == null) throw new NullPointerException("Current map is not set");

		return currentMap.put(key, value);
	}
}
