package bjc.utils.data;

import java.util.Iterator;
import java.util.function.Function;

/**
 * An iterator that transforms values from one type to another.
 *
 * @author EVE
 *
 * @param <S>
 *                The source iterator type.
 *
 * @param <D>
 *                The destination iterator type.
 */
public class TransformIterator<S, D> implements Iterator<D> {
	private final Iterator<S> source;

	private final Function<S, D> transform;

	/**
	 * Create a new transform iterator.
	 *
	 * @param source
	 *                The source iterator to use.
	 *
	 * @param transform
	 *                The transform to apply.
	 */
	public TransformIterator(final Iterator<S> source, final Function<S, D> transform) {
		this.source = source;
		this.transform = transform;
	}

	@Override
	public boolean hasNext() {
		return source.hasNext();
	}

	@Override
	public D next() {
		return transform.apply(source.next());
	}

}
