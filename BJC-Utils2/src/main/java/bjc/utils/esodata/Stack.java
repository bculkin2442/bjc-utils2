package bjc.utils.esodata;
import java.util.Deque;
import java.util.LinkedList;

/**
 * A stack, with support for combinators.
 *
 * A FILO stack with support for forth/factor style combinators.
 *
 * @param T The datatype stored in the stack.
 * @author Ben Culkin
 */
public class Stack<T> {
	private Deque<T> backing;

	/**
	 * Create a new empty stack.
	 *
	 */
	public Stack() {
		backing = new LinkedList();
	}

	/**
	 * Push an element onto the stack.
	 *
	 * @param elm The element to insert.
	 */
	public void push(T elm) {
		backing.push(elm);
	}

	/**
	 * Pop an element off of the stack.
	 *
	 * @return The element on top of the stack.
	 */
	public T pop(T elm) {
		return backing.pop();
	}

	/**
	 * Get the top item of the stack without removing it.
	 *
	 * @return The element on top of the stack.
	 */
	public T top() {
		return backing.peek();
	}

	/**
	 * Get the number of elements in the stack.
	 *
	 * @return the number of elements in the stack.
	 */
	public int size() {
		return backing.size();
	}

	/** Check if the stack is empty.
	 *
	 * @return Whether or not the stack is empty
	 */
	public boolean empty() {
		return backing.size() == 0;
	}

	/*
	 * Basic combinators
	 */
	
	/**
	 * Drop n items from the stack.
	 *
	 * @param n The number of items to drop.
	 */
	public void drop(int n) {
		for(int i = 0; i < n; i++) {
			backing.pop();
		}
	}

	/**
	 * Drop one item from the stack.
	 */
	public void drop() {
		drop(1);
	}

	/**
	 * Delete n items below the current one.
	 *
	 * @param n The number of items below the top to delete.
	 */
	public void nip(int n) {
		T elm = backing.pop();

		drop(n);

		backing.push(elm);
	}

	/**
	 * Delete the second element in the stack.
	 */
	public void nip() {
		nip(1);
	}

	/**
	 * Replicate the top n items of the stack m times.
	 *
	 * @param n The number of items to duplicate.
	 * @param m The number of times to duplicate items.
	 */
	public void multidup(int n, int m) {
		LinkedList<T> lst = new LinkedList<>();

		for(int i = 0; i < n; i++) {
			lst.add(0, backing.pop());
		}
		
		for(int i = 0; i < m; i++) {
			for(T elm : lst) {
				backing.push(elm);
			}
		}
	}

	/**
	 * Duplicate the top n items of the stack.
	 *
	 * @param n The number of items to duplicate.
	 */
	public void dup(int n) {
		multidup(n, 2);
	}

	/**
	 * Duplicate the top item on the stack.
	 */
	public void dup() {
		dup(1);
	}

	/**
	 * Replicate the n elements below the top one m times.
	 *
	 * @param n The number of items to duplicate.
	 * @param m The number of times to duplicate items.
	 */
	public void multiover(int n, int m) {
		LinkedList<T> lst = new LinkedList<>();

		T elm = backing.pop();

		for(int i = 0; i < n; i++) {
			lst.add(0, backing.pop());
		}

		for(T nelm : lst) {
			backing.push(nelm);
		}
		backing.push(elm);

		for(int i = 1; i < m; i++) {
			for(T nelm : lst) {
				backing.push(nelm);
			}
		}
	}

	/**
	 * Duplicate the n elements below the top one.
	 *
	 * @param n The number of items to duplicate.
	 */
	public void over(int n) {
		multiover(n, 2);
	}

	/**
	 * Duplicate the second item in the stack.
	 */
	public void over() {
		over(1);
	}

	/**
	 * Duplicate the third item in the stack.
	 */
	public void pick() {
		T z = backing.pop();
		T y = backing.pop();
		T x = backing.pop();

		backing.push(x);
		backing.push(y);
		backing.push(z);
		backing.push(x);
	}

	/**
	 * Swap the top two items on the stack.
	 */
	public void swap() {
		T y = backing.pop();
		T x = backing.pop();

		backing.push(y);
		backing.push(x);
	}

	/**
	 * Duplicate the second item below the first item.
	 */
	public void deepdup() {
		T y = backing.pop();
		T x = backing.pop();

		backing.push(x);
		backing.push(x);
		backing.push(y);
	}

	/**
	 * Swap the second and third items in the stack.
	 */
	public void deepswap() {
		T z = backing.pop();
		T y = backing.pop();
		T x = backing.pop();

		backing.push(y);
		backing.push(x);
		backing.push(z);
	}

	/**
	 * Rotate the top three items on the stack
	 */
	public void rot() {
		T z = backing.pop();
		T y = backing.pop();
		T x = backing.pop();

		backing.push(y);
		backing.push(z);
		backing.push(x);
	}

	/**
	 * Inversely rotate the top three items on the stack
	 */
	public void invrot() {
		T z = backing.pop();
		T y = backing.pop();
		T x = backing.pop();

		backing.push(z);
		backing.push(x);
		backing.push(y);
	}
}
