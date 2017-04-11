package bjc.utils.data;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * An iterator that generates a series of elements from a single element.
 * 
 * @author bjculkin
 *
 * @param <E>
 *                The type of element generated.
 */
public class GeneratingIterator<E> implements Iterator<E> {
	private E state;

	private UnaryOperator<E> transtion;

	private Predicate<E> stpper;

	/**
	 * Create a new generative iterator.
	 * 
	 * @param initial
	 *                The initial state of the generator.
	 * 
	 * @param transition
	 *                The function to apply to the state.
	 * 
	 * @param stopper
	 *                The predicate applied to the current state to
	 *                determine when to stop.
	 */
	public GeneratingIterator(E initial, UnaryOperator<E> transition, Predicate<E> stopper) {
		state = initial;
		transtion = transition;
		stpper = stopper;
	}

	@Override
	public boolean hasNext() {
		return stpper.test(state);
	}

	@Override
	public E next() {
		state = transtion.apply(state);

		return state;
	}

}
