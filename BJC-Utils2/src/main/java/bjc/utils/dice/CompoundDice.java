package bjc.utils.dice;

/**
 * Implements a "compound dice"
 * 
 * To explain, a compound dice is something like a d100 composed from two
 * d10s instead of a hundred sided die.
 * 
 * @author ben
 *
 */
public class CompoundDice implements IDiceExpression {
	/**
	 * The left die of the expression
	 */
	private IDiceExpression	l;
	/**
	 * The right die of the expression
	 */
	private IDiceExpression	r;

	/**
	 * Create a new compound dice using the specified dice
	 * 
	 * @param l
	 *            The die to use on the left
	 * @param r
	 *            The die to use on the right
	 */
	public CompoundDice(IDiceExpression l, IDiceExpression r) {
		this.l = l;
		this.r = r;
	}

	@Override
	public int roll() {
		/*
		 * Make the combination of the two dice
		 */
		return Integer.parseInt(l.roll() + "" + r.roll());
	}

	@Override
	public String toString() {
		return "compound[l=" + l.toString() + ", r=" + r.toString() + "]";
	}
}
