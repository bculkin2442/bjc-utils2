package bjc.utils.funcutils;

import bjc.utils.data.IHolder;
import bjc.utils.data.IPair;
import bjc.utils.data.Identity;
import bjc.utils.data.Pair;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

final class CompoundCollector<InitialType, AuxType1, AuxType2, FinalType1, FinalType2>
		implements Collector<InitialType, IHolder<IPair<AuxType1, AuxType2>>, IPair<FinalType1, FinalType2>> {

	private Set<java.util.stream.Collector.Characteristics> characteristicSet;

	private Collector<InitialType, AuxType1, FinalType1>	first;
	private Collector<InitialType, AuxType2, FinalType2>	second;

	public CompoundCollector(Collector<InitialType, AuxType1, FinalType1> first,
			Collector<InitialType, AuxType2, FinalType2> second) {
		this.first = first;
		this.second = second;

		characteristicSet = first.characteristics();
		characteristicSet.addAll(second.characteristics());
	}

	@Override
	public BiConsumer<IHolder<IPair<AuxType1, AuxType2>>, InitialType> accumulator() {
		BiConsumer<AuxType1, InitialType> firstAccumulator = first.accumulator();
		BiConsumer<AuxType2, InitialType> secondAccumulator = second.accumulator();

		return (state, value) -> {
			state.doWith((statePair) -> {
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
	public BinaryOperator<IHolder<IPair<AuxType1, AuxType2>>> combiner() {
		BinaryOperator<AuxType1> firstCombiner = first.combiner();
		BinaryOperator<AuxType2> secondCombiner = second.combiner();

		return (leftState, rightState) -> {
			return leftState.unwrap((leftPair) -> {
				return rightState.transform((rightPair) -> {
					return leftPair.combine(rightPair, firstCombiner, secondCombiner);
				});
			});
		};
	}

	@Override
	public Function<IHolder<IPair<AuxType1, AuxType2>>, IPair<FinalType1, FinalType2>> finisher() {
		return (state) -> {
			return state.unwrap((pair) -> {
				return pair.bind((left, right) -> {
					return new Pair<>(first.finisher().apply(left), second.finisher().apply(right));
				});
			});
		};
	}

	@Override
	public Supplier<IHolder<IPair<AuxType1, AuxType2>>> supplier() {
		return () -> {
			return new Identity<>(new Pair<>(first.supplier().get(), second.supplier().get()));
		};
	}
}
