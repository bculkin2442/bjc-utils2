package bjc.utils.funcutils;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import bjc.data.Holder;
import bjc.data.Pair;
import bjc.data.Identity;
import bjc.data.SimplePair;

/**
 * Implementation of a collecter that uses two collectors.
 *
 * @author Ben Culkin
 */
final class CompoundCollector<InitialType, AuxType1, AuxType2, FinalType1, FinalType2>
		implements Collector<InitialType, Holder<Pair<AuxType1, AuxType2>>,
				Pair<FinalType1, FinalType2>> {
	/* Our characteristics. */
	private final Set<java.util.stream.Collector.Characteristics> characteristicSet;

	/* The first collector. */
	private final Collector<InitialType, AuxType1, FinalType1> first;
	/* The second collector. */
	private final Collector<InitialType, AuxType2, FinalType2> second;

	/**
	 * Create a collector that uses two collectors.
	 *
	 * @param first
	 *               The first collector.
	 *
	 * @param second
	 *               The second collector.
	 */
	public CompoundCollector(final Collector<InitialType, AuxType1, FinalType1> first,
			final Collector<InitialType, AuxType2, FinalType2> second) {
		this.first = first;
		this.second = second;

		/* Accumulate characteristics. */
		characteristicSet = first.characteristics();
		characteristicSet.addAll(second.characteristics());
	}

	@Override
	public BiConsumer<Holder<Pair<AuxType1, AuxType2>>, InitialType> accumulator() {
		final BiConsumer<AuxType1, InitialType> firstAccumulator = first.accumulator();
		final BiConsumer<AuxType2, InitialType> secondAccumulator = second.accumulator();

		return (state, value) -> {
			state.doWith(statePair -> {
				statePair.doWith((left, right) -> {
					firstAccumulator.accept(left, value);
					secondAccumulator.accept(right, value);
				});
			});
		};
	}

	@Override
	public Set<java.util.stream.Collector.Characteristics> characteristics() {
		return characteristicSet;
	}

	@Override
	public BinaryOperator<Holder<Pair<AuxType1, AuxType2>>> combiner() {
		final BinaryOperator<AuxType1> firstCombiner = first.combiner();
		final BinaryOperator<AuxType2> secondCombiner = second.combiner();

		return (leftState, rightState) -> leftState.unwrap(leftPair -> {
			return rightState.transform(rightPair -> {
				return leftPair.combine(rightPair, firstCombiner, secondCombiner);
			});
		});
	}

	@Override
	public Function<Holder<Pair<AuxType1, AuxType2>>, Pair<FinalType1, FinalType2>>
			finisher() {
		return state -> state.unwrap(pair -> {
			return pair.bind((left, right) -> {
				final FinalType1 finalLeft = first.finisher().apply(left);
				final FinalType2 finalRight = second.finisher().apply(right);

				return new SimplePair<>(finalLeft, finalRight);
			});
		});
	}

	@Override
	public Supplier<Holder<Pair<AuxType1, AuxType2>>> supplier() {
		return () -> {
			final AuxType1 initialLeft = first.supplier().get();
			final AuxType2 initialRight = second.supplier().get();

			return new Identity<>(new SimplePair<>(initialLeft, initialRight));
		};
	}
}
