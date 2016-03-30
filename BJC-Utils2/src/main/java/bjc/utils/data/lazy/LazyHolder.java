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
	private final class LazyHolderSupplier<NewT>
			implements Supplier<NewT> {
		private FunctionalList<Function<T, T>>	pendingActions;
		private Function<T, NewT>				f;

		public LazyHolderSupplier(FunctionalList<Function<T, T>> actons,
				Function<T, NewT> f) {
			// Resolve latent bug I just realized. After a map, adding new
			// actions to the original holder could've resulted in changes
			// to all unactualized mapped values from that holder
			pendingActions = actons.clone();

			this.f = f;
		}

		@Override
		public NewT get() {
			if (held == null) {
				return pendingActions.reduceAux(heldSrc.get(),
						Function<T, T>::apply, f::apply);
			} else {
				return pendingActions.reduceAux(held,
						Function<T, T>::apply, f::apply);
			}
		}
	}

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
			// Do the action with the value
			f.accept(val);

			// Return the untransformed value
			return val;
		});
	}

	@Override
	public <NewT> IHolder<NewT> map(Function<T, NewT> f) {
		// Don't actually map until we need to
		return new LazyHolder<>(new LazyHolderSupplier<>(actions, f));
	}

	@Override
	public IHolder<T> transform(Function<T, T> f) {
		// Queue the transform until we need to apply it
		actions.add(f);

		return this;
	}

	@Override
	public <E> E unwrap(Function<T, E> f) {
		// Actualize ourselves
		if (held == null) {
			held = heldSrc.get();
		}

		// Apply all pending transforms
		actions.forEach((act) -> held = act.apply(held));

		return f.apply(held);
	}

}
