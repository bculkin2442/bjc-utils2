package bjc.utils.esodata;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.IList;
import bjc.utils.funcdata.IMap;

/**
 * A variant of a map where inserting a duplicate key shadows the existing value
 * instead of replacing it.
 *
 * This could be useful for things like variable scopes.
 *
 * @author EVE
 *
 * @param <KeyType>
 *        The key of the map.
 *
 * @param <ValueType>
 *        The values in the map.
 */
public class PushdownMap<KeyType, ValueType> implements IMap<KeyType, ValueType> {
	/* Our backing storage. */
	private final IMap<KeyType, Stack<ValueType>> backing;

	/** Create a new empty stack-based map. */
	public PushdownMap() {
		backing = new FunctionalMap<>();
	}

	/** Create a new empty stack-based map using the specified backing. */
	private PushdownMap(final IMap<KeyType, Stack<ValueType>> back) {
		backing = back;
	}

	@Override
	public void clear() {
		backing.clear();
	}

	@Override
	public boolean containsKey(final KeyType key) {
		return backing.containsKey(key);
	}

	@Override
	public IMap<KeyType, ValueType> extend() {
		return new PushdownMap<>(backing.extend());
	}

	@Override
	public void forEach(final BiConsumer<KeyType, ValueType> action) {
		backing.forEach((key, stk) -> action.accept(key, stk.top()));
	}

	@Override
	public void forEachKey(final Consumer<KeyType> action) {
		backing.forEachKey(action);
	}

	@Override
	public void forEachValue(final Consumer<ValueType> action) {
		backing.forEachValue(stk -> action.accept(stk.top()));
	}

	@Override
	public ValueType get(final KeyType key) {
		return backing.get(key).top();
	}

	@Override
	public int size() {
		return backing.size();
	}

	@Override
	public IList<KeyType> keyList() {
		return backing.keyList();
	}

	@Override
	public <V2> IMap<KeyType, V2> transform(final Function<ValueType, V2> transformer) {
		/*
		 * @NOTE Can and should we support this? More to the point,
		 * maybe this should be a map sub-type that does what it needs
		 * to?
		 */
		throw new UnsupportedOperationException("Cannot transform pushdown maps.");
	}

	@Override
	public ValueType put(final KeyType key, final ValueType val) {
		if(backing.containsKey(key)) {
			final Stack<ValueType> stk = backing.get(key);

			final ValueType vl = stk.top();

			stk.push(val);

			return vl;
		} else {
			final Stack<ValueType> stk = new SimpleStack<>();

			stk.push(val);

			return null;
		}
	}

	@Override
	public ValueType remove(final KeyType key) {
		final Stack<ValueType> stk = backing.get(key);

		if(stk.size() > 1) {
			return stk.pop();
		} else {
			return backing.remove(key).top();
		}
	}

	@Override
	public IList<ValueType> valueList() {
		return backing.valueList().map(Stack::top);
	}

	@Override
	public int hashCode() {
		final int prime = 31;

		int result = 1;
		result = prime * result + (backing == null ? 0 : backing.hashCode());

		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof PushdownMap<?, ?>)) return false;

		final PushdownMap<?, ?> other = (PushdownMap<?, ?>) obj;

		if(backing == null) {
			if(other.backing != null) return false;
		} else if(!backing.equals(other.backing)) return false;

		return true;
	}

	@Override
	public String toString() {
		return String.format("PushdownMap [backing=%s]", backing);
	}
}
