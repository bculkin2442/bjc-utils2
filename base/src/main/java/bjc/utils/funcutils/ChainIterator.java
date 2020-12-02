package bjc.utils.funcutils;

import java.util.*;
import java.util.function.*;

/**
 * A chain iterator. This is essentially flatMap in iterator form.
 * 
 * @author bjculkin
 *
 * @param <T1>
 *             The type of the input values.
 *
 * @param <T2>
 *             The type of the output values.
 */
public class ChainIterator<T1, T2> implements Iterator<T2> {
	private Iterator<T1> mainItr;
	private Function<T1, Iterator<T2>> trans;

	private Iterator<T2> curItr;

	/**
	 * Create a new chain iterator.
	 *
	 * @param mainItr
	 *                The main iterator for input.
	 *
	 * @param trans
	 *                The transformation to use to produce the outputs.
	 */
	public ChainIterator(Iterator<T1> mainItr, Function<T1, Iterator<T2>> trans) {
		this.mainItr = mainItr;
		this.trans = trans;
	}

	@Override
	public boolean hasNext() {
		if (curItr != null) {
			return curItr.hasNext() ? true : mainItr.hasNext();
		}

		return mainItr.hasNext();
	}

	@Override
	public T2 next() {
		if (curItr == null || !curItr.hasNext()) {
			curItr = trans.apply(mainItr.next());
		}

		return curItr.next();
	}
}