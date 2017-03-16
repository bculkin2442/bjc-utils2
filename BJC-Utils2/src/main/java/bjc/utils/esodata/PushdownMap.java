package bjc.utils.esodata;

import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.IList;
import bjc.utils.funcdata.IMap;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A variant of a map where inserting a duplicate key shadows the existing value
 * instead of replacing it.
 * 
 * @author EVE
 *
 * @param <KeyType>
 *                The key of the map.
 * @param <ValueType>
 *                The values in the map.
 */
public class PushdownMap<KeyType, ValueType> implements IMap<KeyType, ValueType> {
	private IMap<KeyType, Stack<ValueType>> backing;

	/**
	 * Create a new empty stack-based map.
	 */
	public PushdownMap() {
		backing = new FunctionalMap<>();
	}

	private PushdownMap(IMap<KeyType, Stack<ValueType>> back) {
		backing = back;
	}

	@Override
	public void clear() {
		backing.clear();
	}

	@Override
	public boolean containsKey(KeyType key) {
		return backing.containsKey(key);
	}

	@Override
	public IMap<KeyType, ValueType> extend() {
		return new PushdownMap<>(backing.extend());
	}

	@Override
	public void forEach(BiConsumer<KeyType, ValueType> action) {
		backing.forEach((key, stk) -> action.accept(key, stk.top()));
	}

	@Override
	public void forEachKey(Consumer<KeyType> action) {
		backing.forEachKey(action);
	}

	@Override
	public void forEachValue(Consumer<ValueType> action) {
		backing.forEachValue(stk -> action.accept(stk.top()));
	}

	@Override
	public ValueType get(KeyType key) {
		return backing.get(key).top();
	}

	@Override
	public int getSize() {
		return backing.getSize();
	}

	@Override
	public IList<KeyType> keyList() {
		return backing.keyList();
	}

	@Override
	public <V2> IMap<KeyType, V2> mapValues(Function<ValueType, V2> transformer) {
		throw new UnsupportedOperationException("Cannot transform pushdown maps.");
	}

	@Override
	public ValueType put(KeyType key, ValueType val) {
		if(backing.containsKey(key)) {
			Stack<ValueType> stk = backing.get(key);

			ValueType vl = stk.top();

			stk.push(val);

			return vl;
		} else {
			Stack<ValueType> stk = new SimpleStack<>();

			stk.push(val);

			return null;
		}
	}

	@Override
	public ValueType remove(KeyType key) {
		Stack<ValueType> stk = backing.get(key);

		if(stk.size() > 1) {
			return stk.pop();
		} else {
			return backing.remove(key).top();
		}
	}

	@Override
	public IList<ValueType> valueList() {
		return backing.valueList().map(stk -> stk.top());
	}
}
