package bjc.utils.dice;

public class ScalarDiceExpression implements DiceExpression {
	private DiceExpressionType	det;
	private DiceExpression		exp;

	private int					scalar;

	public ScalarDiceExpression(DiceExpression dex, int scalr,
			DiceExpressionType dt) {
		exp = dex;
		scalar = scalr;
		det = dt;
	}

	@Override
	public int roll() {
		switch (det) {
			case ADD:
				return exp.roll() + scalar;
			case SUBTRACT:
				return exp.roll() - scalar;
			case MULTIPLY:
				return exp.roll() * scalar;
			case DIVIDE:
				return Math.round(exp.roll() / scalar);
			default:
				throw new IllegalStateException(
						"Got passed  a invalid ScalarExpressionType "
								+ det);
		}
	}
}
