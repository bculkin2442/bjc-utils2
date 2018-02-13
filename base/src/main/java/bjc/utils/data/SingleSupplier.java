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
 *        The supplied type
 */
public class SingleSupplier<T> implements Supplier<T> {
	/* The next supplier ID. */
	private static long nextID = 0;
	/* The supplier to yield from. */
	private final Supplier<T> source;
	/* Whether this value has been retrieved yet. */
	private boolean gotten;
	/* The ID of this supplier. */
	private final long id;

	/*
	 * The place where the supplier was instantiated.
	 *
	 * @NOTE This is both slow to create, and generally bad practice to keep
	 * exceptions around without throwing them. However, it is very useful
	 * to find where the first instantiation was.
	 */
	private Exception instSite;

	/**
	 * Create a new single supplier from an existing value.
	 *
	 * @param supp
	 *        The supplier to give a single value from.
	 */
	public SingleSupplier(final Supplier<T> supp) {
		source = supp;

		gotten = false;

		id = nextID++;
	}

	@Override
	public T get() {
		if(gotten == true) {
			final String msg = String.format(
					"Attempted to retrieve value more than once from single supplier #%d", id);

			final IllegalStateException isex = new IllegalStateException(msg);

			isex.initCause(instSite);

			throw isex;
		}

		gotten = true;

		try {
			throw new IllegalStateException("Previous instantiation here.");
		} catch(final IllegalStateException isex) {
			instSite = isex;
		}

		return source.get();
	}

	@Override
	public String toString() {
		return String.format("SingleSupplier [source='%s', gotten=%s, id=%s]", source, gotten, id);
	}
}
