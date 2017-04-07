package bjc.utils.funcdata;

import bjc.utils.data.IPair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Basic implementation of {@link IMap}
 *
 * @author ben
 *
 * @param <KeyType>
 *                The type of the map's keys
 * @param <ValueType>
 *                The type of the map's values
 */
public class FunctionalMap<KeyType, ValueType> implements IMap<KeyType, ValueType> {
	private Map<KeyType, ValueType> wrappedMap;

	/**
	 * Create a new blank functional map
	 */
	public FunctionalMap() {
		wrappedMap = new HashMap<>();
	}

	/**
	 * Create a new functional map with the specified entries
	 *
	 * @param entries
	 *                The entries to put into the map
	 */
	@SafeVarargs
	public FunctionalMap(IPair<KeyType, ValueType>... entries) {
		this();

		for (IPair<KeyType, ValueType> entry : entries) {
			entry.doWith((key, val) -> {
				wrappedMap.put(key, val);
			});
		}
	}

	/**
	 * Create a new functional map wrapping the specified map
	 *
	 * @param wrap
	 *                The map to wrap
	 */
	public FunctionalMap(Map<KeyType, ValueType> wrap) {
		if (wrap == null)
			throw new NullPointerException("Map to wrap must not be null");

		wrappedMap = wrap;
	}

	@Override
	public void clear() {
		wrappedMap.clear();
	}

	@Override
	public boolean containsKey(KeyType key) {
		return wrappedMap.containsKey(key);
	}

	@Override
	public IMap<KeyType, ValueType> extend() {
		return new ExtendedMap<>(this, new FunctionalMap<>());
	}

	@Override
	public void forEach(BiConsumer<KeyType, ValueType> action) {
		wrappedMap.forEach(action);
	}

	@Override
	public void forEachKey(Consumer<KeyType> action) {
		wrappedMap.keySet().forEach(action);
	}

	@Override
	public void forEachValue(Consumer<ValueType> action) {
		wrappedMap.values().forEach(action);
	}

	@Override
	public ValueType get(KeyType key) {
		if (key == null)
			throw new NullPointerException("Key must not be null");

		if (!wrappedMap.containsKey(key)) {
			String msg = String.format("Key %s is not present in the map", key);

			throw new IllegalArgumentException(msg);
		}

		return wrappedMap.get(key);
	}

	@Override
	public int size() {
		return wrappedMap.size();
	}

	@Override
	public IList<KeyType> keyList() {
		FunctionalList<KeyType> keys = new FunctionalList<>();

		wrappedMap.keySet().forEach(key -> {
			keys.add(key);
		});

		return keys;
	}

	@Override
	public <MappedValue> IMap<KeyType, MappedValue> transform(Function<ValueType, MappedValue> transformer) {
		if (transformer == null)
			throw new NullPointerException("Transformer must not be null");

		return new TransformedValueMap<>(this, transformer);
	}

	@Override
	public ValueType put(KeyType key, ValueType val) {
		if (key == null)
			throw new NullPointerException("Key must not be null");

		return wrappedMap.put(key, val);
	}

	@Override
	public ValueType remove(KeyType key) {
		return wrappedMap.remove(key);
	}

	@Override
	public String toString() {
		return wrappedMap.toString();
	}

	@Override
	public IList<ValueType> valueList() {
		FunctionalList<ValueType> values = new FunctionalList<>();

		wrappedMap.values().forEach(value -> {
			values.add(value);
		});

		return values;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((wrappedMap == null) ? 0 : wrappedMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FunctionalMap))
			return false;

		FunctionalMap<?, ?> other = (FunctionalMap<?, ?>) obj;

		if (wrappedMap == null) {
			if (other.wrappedMap != null)
				return false;
		} else if (!wrappedMap.equals(other.wrappedMap))
			return false;
		return true;
	}
}
