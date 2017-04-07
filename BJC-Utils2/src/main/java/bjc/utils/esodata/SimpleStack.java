package bjc.utils.esodata;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Simple implementation of a stack.
 *
 * @param <T>
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
		if (backing.isEmpty())
			throw new StackUnderflowException();

		return backing.pop();
	}

	@Override
	public T top() {
		if (backing.isEmpty())
			throw new StackUnderflowException();

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

	@SuppressWarnings("unchecked")
	public T[] toArray() {
		return (T[]) backing.toArray();
	}

	@Override
	public int hashCode() {
		final int prime = 31;

		int result = 1;
		result = prime * result + ((backing == null) ? 0 : backing.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SimpleStack<?>))
			return false;

		SimpleStack<?> other = (SimpleStack<?>) obj;

		if (backing == null) {
			if (other.backing != null)
				return false;
		} else if (!backing.equals(other.backing))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return String.format("SimpleStack [backing=%s]", backing);
	}
}
