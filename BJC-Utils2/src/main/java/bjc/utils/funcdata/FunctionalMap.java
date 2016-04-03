package bjc.utils.funcdata;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import bjc.utils.data.Pair;

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
public class FunctionalMap<K, V> {
	private final class TransformedMap<V2> extends FunctionalMap<K, V2> {
		private FunctionalMap<K, V>	mapToTransform;
		private Function<V, V2>		transformer;

		public TransformedMap(FunctionalMap<K, V> destMap,
				Function<V, V2> transform) {
			mapToTransform = destMap;
			transformer = transform;
		}

		@Override
		public V2 get(K key) {
			return transformer.apply(mapToTransform.get(key));
		}
	}

	private Map<K, V> wrappedMap;

	/**
	 * Create a new blank functional map
	 */
	public FunctionalMap() {
		wrappedMap = new HashMap<>();
	}

	/**
	 * Create a new functional map wrapping the specified map
	 * 
	 * @param wrap
	 *            The map to wrap
	 */
	public FunctionalMap(Map<K, V> wrap) {
		if (wrap == null) {
			throw new NullPointerException("Map to wrap must not be null");
		}

		wrappedMap = wrap;
	}

	/**
	 * Create a new functional map with the specified entries
	 * 
	 * @param entries
	 *            The entries to put into the map
	 */
	@SafeVarargs
	public FunctionalMap(Pair<K, V>... entries) {
		this();

		for (Pair<K, V> entry : entries) {
			entry.doWith((key, val) -> {
				wrappedMap.put(key, val);
			});
		}
	}

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
	 */
	public V put(K key, V val) {
		if (key == null) {
			throw new NullPointerException("Key must not be null");
		}

		return wrappedMap.put(key, val);
	}

	/**
	 * Get the value assigned to the given key
	 * 
	 * @param key
	 *            The key to look for a value under
	 * @return The value of the key
	 * 
	 * 
	 */
	public V get(K key) {
		if (key == null) {
			throw new NullPointerException("Key must not be null");
		}

		if (wrappedMap.containsKey(wrappedMap)) {
			return wrappedMap.get(key);
		} else {
			throw new IllegalArgumentException(
					"Key " + key + " is not present in the map");
		}
	}

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
	public <V2> FunctionalMap<K, V2>
			mapValues(Function<V, V2> transformer) {
		if (transformer == null) {
			throw new NullPointerException("Transformer must not be null");
		}

		return new TransformedMap<>(this, transformer);
	}

	/**
	 * Check if this map contains the specified key
	 * 
	 * @param key
	 *            The key to check
	 * @return Whether or not the map contains the key
	 */
	public boolean containsKey(K key) {
		return wrappedMap.containsKey(key);
	}
}
