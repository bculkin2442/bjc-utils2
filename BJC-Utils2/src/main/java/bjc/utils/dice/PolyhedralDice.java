package bjc.utils.dice;

/**
 * Class that produces common polyhedral dice
 * 
 * @author ben
 *
 */
public class PolyhedralDice {
	/**
	 * Produce the specified number of 10-sided dice
	 * 
	 * @param nDice
	 *            The number of ten-sided dice to produce
	 * @return A group of ten-sided dice of the specified size
	 */
	public static LazyDice d10(int nDice) {
		return new LazyDice(nDice, 10);
	}

	/**
	 * Produce the specified number of 100-sided dice
	 * 
	 * @param nDice
	 *            The number of hundred-sided dice to produce
	 * @return A group of hundred-sided dice of the specified size
	 */
	public static LazyDice d100(int nDice) {
		return new LazyDice(nDice, 100);
	}

	/**
	 * Produce the specified number of 12-sided dice
	 * 
	 * @param nDice
	 *            The number of twelve-sided dice to produce
	 * @return A group of twelve-sided dice of the specified size
	 */
	public static LazyDice d12(int nDice) {
		return new LazyDice(nDice, 12);
	}

	/**
	 * Produce the specified number of 20-sided dice
	 * 
	 * @param nDice
	 *            The number of twenty-sided dice to produce
	 * @return A group of twenty-sided dice of the specified size
	 */
	public static LazyDice d20(int nDice) {
		return new LazyDice(nDice, 20);
	}

	/**
	 * Produce the specified number of 10-sided dice
	 * 
	 * @param nDice
	 *            The number of ten-sided dice to produce
	 * @return A group of ten-sided dice of the specified size
	 */
	public static LazyDice d4(int nDice) {
		return new LazyDice(nDice, 4);
	}

	/**
	 * Produce the specified number of 10-sided dice
	 * 
	 * @param nDice
	 *            The number of ten-sided dice to produce
	 * @return A group of ten-sided dice of the specified size
	 */
	public static LazyDice d6(int nDice) {
		return new LazyDice(nDice, 6);
	}

	/**
	 * Produce the specified number of 10-sided dice
	 * 
	 * @param nDice
	 *            The number of ten-sided dice to produce
	 * @return A group of ten-sided dice of the specified size
	 */
	public static LazyDice d8(int nDice) {
		return new LazyDice(nDice, 8);
	}
}
