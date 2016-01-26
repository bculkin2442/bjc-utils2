package bjc.utils.dice;

public class CompoundDiceExpression implements DiceExpression {
	private DiceExpressionType	det;
	private DiceExpression		left;

	private DiceExpression		right;

	public CompoundDiceExpression(DiceExpression right,
			DiceExpression left, DiceExpressionType det) {
		this.right = right;
		this.left = left;
		this.det = det;
	}

	@Override
	public int roll() {
		switch (det) {
			case ADD:
				return right.roll() + left.roll();
			case SUBTRACT:
				return right.roll() - left.roll();
			case MULTIPLY:
				return right.roll() * left.roll();
			case DIVIDE:
				return Math.round(right.roll() / left.roll());
			default:
				throw new IllegalStateException(
						"Got passed  a invalid ScalarExpressionType "
								+ det);

		}
	}
}
