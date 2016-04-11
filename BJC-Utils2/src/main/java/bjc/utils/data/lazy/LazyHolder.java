package bjc.utils.data.lazy;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import bjc.utils.data.IHolder;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IFunctionalList;

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
	private static final class LazyHolderHolder<T2>
			implements IHolder<T2> {
		private Supplier<IHolder<T2>>				holderSource;

		private IHolder<T2>							holder;

		private IFunctionalList<Function<T2, T2>>	actions	= new FunctionalList<>();

		public LazyHolderHolder(Supplier<IHolder<T2>> source) {

			holderSource = source;
		}

		@Override
		public void doWith(Consumer<T2> action) {
			actions.add((val) -> {
				action.accept(val);

				return val;
			});
		}

		@Override
		public <NewT> IHolder<NewT> map(Function<T2, NewT> transformer) {
			// TODO implement me
			throw new UnsupportedOperationException(
					"Mapping is not yet supported on bound holders");
		}

		@Override
		public IHolder<T2> transform(Function<T2, T2> transformer) {
			actions.add(transformer);

			return this;
		}

		@Override
		public <E> E unwrap(Function<T2, E> unwrapper) {
			if (holder == null) {
				holder = holderSource.get();
			}

			if (!actions.isEmpty()) {
				actions.forEach((transform) -> {
					holder.transform(transform);
				});
			}

			return holder.unwrap(unwrapper);
		}

		@Override
		public <E> IHolder<E> bind(Function<T2, IHolder<E>> binder) {
			return new LazyHolderHolder<>(() -> {
				return binder.apply(unwrap((val) -> val));
			});
		}

		@Override
		public String toString() {
			if (holderSource == null) {
				if (holder == null) {
					return "(null)";
				}

				return holder.toString();
			}

			if (holder == null) {
				return "(unmaterialized values)";
			}

			return holder.toString();
		}
	}

	private static final class LazyHolderSupplier<NewT, T2>
			implements Supplier<NewT> {
		private IFunctionalList<Function<T2, T2>>	pendingActions;
		private Function<T2, NewT>					pendingTransform;

		private T2									heldValue;
		private Supplier<T2>						heldSource;

		public LazyHolderSupplier(IFunctionalList<Function<T2, T2>> actons,
				Function<T2, NewT> transform, T2 heldValue,
				Supplier<T2> heldSource) {
			// Resolve latent bug I just realized. After a map, adding new
			// actions to the original holder could've resulted in changes
			// to all unactualized mapped values from that holder
			pendingActions = new FunctionalList<>();

			for (Function<T2, T2> action : actons.toIterable()) {
				pendingActions.add(action);
			}

			this.pendingTransform = transform;
			this.heldValue = heldValue;
			this.heldSource = heldSource;
		}

		@Override
		public NewT get() {
			if (heldValue == null) {
				return pendingActions.reduceAux(heldSource.get(),
						Function<T2, T2>::apply, pendingTransform::apply);
			}

			return pendingActions.reduceAux(heldValue,
					Function<T2, T2>::apply, pendingTransform::apply);
		}
	}

	/**
	 * List of queued actions to be performed on realized values
	 */
	private IFunctionalList<Function<T, T>>	actions	= new FunctionalList<>();

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
		return new LazyHolder<>(new LazyHolderSupplier<>(actions,
				transform, heldValue, heldSource));
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
		}

		// We're materialized by default
		return true;
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

	@Override
	public <T2> IHolder<T2> bind(Function<T, IHolder<T2>> binder) {
		return new LazyHolderHolder<>(() -> {
			return binder.apply(unwrap((val) -> val));
		});
	}

	@Override
	public String toString() {
		if (isMaterialized()) {
			if (hasPendingActions()) {
				return heldValue.toString() + " (has pending actions)";
			}

			return heldValue.toString();
		}

		return "(unmaterialized value)";
	}
}