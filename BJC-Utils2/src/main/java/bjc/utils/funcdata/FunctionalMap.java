package bjc.utils.funcdata;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import bjc.utils.data.Pair;

/**
 * Basic implementation of {@link IFunctionalMap}
 * 
 * @author ben
 *
 * @param <K>
 *            The type of the map's keys
 * @param <V>
 *            The type of the map's values
 */
public class FunctionalMap<K, V> implements IFunctionalMap<K, V> {
	/**
	 * A map that transforms values from one type to another
	 * 
	 * @author ben
	 *
	 * @param <K>
	 *            The type of the map's keys
	 * @param <V>
	 *            The type of the map's values
	 * @param <V2>
	 *            The type of the transformed values
	 */
	private static final class TransformedValueMap<K, V, V2>
			implements IFunctionalMap<K, V2> {
		private IFunctionalMap<K, V>	mapToTransform;
		private Function<V, V2>			transformer;

		public TransformedValueMap(IFunctionalMap<K, V> destMap,
				Function<V, V2> transform) {
			mapToTransform = destMap;
			transformer = transform;
		}

		@Override
		public V2 get(K key) {
			return transformer.apply(mapToTransform.get(key));
		}

		@Override
		public boolean containsKey(K key) {
			return mapToTransform.containsKey(key);
		}

		@Override
		public String toString() {
			return mapToTransform.toString();
		}

		@Override
		public V2 put(K key, V2 val) {
			throw new UnsupportedOperationException(
					"Can't add items to transformed map");
		}

		@Override
		public <V3> IFunctionalMap<K, V3> mapValues(
				Function<V2, V3> transform) {
			return new TransformedValueMap<>(this, transform);
		}

		@Override
		public IFunctionalList<K> keyList() {
			return mapToTransform.keyList();
		}

		@Override
		public void forEach(BiConsumer<K, V2> action) {
			mapToTransform.forEach((key, val) -> {
				action.accept(key, transformer.apply(val));
			});
		}

		@Override
		public V2 remove(K key) {
			return transformer.apply(mapToTransform.remove(key));
		}

		@Override
		public int getSize() {
			return mapToTransform.getSize();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.funcdata.IFunctionalMap#put(K, V)
	 */
	@Override
	public V put(K key, V val) {
		if (key == null) {
			throw new NullPointerException("Key must not be null");
		}

		return wrappedMap.put(key, val);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.funcdata.IFunctionalMap#get(K)
	 */
	@Override
	public V get(K key) {
		if (key == null) {
			throw new NullPointerException("Key must not be null");
		}

		if (!wrappedMap.containsKey(key)) {
			throw new IllegalArgumentException(
					"Key " + key + " is not present in the map");
		}

		return wrappedMap.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.funcdata.IFunctionalMap#mapValues(java.util.function.
	 * Function)
	 */
	@Override
	public <V2> IFunctionalMap<K, V2> mapValues(
			Function<V, V2> transformer) {
		if (transformer == null) {
			throw new NullPointerException("Transformer must not be null");
		}

		return new TransformedValueMap<>(this, transformer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bjc.utils.funcdata.IFunctionalMap#containsKey(K)
	 */
	@Override
	public boolean containsKey(K key) {
		return wrappedMap.containsKey(key);
	}

	@Override
	public String toString() {
		return wrappedMap.toString();
	}

	@Override
	public IFunctionalList<K> keyList() {
		FunctionalList<K> keys = new FunctionalList<>();

		wrappedMap.keySet().forEach((key) -> {
			keys.add(key);
		});

		return keys;
	}

	@Override
	public void forEach(BiConsumer<K, V> action) {
		wrappedMap.forEach(action);
	}

	@Override
	public V remove(K key) {
		return wrappedMap.remove(key);
	}

	@Override
	public int getSize() {
		return wrappedMap.size();
	}
}