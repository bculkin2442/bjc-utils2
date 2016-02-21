package bjc.utils.dice;

/**
 * Implements a collection of one or more of a particular die, where the
 * number of dice in the group is variable.
 * 
 * @author ben
 *
 */
public class LazyDice implements IDiceExpression {
	/**
	 * The die being rolled
	 */
	private IDiceExpression	die;

	/**
	 * The number of the specified die to roll
	 */
	private IDiceExpression	nDice;

	/**
	 * Create a new collection of dice
	 * 
	 * @param nDce
	 *            The number of dice in the collection
	 * @param de
	 *            The type of dice the collection is composed of
	 */
	public LazyDice(IDiceExpression nDce, IDiceExpression de) {
		nDice = nDce;
		die = de;
	}

	/**
	 * Create a new collection of dice
	 * 
	 * @param nDce
	 *            The number of dice in the collection
	 * @param de
	 *            The type of dice the collection is composed of
	 */
	public LazyDice(int nSides, int de) {
		nDice = new ScalarDie(nSides);
		die = new Die(de);
	}

	@Override
	public int roll() {
		int res = 0;

		/*
		 * Add the results of rolling each die
		 */
		int nRoll = nDice.roll();

		for (int i = 0; i < nRoll; i++) {
			res += die.roll();
		}

		return res;
	}

	/**
	 * Create a dice from a string expression
	 * 
	 * @param dice
	 *            The string to parse the dice from
	 * @return A dice group parsed from the string
	 */
	public static LazyDice fromString(String dice) {
		/*
		 * Split it on the dice type marker
		 */
		String[] strangs = dice.split("d");

		try {
			/*
			 * Create the actual dice
			 */
			return new LazyDice(
					new ScalarDie(Integer.parseInt(strangs[0])),
					new Die(Integer.parseInt(strangs[1])));
		} catch (NumberFormatException nfex) {
			/*
			 * Tell the user the expression is invalid
			 */
			throw new IllegalStateException(
					"Attempted to create a dice using something that's not"
							+ " an integer: " + strangs[0] + " and "
							+ strangs[1] + " are likely culprits.");
		}
	}
}
