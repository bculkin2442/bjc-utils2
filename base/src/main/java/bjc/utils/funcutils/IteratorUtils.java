package bjc.utils.funcutils;

import java.util.*;
import java.util.function.*;

import bjc.data.ArrayIterator;

/**
 * Utility methods for dealing with iterators.
 *
 * @author bjculkin
 *
 */
public class IteratorUtils {
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
	public static class ChainIterator<T1, T2> implements Iterator<T2> {
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

	/**
	 * Convert an iterator to an iterable.
	 *
	 * @param itr
	 *            The iterator to convert.
	 *
	 * @return An iterable that gives back that iterator.
	 */
	public static <E> Iterable<E> I(Iterator<E> itr) {
		return () -> itr;
	}

	/**
	 * Convert an iterable to an iterator.
	 *
	 * @param itr
	 *            The iterable to convert.
	 *
	 * @return The iterator from that iterable
	 */
	public static <E> Iterator<E> I(Iterable<E> itr) {
		return itr.iterator();
	}

	/**
	 * Convert an array to an iterator.
	 *
	 * @param parms
	 *              The array to iterate over.
	 *
	 * @return An iterator over the provided array.
	 */
	@SafeVarargs
	public static <E> Iterator<E> AI(E... parms) {
		return new ArrayIterator<>(parms);
	}

	/**
	 * Create a chain iterator.
	 *
	 * @param itrA
	 *             The iterator for input values.
	 *
	 * @param itrB
	 *             The transformation for output values.
	 *
	 * @return A chain iterator from the provided values.
	 */
	public static <A, B> Iterator<B> chain(Iterator<A> itrA,
			Function<A, Iterator<B>> itrB) {
		return new ChainIterator<>(itrA, itrB);
	}
}
