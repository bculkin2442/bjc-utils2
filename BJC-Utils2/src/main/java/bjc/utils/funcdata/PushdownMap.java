package bjc.utils.funcdata;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A map where
 * 
 * @author ben
 *
 * @param <KeyType>
 * @param <ValueType>
 */
public class PushdownMap<KeyType, ValueType>
		implements IMap<KeyType, ValueType> {

	@Override
	public boolean containsKey(KeyType key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IMap<KeyType, ValueType> extend() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void forEach(BiConsumer<KeyType, ValueType> action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void forEachKey(Consumer<KeyType> action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void forEachValue(Consumer<ValueType> action) {
		// TODO Auto-generated method stub

	}

	@Override
	public ValueType get(KeyType key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IList<KeyType> keyList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V2> IMap<KeyType, V2> mapValues(
			Function<ValueType, V2> transformer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueType put(KeyType key, ValueType val) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueType remove(KeyType key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IList<ValueType> valueList() {
		// TODO Auto-generated method stub
		return null;
	}

}
