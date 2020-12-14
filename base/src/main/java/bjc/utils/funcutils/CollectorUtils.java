package bjc.utils.funcutils;

import java.util.stream.Collector;

import bjc.data.Holder;
import bjc.data.Pair;

/**
 * Utilities for producing implementations of {@link Collector}
 *
 * @author ben
 */
public class CollectorUtils {
	/**
	 * Create a collector that applies two collectors at once.
	 *
	 * @param <InitialType>
	 *                      The type of the collection to collect from.
	 *
	 * @param <AuxType1>
	 *                      The intermediate type of the first collector.
	 *
	 * @param <AuxType2>
	 *                      The intermediate type of the second collector.
	 *
	 * @param <FinalType1>
	 *                      The final type of the first collector.
	 *
	 * @param <FinalType2>
	 *                      The final type of the second collector.
	 *
	 * @param first
	 *                      The first collector to use.
	 *
	 * @param second
	 *                      The second collector to use.
	 *
	 * @return A collector that functions as mentioned above.
	 */
	public static <InitialType, AuxType1, AuxType2, FinalType1, FinalType2>
			Collector<InitialType, Holder<Pair<AuxType1, AuxType2>>,
					Pair<FinalType1, FinalType2>>
			compoundCollect(final Collector<InitialType, AuxType1, FinalType1> first,
					final Collector<InitialType, AuxType2, FinalType2> second) {
		return new CompoundCollector<>(first, second);
	}
}
