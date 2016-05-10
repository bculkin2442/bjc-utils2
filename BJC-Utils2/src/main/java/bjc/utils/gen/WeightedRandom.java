package bjc.utils.gen;

import java.util.Random;

import bjc.utils.data.IHolder;
import bjc.utils.data.IPair;
import bjc.utils.data.Identity;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

/**
 * Represents a random number generator where certain results are weighted
 * more heavily than others.
 * 
 * @author ben
 *
 * @param <E>
 *            The type of values that are randomly selected.
 */
public class WeightedRandom<E> {
	/**
	 * The list of probabilities for each result
	 */
	private IList<Integer>	probabilities;

	/**
	 * The list of possible results to pick from
	 */
	private IList<E>			results;

	/**
	 * The source for any needed random numbers
	 */
	private Random						source;

	private int							totalChance;

	/**
	 * Create a new weighted random generator with the specified source of
	 * randomness
	 * 
	 * @param src
	 *            The source of randomness to use.
	 */
	public WeightedRandom(Random src) {
		probabilities = new FunctionalList<>();
		results = new FunctionalList<>();

		if (src == null) {
			throw new NullPointerException(
					"Source of randomness must not be null");
		}

		source = src;
	}

	/**
	 * Add a probability for a specific result to be given.
	 * 
	 * @param chance
	 *            The chance to get this result.
	 * @param result
	 *            The result to get when the chance comes up.
	 */
	public void addProbability(int chance, E result) {
		probabilities.add(chance);
		results.add(result);

		totalChance += chance;
	}

	/**
	 * Generate a weighted random value.
	 * 
	 * @return A random value selected in a weighted fashion.
	 */
	public E generateValue() {
		IHolder<Integer> randomValue = new Identity<>(
				source.nextInt(totalChance));
		IHolder<E> currentResult = new Identity<>();
		IHolder<Boolean> valuePicked = new Identity<>(true);

		probabilities.forEachIndexed((itemIndex, itemProbability) -> {
			if (valuePicked.unwrap(bool -> bool)) {
				if (randomValue
						.unwrap((number) -> number < itemProbability)) {
					currentResult.transform(
							(result) -> results.getByIndex(itemIndex));

					valuePicked.transform((bool) -> false);
				} else {
					randomValue.transform(
							(number) -> number - itemProbability);
				}
			}
		});

		return currentResult.unwrap((result) -> result);
	}

	/**
	 * Return a list of values that can be generated by this generator
	 * 
	 * @return A list of all the values that can be generated
	 */
	public IList<E> getResults() {
		return results;
	}

	/**
	 * Return a list containing values that can be generated paired with
	 * the probability of those values being generated
	 * 
	 * @return A list of pairs of values and value probabilities
	 */
	public IList<IPair<Integer, E>> getValues() {
		return probabilities.pairWith(results);
	}
}