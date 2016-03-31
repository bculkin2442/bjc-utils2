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
		private Function<T, NewT>				pendingTransform;

		public LazyHolderSupplier(FunctionalList<Function<T, T>> actons,
				Function<T, NewT> transform) {
			// Resolve latent bug I just realized. After a map, adding new
			// actions to the original holder could've resulted in changes
			// to all unactualized mapped values from that holder
			pendingActions = actons.clone();

			this.pendingTransform = transform;
		}

		@Override
		public NewT get() {
			if (heldValue == null) {
				return pendingActions.reduceAux(heldSource.get(),
						Function<T, T>::apply, pendingTransform::apply);
			} else {
				return pendingActions.reduceAux(heldValue,
						Function<T, T>::apply, pendingTransform::apply);
			}
		}
	}

	/**
	 * List of queued actions to be performed on realized values
	 */
	private FunctionalList<Function<T, T>>	actions	=
			new FunctionalList<>();

	/**
	 * The value internally held by this lazy holder
	 */
	private T								heldValue;

	/**
	 * The source for a value held by this lazy holder
	 */
	private Supplier<T>						heldSource;

	/**
	 * Create a new lazy holder with the given supplier
	 * 
	 * @param source
	 *            The supplier for a value when it is neededs
	 */
	public LazyHolder(Supplier<T> source) {
		heldSource = source;

		heldValue = null;
	}

	/**
	 * Create a new lazy holder with the given value
	 * 
	 * @param value
	 *            The value held in the holder
	 */
	public LazyHolder(T value) {
		heldValue = value;
	}

	@Override
	public void doWith(Consumer<T> action) {
		transform((value) -> {
			// Do the action with the value
			action.accept(value);

			// Return the untransformed value
			return value;
		});
	}

	@Override
	public <NewT> IHolder<NewT> map(Function<T, NewT> transform) {
		// Don't actually map until we need to
		return new LazyHolder<>(
				new LazyHolderSupplier<>(actions, transform));
	}

	@Override
	public IHolder<T> transform(Function<T, T> transform) {
		// Queue the transform until we need to apply it
		actions.add(transform);

		return this;
	}

	@Override
	public <E> E unwrap(Function<T, E> unwrapper) {
		// Actualize ourselves
		if (heldValue == null) {
			heldValue = heldSource.get();
		}

		// Apply all pending transforms
		actions.forEach((action) -> heldValue = action.apply(heldValue));

		return unwrapper.apply(heldValue);
	}

}
