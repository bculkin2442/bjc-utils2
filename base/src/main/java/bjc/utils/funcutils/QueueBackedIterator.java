package bjc.utils.funcutils;

import java.util.*;

/**
 * An iterator backed by a queue.
 * 
 * @author Ben Culkin
 *
 * @param <ElementType> The type of element
 */
public class QueueBackedIterator<ElementType>
		implements Iterator<ElementType>
{
	private final Queue<ElementType> backer;

	/**
	 * Create a new queue-backed iterator.
	 * 
	 * @param backer The queue which backs this iterator.
	 */
	public QueueBackedIterator(Queue<ElementType> backer)
	{
		this.backer = backer;
	}

	@Override
	public boolean hasNext() {
		return !backer.isEmpty();
	}

	@Override
	public ElementType next() {
		return backer.remove();
	}
}