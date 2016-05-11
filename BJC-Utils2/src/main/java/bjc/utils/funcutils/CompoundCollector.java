package bjc.utils.funcutils;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

import bjc.utils.data.IHolder;
import bjc.utils.data.IPair;
import bjc.utils.data.Identity;
import bjc.utils.data.Pair;

final class CompoundCollector<InitialType, AuxType1, AuxType2, FinalType1, FinalType2>
		implements
		Collector<InitialType, IHolder<IPair<AuxType1, AuxType2>>, IPair<FinalType1, FinalType2>> {
	private Set<java.util.stream.Collector.Characteristics>	characteristicSet;

	private Collector<InitialType, AuxType1, FinalType1>	firstCollector;
	private Collector<InitialType, AuxType2, FinalType2>	secondCollector;

	public CompoundCollector(
			Collector<InitialType, AuxType1, FinalType1> firstCollector,
			Collector<InitialType, AuxType2, FinalType2> secondCollector) {
		this.firstCollector = firstCollector;
		this.secondCollector = secondCollector;

		characteristicSet = firstCollector.characteristics();
		characteristicSet.addAll(secondCollector.characteristics());
	}

	@Override
	public Supplier<IHolder<IPair<AuxType1, AuxType2>>> supplier() {
		return () -> new Identity<>(
				new Pair<>(firstCollector.supplier().get(),
						secondCollector.supplier().get()));
	}

	@Override
	public BiConsumer<IHolder<IPair<AuxType1, AuxType2>>, InitialType>
			accumulator() {
		BiConsumer<AuxType1, InitialType> firstAccumulator =
				firstCollector.accumulator();
		BiConsumer<AuxType2, InitialType> secondAccumulator =
				secondCollector.accumulator();

		return (state, value) -> {
			state.doWith((statePair) -> {
				statePair.doWith((leftState, rightState) -> {
					firstAccumulator.accept(leftState, value);
					secondAccumulator.accept(rightState, value);
				});
			});
		};
	}

	@Override
	public BinaryOperator<IHolder<IPair<AuxType1, AuxType2>>>
			combiner() {
		BinaryOperator<AuxType1> firstCombiner =
				firstCollector.combiner();
		BinaryOperator<AuxType2> secondCombiner =
				secondCollector.combiner();

		return (leftState, rightState) -> {
			return leftState.unwrap((leftPair) -> {
				return rightState.transform((rightPair) -> {
					return leftPair.combine(rightPair, firstCombiner,
							secondCombiner);
				});
			});
		};
	}

	@Override
	public Function<IHolder<IPair<AuxType1, AuxType2>>, IPair<FinalType1, FinalType2>>
			finisher() {
		return (state) -> {
			return state.unwrap((pair) -> {
				return pair.bind((leftVal, rightVal) -> {
					return new Pair<>(
							firstCollector.finisher().apply(leftVal),
							secondCollector.finisher()
									.apply(rightVal));
				});
			});
		};
	}

	@Override
	public Set<java.util.stream.Collector.Characteristics>
			characteristics() {
		return characteristicSet;
	}
}