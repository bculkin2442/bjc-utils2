package bjc.utils.data;

import java.util.function.Supplier;

public class SingleSupplier<T> implements Supplier<T> {
	private Supplier<T>	source;

	private boolean		gotten;

	private long		id;

	private static long	nextID	= 0;

	public SingleSupplier(Supplier<T> supp) {
		source = supp;

		gotten = false;

		id = nextID++;
	}

	@Override
	public T get() {
		if (gotten == true) {
			throw new IllegalStateException(
					"Attempted to get value more than once"
							+ " from single supplier #" + id);
		}

		gotten = true;

		return source.get();
	}
}
