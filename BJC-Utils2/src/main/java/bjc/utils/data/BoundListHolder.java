package bjc.utils.data;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import bjc.utils.funcdata.IFunctionalList;

class BoundListHolder<ContainedType> implements IHolder<ContainedType> {
	private IFunctionalList<IHolder<ContainedType>> heldHolders;

	public BoundListHolder(
			IFunctionalList<IHolder<ContainedType>> toHold) {
		heldHolders = toHold;
	}

	@Override
	public <BoundType> IHolder<BoundType> bind(
			Function<ContainedType, IHolder<BoundType>> binder) {
		IFunctionalList<IHolder<BoundType>> boundHolders = heldHolders
				.map((containedHolder) -> {
					return containedHolder.bind(binder);
				});

		return new BoundListHolder<>(boundHolders);
	}

	@Override
	public <MappedType> IHolder<MappedType> map(
			Function<ContainedType, MappedType> mapper) {
		IFunctionalList<IHolder<MappedType>> mappedHolders = heldHolders
				.map((containedHolder) -> {
					return containedHolder.map(mapper);
				});

		return new BoundListHolder<>(mappedHolders);
	}

	@Override
	public IHolder<ContainedType> transform(
			UnaryOperator<ContainedType> transformer) {
		heldHolders.forEach((containedHolder) -> {
			containedHolder.transform(transformer);
		});

		return this;
	}

	@Override
	public <UnwrappedType> UnwrappedType unwrap(
			Function<ContainedType, UnwrappedType> unwrapper) {
		return heldHolders.randItem().unwrap(unwrapper);
	}

	@Override
	public <NewType> Function<ContainedType, IHolder<NewType>> lift(
			Function<ContainedType, NewType> func) {
		return (val) -> {
			return new ListHolder<>(func.apply(val));
		};
	}
}
