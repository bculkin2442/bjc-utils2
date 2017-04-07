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
 *                The supplied type
 */
public class SingleSupplier<T> implements Supplier<T> {
	private static long nextID = 0;

	private Supplier<T> source;

	private boolean gotten;

	private long id;

	/*
	 * This is bad practice, but I want to know where the single
	 * instantiation was, in case of duplicate initiations.
	 */
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
			String msg = String.format(
					"Attempted to retrieve value more than once from single supplier #%d", id);

			IllegalStateException isex = new IllegalStateException(msg);

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

	@Override
	public String toString() {
		return String.format("SingleSupplier [source='%s', gotten=%s, id=%s]", source, gotten, id);
	}
}
