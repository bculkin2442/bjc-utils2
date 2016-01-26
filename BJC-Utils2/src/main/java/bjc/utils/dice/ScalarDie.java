package bjc.utils.dice;

public class ScalarDie implements DiceExpression {
	private int num;

	@Override
	public int roll() {
		return num;
	}

	public ScalarDie(int num) {
		this.num = num;
	}

}
