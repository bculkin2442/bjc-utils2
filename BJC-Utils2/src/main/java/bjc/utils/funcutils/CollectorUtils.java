package bjc.utils.funcutils;

import bjc.utils.data.IHolder;
import bjc.utils.data.IPair;

import java.util.stream.Collector;

/**
 * Utilities for producing implementations of {@link Collector}
 *
 * @author ben
 *
 */
public class CollectorUtils {
	/**
	 * Create a collector that applies two collectors at once
	 *
	 * @param <InitialType>
	 *                The type of the collection to collect from
	 * @param <AuxType1>
	 *                The intermediate type of the first collector
	 * @param <AuxType2>
	 *                The intermediate type of the second collector
	 * @param <FinalType1>
	 *                The final type of the first collector
	 * @param <FinalType2>
	 *                The final type of the second collector
	 * @param first
	 *                The first collector to use
	 * @param second
	 *                The second collector to use
	 * @return A collector that functions as mentioned above
	 */
	public static <InitialType, AuxType1, AuxType2, FinalType1, FinalType2> Collector<InitialType, IHolder<IPair<AuxType1, AuxType2>>, IPair<FinalType1, FinalType2>> compoundCollect(
			Collector<InitialType, AuxType1, FinalType1> first,
			Collector<InitialType, AuxType2, FinalType2> second) {
		return new CompoundCollector<>(first, second);
	}
}
