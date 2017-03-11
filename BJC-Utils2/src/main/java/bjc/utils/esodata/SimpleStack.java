package bjc.utils.esodata;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * Simple implementation of a stack.
 *
 * @param T The datatype stored in the stack.
 * @author Ben Culkin
 */
public class SimpleStack<T> extends Stack<T> {
	private Deque<T> backing;

	/**
	 * Create a new empty stack.
	 *
	 */
	public SimpleStack() {
		backing = new LinkedList();
	}

	@Override
	public void push(T elm) {
		backing.push(elm);
	}

	@Override
	public T pop() {
		return backing.pop();
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
