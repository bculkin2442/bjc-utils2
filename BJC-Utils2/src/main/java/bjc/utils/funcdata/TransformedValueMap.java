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
 *            The type of the map's keys
 * @param <OldValue>
 *            The type of the map's values
 * @param <NewValue>
 *            The type of the transformed values
 */
final class TransformedValueMap<OldKey, OldValue, NewValue>
		implements IMap<OldKey, NewValue> {
	private IMap<OldKey, OldValue>			mapToTransform;
	private Function<OldValue, NewValue>	transformer;

	public TransformedValueMap(IMap<OldKey, OldValue> destMap,
			Function<OldValue, NewValue> transform) {
		mapToTransform = destMap;
		transformer = transform;
	}

	@Override
	public boolean containsKey(OldKey key) {
		return mapToTransform.containsKey(key);
	}

	@Override
	public IMap<OldKey, NewValue> extend() {
		return new ExtendedMap<>(this, new FunctionalMap<>());
	}

	@Override
	public void forEach(BiConsumer<OldKey, NewValue> action) {
		mapToTransform.forEach((key, val) -> {
			action.accept(key, transformer.apply(val));
		});
	}

	@Override
	public void forEachKey(Consumer<OldKey> action) {
		mapToTransform.forEachKey(action);
	}

	@Override
	public void forEachValue(Consumer<NewValue> action) {
		mapToTransform.forEachValue((val) -> {
			action.accept(transformer.apply(val));
		});
	}

	@Override
	public NewValue get(OldKey key) {
		return transformer.apply(mapToTransform.get(key));
	}

	@Override
	public int getSize() {
		return mapToTransform.getSize();
	}

	@Override
	public IList<OldKey> keyList() {
		return mapToTransform.keyList();
	}

	@Override
	public <MappedValue> IMap<OldKey, MappedValue> mapValues(
			Function<NewValue, MappedValue> transform) {
		return new TransformedValueMap<>(this, transform);
	}

	@Override
	public NewValue put(OldKey key, NewValue val) {
		throw new UnsupportedOperationException(
				"Can't add items to transformed map");
	}

	@Override
	public NewValue remove(OldKey key) {
		return transformer.apply(mapToTransform.remove(key));
	}

	@Override
	public String toString() {
		return mapToTransform.toString();
	}

	@Override
	public IList<NewValue> valueList() {
		return mapToTransform.valueList().map(transformer);
	}

	@Override
	public void clear() {
		mapToTransform.clear();
	}
}