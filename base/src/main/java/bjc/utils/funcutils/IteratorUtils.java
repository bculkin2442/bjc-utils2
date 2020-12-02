package bjc.utils.funcutils;

import java.util.*;
import java.util.function.*;

import bjc.data.*;

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
	 * @param <E> The type being iterated over.
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
	 * @param <E> The type being iterated over.
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
	 * @param <E> The type being iterated over.
	 * 
	 * @param parms
	 *              The array to iterate over.
	 *
	 * @return An iterator over the provided array.
	 */
	@SafeVarargs
	public static <E> Iterator<E> I(E... parms) {
		return new ArrayIterator<>(parms);
	}

	/**
	 * Create a chain iterator.
	 * 
	 * @param <A> The initial type being iterated over.
	 * @param <B> The resulting type being iterated over.
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
	
	/**
	 * Perform a left-fold over an iterator.
	 * 
	 * @param <ElementType> The type of elements in the iterator.
	 * @param <ResultType> The result from the fold.
	 * 
	 * @param itr The items to iterate over.
	 * @param zero The initial element for the fold.
	 * @param folder The function that does the folding.
	 * 
	 * @return The result of folding over the iterator.
	 */
	public static <ElementType, ResultType> ResultType foldLeft(
			Iterable<ElementType> itr,
			ResultType zero,
			BiFunction<ElementType, ResultType, ResultType> folder)
	{
		ResultType state = zero;
		for (ElementType elem : itr) {
			state = folder.apply(elem, state);
		}
		return state;
	}
	
	/**
	 * Creates an 'entangled' pair of a consumer and an iterator.
	 * 
	 * @param <ElementType> The type of value involved.
	 * 
	 * @return A pair consisting of a consumer of values, and an iterator that
	 *         yields the consumed values.
	 */
	public static <ElementType>
	IPair<Consumer<ElementType>, Iterator<ElementType>> entangle()
	{
		Queue<ElementType> backer = new ArrayDeque<>();
		return IPair.pair(backer::add, new QueueBackedIterator<>(backer));
	}
}
