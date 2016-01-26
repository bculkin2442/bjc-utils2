package bjc.utils.dice;

public class Dice implements DiceExpression {
	private Die	die;
	private int	nDice;

	public Dice(int nDce, Die de) {
		nDice = nDce;
		die = de;
	}

	public Dice(int nDce, int nSides) {
		this(nDce, new Die(nSides));
	}

	public int roll() {
		int res = 0;

		for (int i = 0; i < nDice; i++) {
			res += die.roll();
		}

		return res;
	}

	public static Dice fromString(String dice) {
		String[] strangs = dice.split("d");

		try {
			return new Dice(Integer.parseInt(strangs[0]),
					Integer.parseInt(strangs[1]));
		} catch (NumberFormatException nfex) {
			throw new IllegalStateException(
					"Attempted to create a dice using something that's not"
							+ " an integer: " + strangs[0] + " and "
							+ strangs[1] + " are likely culprits.s");
		}
	}
}
