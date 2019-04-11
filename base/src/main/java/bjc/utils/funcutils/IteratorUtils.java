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
}
