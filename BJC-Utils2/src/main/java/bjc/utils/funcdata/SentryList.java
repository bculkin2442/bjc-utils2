package bjc.utils.funcdata;

import java.util.List;

public class SentryList<T> extends FunctionalList<T> {
	public SentryList() {
		super();
	}

	public SentryList(List<T> backing) {
		super(backing);
	}

	public boolean add(T item) {
		boolean val = super.add(item);

		if (val)
			System.out.println("Added item (" + item + ") to list");

		return val;
	}
}
