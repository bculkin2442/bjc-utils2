package bjc.utils.funcdata;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import bjc.utils.funcutils.ListUtils;

class ExtendedMap<KeyType, ValueType>
		implements IFunctionalMap<KeyType, ValueType> {
	private IFunctionalMap<KeyType, ValueType>	delegate;

	private IFunctionalMap<KeyType, ValueType>	store;

	public ExtendedMap(IFunctionalMap<KeyType, ValueType> delegate,
			IFunctionalMap<KeyType, ValueType> store) {
		this.delegate = delegate;
		this.store = store;
	}

	@Override
	public ValueType put(KeyType key, ValueType val) {
		return store.put(key, val);
	}

	@Override
	public ValueType get(KeyType key) {
		if (store.containsKey(key)) {
			return store.get(key);
		}

		return delegate.get(key);
	}

	@Override
	public <MappedValue> IFunctionalMap<KeyType, MappedValue> mapValues(
			Function<ValueType, MappedValue> transformer) {
		return new TransformedValueMap<>(this, transformer);
	}

	@Override
	public boolean containsKey(KeyType key) {
		if (store.containsKey(key)) {
			return true;
		}

		return delegate.containsKey(key);
	}

	@Override
	public IFunctionalList<KeyType> keyList() {
		return ListUtils.mergeLists(store.keyList(), delegate.keyList());
	}

	@Override
	public void forEach(BiConsumer<KeyType, ValueType> action) {
		store.forEach(action);

		delegate.forEach(action);
	}

	@Override
	public ValueType remove(KeyType key) {
		return store.remove(key);
	}

	@Override
	public int getSize() {
		return store.getSize() + delegate.getSize();
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
	public IFunctionalList<ValueType> valueList() {
		return ListUtils.mergeLists(store.valueList(),
				delegate.valueList());
	}

	@Override
	public IFunctionalMap<KeyType, ValueType> extend() {
		return new ExtendedMap<>(this, new FunctionalMap<>());
	}
}
