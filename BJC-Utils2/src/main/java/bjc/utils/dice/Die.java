package bjc.utils.dice;

import java.util.Random;

/**
 * A single polyhedral dice
 * 
 * @author ben
 *
 */
public class Die implements IDiceExpression {
	/**
	 * Random # gen to use for dice
	 */
	private static Random	rng	= new Random();

	/**
	 * Number of sides this die has
	 */
	private int				nSides;

	/**
	 * Create a die with the specified number of sides
	 * 
	 * @param nSides
	 *            The number of sides this dice has
	 */
	public Die(int nSides) {
		this.nSides = nSides;
	}

	/**
	 * Roll this dice once
	 * 
	 * @return The result of rolling the dice
	 */
	public int roll() {
		return rng.nextInt(nSides) + 1;
	}

	@Override
	public String toString() {
		return "d" + nSides;
	}
}
