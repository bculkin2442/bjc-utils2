package bjc.utils.esodata;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Simple implementation of a stack.
 *
 * @param T
 *                The datatype stored in the stack.
 * @author Ben Culkin
 */
public class SimpleStack<T> extends Stack<T> {
	private Deque<T> backing;

	/**
	 * Create a new empty stack.
	 *
	 */
	public SimpleStack() {
		backing = new LinkedList<>();
	}

	@Override
	public void push(T elm) {
		backing.push(elm);
	}

	@Override
	public T pop() {
		if(backing.isEmpty()) throw new StackUnderflowException();
		
		return backing.pop();
	}

	@Override
	public T top() {
		if(backing.isEmpty()) throw new StackUnderflowException();
		
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
	
	@Override
	public String toString() {
		return backing.toString();
	}
	
	@SuppressWarnings("unchecked")
	public T[] toArray() {
		return (T[]) backing.toArray();
	}
}
