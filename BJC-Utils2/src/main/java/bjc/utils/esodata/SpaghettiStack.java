package bjc.utils.esodata;

/*
 * Implements a spaghetti stack, which is a stack that is branched off of a
 * parent stack.
 *
 * @param T The datatype stored in the stack.
 * @author Ben Culkin
 */
class SpaghettiStack<T> extends Stack<T> {
	private Stack<T> backing;

	private Stack<T> parent;

	/**
	 * Create a new empty spaghetti stack, off of the specified parent.
	 *
	 * @param par
	 *                The parent stack
	 */
	public SpaghettiStack(Stack<T> par) {
		backing = new SimpleStack<>();

		parent = par;
	}

	@Override
	public void push(T elm) {
		backing.push(elm);
	}

	@Override
	public T pop() {
		if(backing.empty()) return parent.pop();

		return backing.pop();
	}

	@Override
	public T top() {
		if(backing.empty()) return parent.top();

		return backing.top();
	}

	@Override
	public int size() {
		return parent.size() + backing.size();
	}

	@Override
	public boolean empty() {
		return backing.empty() && parent.empty();
	}
}
