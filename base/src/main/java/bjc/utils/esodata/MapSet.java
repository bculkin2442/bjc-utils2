package bjc.utils.esodata;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapSet<KeyType, ValueType> extends AbstractMap<KeyType, ValueType> {
	private Map<String, Map<KeyType, ValueType>> backing;

	private Map<KeyType, ValueType> currentMap = null;

	public MapSet() {
		backing = new HashMap<>();
	}

	public MapSet(Map<String, Map<KeyType, ValueType>> back) {
		backing = back;
	}

	public void addMap(String key, Map<KeyType, ValueType> map) {
		backing.put(key, map);
	}

	public void clearMap() {
		currentMap = null;

		backing.clear();
	}

	public boolean containsMap(String key) {
		return backing.containsKey(key);
	}

	public Map<KeyType, ValueType> getMap(String key) {
		return backing.get(key);
	}

	public Set<Map.Entry<String, Map<KeyType, ValueType>>> getMapEntries() {
		return backing.entrySet();
	}

	public Set<String> getMapKeys() {
		return backing.keySet();
	}

	public Collection<Map<KeyType, ValueType>> getMapValues() {
		return backing.values();
	}

	public boolean setMap(String key) {
		if (!backing.containsKey(key)) return false;

		currentMap = backing.get(key);

		return true;
	}

	public void setCreateMap(String key) {
		if (!backing.containsKey(key)) {
			currentMap = new HashMap<>();

			backing.put(key, currentMap);

			return;
		}

		currentMap = backing.get(key);
	}

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
