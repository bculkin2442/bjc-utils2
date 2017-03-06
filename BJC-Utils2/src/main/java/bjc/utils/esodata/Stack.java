package bjc.utils.esodata;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.LinkedList;
import java.util.function.Consumer;

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
		List<T> lst = new ArrayList<>(n);

		for(int i = n; i > 0; i--) {
			lst.set(i - 1, backing.pop());
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
		List<T> lst = new ArrayList<>(n);

		T elm = backing.pop();

		for(int i = n; i > 0; i--) {
			lst.set(i - 1, backing.pop());
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

	/*
	 * Dataflow Combinators
	 */
	/**
	 * Hides the top n elements on the stack from cons.
	 *
	 * @param n The number of elements to hide.
	 * @param cons The action to hide the elements from
	 */
	public void dip(int n, Consumer<Stack<T>> cons) {
		List<T> elms = new ArrayList<>(n);

		for(int i = n; i > 0; i--) {
			elms.set(i - 1, backing.pop());
		}

		cons.accept(this);

		for(T elm : elms) {
			backing.push(elm);
		}
	}

	/**
	 * Hide the top element of the stack from cons.
	 *
	 * @param cons The action to hide the top from
	 */
	public void dip(Consumer<Stack<T>> cons) {
		dip(1, cons);
	}

	/**
	 * Copy the top n elements on the stack, replacing them once cons is
	 * done.
	 *
	 * @param n The number of elements to copy.
	 * @param cons The action to execute.
	 */
	public void keep(int n, Consumer<Stack<T>> cons) {
		dup(n);
		dip(n, cons);
	}

	/**
	 * Apply all the actions in conses to the top n elements of the stack.
	 *
	 * @param n The number of elements to give to cons.
	 * @param conses The actions to execute.
	 */
	public void multicleave(int n, List<Consumer<Stack<T>>> conses) {
		List<T> elms = new ArrayList<>(n);

		for(int i = n; i > 0; i--) {
			elms.set(i - 1, backing.pop());
		}

		for(Consumer<Stack<T>> cons : conses) {
			for(T elm : elms) {
				backing.push(elm);
			}

			cons.accept(this);
		}
	}

	/**
	 * Apply all the actions in conses to the top element of the stack.
	 *
	 * @param conses The actions to execute.
	 */
	public void cleave(List<Consumer<Stack<T>>> conses) {
		multicleave(1, conses);
	}

	/**
	 * Apply every action in cons to n arguments.
	 *
	 * @param n The number of parameters each action takes.
	 * @param conses The actions to execute.
	 */
	public void multispread(int n, List<Consumer<Stack<T>>> conses) {
		List<List<T>> nelms = new ArrayList<>(conses.size());

		for(int i = conses.size(); i > 0; i--) {
			List<T> elms = new ArrayList<>(n);

			for(int j = n; j > 0; j--) {
				elms.set(j, backing.pop());
			}

			nelms.set(i, elms);
		}

		int i = 0;
		for(List<T> elms : nelms) {
			for(T elm : elms) {
				backing.push(elm);
			}

			conses.get(i).accept(this);
			i += 1;
		}
	}

	/**
	 * Apply the actions in cons to corresponding elements from the stack.
	 *
	 * @parma conses The actions to execute.
	 */
	public void spread(List<Consumer<Stack<T>>> conses) {
		multispread(1, conses);
	}

	/**
	 * Apply the action in cons to the first m groups of n arguments.
	 *
	 * @param n The number of arguments cons takes.
	 * @param m The number of time to call cons.
	 * @param cons The action to execute.
	 */
	public void multiapply(int n, int m, Consumer<Stack<T>> cons) {
		List<Consumer<Stack<T>>> conses = new ArrayList<>(m);

		for(int i = 0; i < m; i++) {
			conses.add(cons);
		}

		multispread(n, conses);
	}

	/**
	 * Apply cons n times to the corresponding elements in the stack.
	 *
	 * @param n The number of times to execute cons.
	 * @param cons The action to execute.
	 */
	public void apply(int n, Consumer<Stack<T>> cons) {
		multiapply(1, n, cons);
	}
}
