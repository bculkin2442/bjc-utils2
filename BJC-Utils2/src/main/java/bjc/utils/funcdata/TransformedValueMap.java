package bjc.utils.funcdata;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A map that transforms values from one type to another
 *
 * @author ben
 *
 * @param <OldKey>
 *                The type of the map's keys
 * @param <OldValue>
 *                The type of the map's values
 * @param <NewValue>
 *                The type of the transformed values
 */
final class TransformedValueMap<OldKey, OldValue, NewValue> implements IMap<OldKey, NewValue> {
	private IMap<OldKey, OldValue>		backing;
	private Function<OldValue, NewValue>	transformer;

	public TransformedValueMap(IMap<OldKey, OldValue> backingMap, Function<OldValue, NewValue> transform) {
		backing = backingMap;
		transformer = transform;
	}

	@Override
	public void clear() {
		backing.clear();
	}

	@Override
	public boolean containsKey(OldKey key) {
		return backing.containsKey(key);
	}

	@Override
	public IMap<OldKey, NewValue> extend() {
		return new ExtendedMap<>(this, new FunctionalMap<>());
	}

	@Override
	public void forEach(BiConsumer<OldKey, NewValue> action) {
		backing.forEach((key, value) -> {
			action.accept(key, transformer.apply(value));
		});
	}

	@Override
	public void forEachKey(Consumer<OldKey> action) {
		backing.forEachKey(action);
	}

	@Override
	public void forEachValue(Consumer<NewValue> action) {
		backing.forEachValue((value) -> {
			action.accept(transformer.apply(value));
		});
	}

	@Override
	public NewValue get(OldKey key) {
		return transformer.apply(backing.get(key));
	}

	@Override
	public int getSize() {
		return backing.getSize();
	}

	@Override
	public IList<OldKey> keyList() {
		return backing.keyList();
	}

	@Override
	public <MappedValue> IMap<OldKey, MappedValue> mapValues(Function<NewValue, MappedValue> transform) {
		return new TransformedValueMap<>(this, transform);
	}

	@Override
	public NewValue put(OldKey key, NewValue value) {
		throw new UnsupportedOperationException("Can't add items to transformed map");
	}

	@Override
	public NewValue remove(OldKey key) {
		return transformer.apply(backing.remove(key));
	}

	@Override
	public String toString() {
		return backing.toString();
	}

	@Override
	public IList<NewValue> valueList() {
		return backing.valueList().map(transformer);
	}
}
