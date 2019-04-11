package bjc.utils.funcutils;

import java.util.Iterator;
import java.util.function.Function;

/**
 * Utility methods for dealing with iterators.
 * 
 * @author bjculkin
 *
 */
public class IteratorUtils {
	public static class ChainIterator<T1, T2> implements Iterator<T2> {
		private Iterator<T1> mainItr;
		private Function<T1, Iterator<T2>> trans;

		private Iterator<T2> curItr;

		public ChainIterator(Iterator<T1> mainItr, Function<T1, Iterator<T2>> trans) {
			this.mainItr = mainItr;
			this.trans   = trans;
		}

		@Override
		public boolean hasNext() {
			if (curItr != null) {
				if (curItr.hasNext()) return true;
				else                  return mainItr.hasNext();
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
	/**
	 * Convert an iterator to an iterable.
	 * 
	 * @param itr
	 *                The iterator to convert.
	 * @return An iterable that gives back that iterator.
	 */
	public static <E> Iterable<E> I(Iterator<E> itr) {
		return () -> itr;
	}

	/**
	 * Convert an iterable to an iterator.
	 * 
	 * @param itr
	 *                The iterable to convert.
	 * @return The iterator from that iterable
	 */
	public static <E> Iterator<E> I(Iterable<E> itr) {
		return itr.iterator();
	}

	public static <A, B> Iterator<B> chain(Iterator<A> itrA, Function<A, Iterator<B>> itrB) {
		return new ChainIterator(itrA, itrB);
	}
}
