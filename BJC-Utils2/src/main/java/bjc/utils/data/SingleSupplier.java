package bjc.utils.data;

import java.util.function.Supplier;

/**
 * A supplier that can only supply one value.
 * 
 * Attempting to retrieve another value will cause an exception to be thrown.
 * 
 * @author ben
 *
 * @param <T>
 */
public class SingleSupplier<T> implements Supplier<T> {
	private static long nextID = 0;

	private Supplier<T> source;

	private boolean gotten;

	private long id;

	// This is bad practice, but I want to know where the single
	// instantiation was, in case of duplicate initiations
	private Exception instSite;

	/**
	 * Create a new single supplier from an existing value
	 * 
	 * @param supp
	 *                The supplier to give a single value from
	 */
	public SingleSupplier(Supplier<T> supp) {
		source = supp;

		gotten = false;

		id = nextID++;
	}

	@Override
	public T get() {
		if (gotten == true) {
			IllegalStateException isex = new IllegalStateException("Attempted to get value more than once"
					+ " from single supplier #" + id + ". Previous instantiation below.");

			isex.initCause(instSite);

			throw isex;
		}

		gotten = true;

		try {
			throw new IllegalStateException("Previous instantiation here.");
		} catch (IllegalStateException isex) {
			instSite = isex;
		}

		return source.get();
	}
}
