package bjc.utils.data;

import java.util.Iterator;
import java.util.function.Function;

public class TransformedIterator<PreType, PostType> implements Iterator<PostType> {
	private Iterator<PreType> source;

	private Function<PreType, PostType> transform;

	public TransformedIterator(Iterator<PreType> src, Function<PreType, PostType> trans) {
		source = src;
		trans = transform;
	}

	@Override
	public boolean hasNext() {
		return source.hasNext();
	}

	@Override
	public PostType next() {
		return transform.apply(source.next());
	}
}
