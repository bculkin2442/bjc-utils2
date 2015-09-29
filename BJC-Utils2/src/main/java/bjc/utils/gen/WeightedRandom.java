package bjc.utils.gen;

import java.util.Random;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.data.GenHolder;

/**
 * Represents a random number generator where certain results are weighted more heavily than
 * 		others.
 * @author ben
 *
 * @param <E> The type of values that are randomly selected.
 */
public class WeightedRandom<E> {
	private Random src;

	private FunctionalList<Integer>	probs;
	private FunctionalList<E>		results;

	private int totalChance;

	/**
	 * Create a new weighted random generator with the specified source of randomness
	 * @param sr The source of randomness to use.
	 */
	public WeightedRandom(Random sr) {
		probs = new FunctionalList<>();
		results = new FunctionalList<>();

		src = sr;
	}

	/**
	 * Add a probability for a specific result to be given.
	 * @param chance The chance to get this result.
	 * @param res The result to get when the chance comes up.
	 */
	public void addProb(int chance, E res) {
		probs.add(chance);
		results.add(res);

		totalChance += chance;
	}

	/**
	 * Generate a weighted random value.
	 * @return A random value selected in a weighted fashion.
	 */
	public E genVal() {
		GenHolder<Integer> v = new GenHolder<>(src.nextInt(totalChance));
		GenHolder<E> res = new GenHolder<E>();
		GenHolder<Boolean> bl = new GenHolder<>(true);

		probs.forEachIndexed((i, p) -> {
			if (bl.held) {
				if (v.held < p) {
					res.held = results.getByIndex(i);
					bl.held = false;
				} else {
					v.held -= p;
				}
			}
		});

		return res.held;
	}
}
