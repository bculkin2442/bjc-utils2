package bjc.utils.dice;

import static bjc.utils.dice.DiceExpressionType.*;

public class DiceExpressionBuilder {
	private DiceExpression baking;

	public DiceExpressionBuilder(Dice firstDice) {
		baking = firstDice;
	}

	public DiceExpressionBuilder(DiceExpression seed) {
		baking = seed;
	}

	public DiceExpressionBuilder(int nSides, int nDice) {
		baking = new Dice(nSides, nDice);
	}

	public DiceExpressionBuilder add(DiceExpression exp) {
		baking = new CompoundDiceExpression(baking, exp, ADD);
		return this;
	}

	public DiceExpressionBuilder add(int num) {
		baking = new ScalarDiceExpression(baking, num, ADD);
		return this;
	}

	public DiceExpression bake() {
		return baking;
	}

	public DiceExpressionBuilder divide(DiceExpression exp) {
		baking = new CompoundDiceExpression(baking, exp, DIVIDE);
		return this;
	}

	public DiceExpressionBuilder divide(int num) {
		baking = new ScalarDiceExpression(baking, num, DIVIDE);
		return this;
	}

	public DiceExpressionBuilder multiply(DiceExpression exp) {
		baking = new CompoundDiceExpression(baking, exp, MULTIPLY);
		return this;
	}

	public DiceExpressionBuilder multiply(int num) {
		baking = new ScalarDiceExpression(baking, num, MULTIPLY);
		return this;
	}

	public DiceExpressionBuilder subtract(DiceExpression exp) {
		baking = new CompoundDiceExpression(baking, exp, SUBTRACT);
		return this;
	}

	public DiceExpressionBuilder subtract(int num) {
		baking = new ScalarDiceExpression(baking, num, SUBTRACT);
		return this;
	}
}
