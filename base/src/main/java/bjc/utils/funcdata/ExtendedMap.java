package bjc.utils.funcdata;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import bjc.utils.funcutils.ListUtils;

/**
 * An extended version of a map, that stores values into a map, but can look
 * into a different one for others.
 *
 * @author Ben Culkin
 *
 * @param <KeyType>
 *        The type of the keys of the map.
 *
 * @param <ValueType>
 *        The type of the values of the map.
 */
class ExtendedMap<KeyType, ValueType> implements IMap<KeyType, ValueType> {
	/* The map we delegate lookups to. */
	private final IMap<KeyType, ValueType> delegate;
	/* The map we store things in. */
	private final IMap<KeyType, ValueType> store;

	/**
	 * Create a new extended map.
	 *
	 * @param delegate
	 *        The map to lookup things in.
	 * 
	 * @param store
	 *        The map to store things in.
	 */
	public ExtendedMap(final IMap<KeyType, ValueType> delegate, final IMap<KeyType, ValueType> store) {
		this.delegate = delegate;
		this.store = store;
	}

	@Override
	public void clear() {
		store.clear();
	}

	@Override
	public boolean containsKey(final KeyType key) {
		if(store.containsKey(key)) return true;

		return delegate.containsKey(key);
	}

	@Override
	public IMap<KeyType, ValueType> extend() {
		return new ExtendedMap<>(this, new FunctionalMap<>());
	}

	@Override
	public void forEach(final BiConsumer<KeyType, ValueType> action) {
		store.forEach(action);

		delegate.forEach(action);
	}

	@Override
	public void forEachKey(final Consumer<KeyType> action) {
		store.forEachKey(action);

		delegate.forEachKey(action);
	}

	@Override
	public void forEachValue(final Consumer<ValueType> action) {
		store.forEachValue(action);

		delegate.forEachValue(action);
	}

	@Override
	public ValueType get(final KeyType key) {
		if(store.containsKey(key)) return store.get(key);

		return delegate.get(key);
	}

	@Override
	public int size() {
		return store.size() + delegate.size();
	}

	@Override
	public IList<KeyType> keyList() {
		return ListUtils.mergeLists(store.keyList(), delegate.keyList());
	}

	@Override
	public <MappedValue> IMap<KeyType, MappedValue> transform(final Function<ValueType, MappedValue> transformer) {
		return new TransformedValueMap<>(this, transformer);
	}

	@Override
	public ValueType put(final KeyType key, final ValueType val) {
		return store.put(key, val);
	}

	@Override
	public ValueType remove(final KeyType key) {
		if(!store.containsKey(key)) return delegate.remove(key);

		return store.remove(key);
	}

	@Override
	public IList<ValueType> valueList() {
		return ListUtils.mergeLists(store.valueList(), delegate.valueList());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (delegate == null ? 0 : delegate.hashCode());
		result = prime * result + (store == null ? 0 : store.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof ExtendedMap)) return false;

		final ExtendedMap<?, ?> other = (ExtendedMap<?, ?>) obj;

		if(delegate == null) {
			if(other.delegate != null) return false;
		} else if(!delegate.equals(other.delegate)) return false;
		if(store == null) {
			if(other.store != null) return false;
		} else if(!store.equals(other.store)) return false;

		return true;
	}

	@Override
	public String toString() {
		return String.format("ExtendedMap [delegate=%s, store=%s]", delegate, store);
	}
}
