package bjc.utils.funcdata;

import bjc.utils.funcutils.ListUtils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

class ExtendedMap<KeyType, ValueType> implements IMap<KeyType, ValueType> {
	private IMap<KeyType, ValueType> delegate;

	private IMap<KeyType, ValueType> store;

	public ExtendedMap(IMap<KeyType, ValueType> delegate, IMap<KeyType, ValueType> store) {
		this.delegate = delegate;
		this.store = store;
	}

	@Override
	public void clear() {
		store.clear();
	}

	@Override
	public boolean containsKey(KeyType key) {
		if(store.containsKey(key)) return true;

		return delegate.containsKey(key);
	}

	@Override
	public IMap<KeyType, ValueType> extend() {
		return new ExtendedMap<>(this, new FunctionalMap<>());
	}

	@Override
	public void forEach(BiConsumer<KeyType, ValueType> action) {
		store.forEach(action);

		delegate.forEach(action);
	}

	@Override
	public void forEachKey(Consumer<KeyType> action) {
		store.forEachKey(action);

		delegate.forEachKey(action);
	}

	@Override
	public void forEachValue(Consumer<ValueType> action) {
		store.forEachValue(action);

		delegate.forEachValue(action);
	}

	@Override
	public ValueType get(KeyType key) {
		if(store.containsKey(key)) return store.get(key);

		return delegate.get(key);
	}

	@Override
	public int getSize() {
		return store.getSize() + delegate.getSize();
	}

	@Override
	public IList<KeyType> keyList() {
		return ListUtils.mergeLists(store.keyList(), delegate.keyList());
	}

	@Override
	public <MappedValue> IMap<KeyType, MappedValue> mapValues(Function<ValueType, MappedValue> transformer) {
		return new TransformedValueMap<>(this, transformer);
	}

	@Override
	public ValueType put(KeyType key, ValueType val) {
		return store.put(key, val);
	}

	@Override
	public ValueType remove(KeyType key) {
		if(!store.containsKey(key)) return delegate.remove(key);
		
		return store.remove(key);
	}

	@Override
	public IList<ValueType> valueList() {
		return ListUtils.mergeLists(store.valueList(), delegate.valueList());
	}
}
