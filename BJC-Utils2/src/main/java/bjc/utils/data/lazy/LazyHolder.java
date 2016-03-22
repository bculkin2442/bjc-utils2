package bjc.utils.data.lazy;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import bjc.utils.data.IHolder;
import bjc.utils.funcdata.FunctionalList;

/**
 * Holds a single value of a specific type. This is used for indirect
 * references to data, and more specifically for accessing non-final
 * variables from a lambda. AKA the identity monad
 * 
 * This is a lazy variant of {@link IHolder}
 * 
 * @author ben
 *
 * @param <T>
 *            The type of the data being held
 */
public class LazyHolder<T> implements IHolder<T> {
	/**
	 * List of queued actions to be performed on realized values
	 */
	private FunctionalList<Function<T, T>>	actions;

	/**
	 * The value internally held by this lazy holder
	 */
	private T								held;

	/**
	 * The source for a value held by this lazy holder
	 */
	private Supplier<T>						heldSrc;

	/**
	 * Create a new lazy holder with the given supplier
	 * 
	 * @param src
	 *            The supplier for a value when it is neededs
	 */
	public LazyHolder(Supplier<T> src) {
		heldSrc = src;

		held = null;
	}

	/**
	 * Create a new lazy holder with the given value
	 * 
	 * @param val
	 *            The value held in the holder
	 */
	public LazyHolder(T val) {
		held = val;
	}

	@Override
	public void doWith(Consumer<T> f) {
		transform((val) -> {
			f.accept(val);

			return val;
		});
	}

	@Override
	public <NewT> IHolder<NewT> map(Function<T, NewT> f) {
		return new LazyHolder<>(() -> {
			if (held == null) {
				return actions.reduceAux(heldSrc.get(),
						Function<T, T>::apply, f::apply);
			} else {
				return actions.reduceAux(held, Function<T, T>::apply,
						f::apply);
			}
		});
	}

	@Override
	public IHolder<T> transform(Function<T, T> f) {
		actions.add(f);

		return this;
	}

	@Override
	public <E> E unwrap(Function<T, E> f) {
		// Actualize ourselves
		if (held == null) {
			held = heldSrc.get();
		}

		actions.forEach((act) -> held = act.apply(held));

		return f.apply(held);
	}

}
