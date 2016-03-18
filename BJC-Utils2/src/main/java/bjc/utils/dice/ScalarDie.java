package bjc.utils.dice;

/**
 * A die that represents a static number
 * 
 * @author ben
 *
 */
public class ScalarDie implements IDiceExpression {
	/**
	 * The represented number
	 */
	private int num;

	@Override
	public int roll() {
		return num;
	}

	/**
	 * Create a dice with the specified number
	 * 
	 * @param num
	 *            The number used for the dice
	 */
	public ScalarDie(int num) {
		this.num = num;
	}

	@Override
	public String toString() {
		return Integer.toString(num);
	}

}
