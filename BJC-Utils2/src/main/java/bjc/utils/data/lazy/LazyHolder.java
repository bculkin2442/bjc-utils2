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
public class LazyHolder<T> implements IHolder<T>, ILazy {
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
		if (source == null) {
			throw new NullPointerException("Source must be non-null");
		}

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
		if (action == null) {
			throw new NullPointerException("Action must be non-null");
		}

		transform((value) -> {
			// Do the action with the value
			action.accept(value);

			// Return the untransformed value
			return value;
		});
	}

	@Override
	public <NewT> IHolder<NewT> map(Function<T, NewT> transform) {
		if (transform == null) {
			throw new NullPointerException("Transform must be non-null");
		}

		// Don't actually map until we need to
		return new LazyHolder<>(
				new LazyHolderSupplier<>(actions, transform));
	}

	@Override
	public IHolder<T> transform(Function<T, T> transform) {
		if (transform == null) {
			throw new NullPointerException("Transform must be non-null");
		}

		// Queue the transform until we need to apply it
		actions.add(transform);

		return this;
	}

	@Override
	public <E> E unwrap(Function<T, E> unwrapper) {
		if (unwrapper == null) {
			throw new NullPointerException("Unwrapper must be null");
		}

		// Actualize ourselves
		if (heldValue == null) {
			heldValue = heldSource.get();
		}

		// Apply all pending transforms
		actions.forEach((action) -> heldValue = action.apply(heldValue));

		return unwrapper.apply(heldValue);
	}

	@Override
	public boolean isMaterialized() {
		if (heldSource != null) {
			// We're materialized if a value exists
			return heldValue == null;
		} else {
			// We're materialized by default
			return true;
		}
	}

	@Override
	public boolean hasPendingActions() {
		return actions.isEmpty();
	}

	@Override
	public void materialize() {
		// Only materialize if we haven't already
		if (!isMaterialized()) {
			heldValue = heldSource.get();
		}
	}

	@Override
	public void applyPendingActions() {
		materialize();
		
		actions.forEach((action) -> {
			heldValue = action.apply(heldValue);
		});
	}
}