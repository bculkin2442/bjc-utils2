package bjc.utils.esodata;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * A FIFO implementation of a stack.
 *
 * @param T The datatype stored in the stack.
 * @author Ben Culkin
 */
public class QueueStack<T> extends Stack<T> {
	private Deque<T> backing;

	/**
	 * Create a new empty stack queue.
	 *
	 */
	public SimpleStack() {
		backing = new LinkedList();
	}

	@Override
	public void push(T elm) {
		backing.add(elm);
	}

	@Override
	public T pop() {
		return backing.remove();
	}

	@Override
	public T top() {
		return backing.peek();
	}

	@Override
	public int size() {
		return backing.size();
	}

	@Override
	public boolean empty() {
		return backing.size() == 0;
	}
}
