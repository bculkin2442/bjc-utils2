package bjc.utils.funcdata;

import java.util.List;

/**
 * A list that logs when items are inserted into it.
 *
 * @author bjculkin
 *
 * @param <T>
 *        The type of item in the list.
 */
public class SentryList<T> extends FunctionalList<T> {
	/** Create a new sentry list. */
	public SentryList() {
		super();
	}

	/**
	 * Create a new sentry list backed by an existing list.
	 *
	 * @param backing
	 *        The backing list.
	 */
	public SentryList(final List<T> backing) {
		super(backing);
	}

	@Override
	public boolean add(final T item) {
		final boolean val = super.add(item);

		if(val) {
			System.out.println("Added item (" + item + ") to list");
		}

		return val;
	}
}
