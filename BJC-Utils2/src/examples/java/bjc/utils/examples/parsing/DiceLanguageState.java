package bjc.utils.examples.parsing;

import java.util.Map;

import bjc.utils.data.Pair;
import bjc.utils.dice.DiceExpressionParser;
import bjc.utils.dice.IDiceExpression;

public class DiceLanguageState
		extends Pair<DiceExpressionParser, Map<String, IDiceExpression>> {

	public DiceLanguageState() {
	}

	public DiceLanguageState(DiceExpressionParser left,
			Map<String, IDiceExpression> right) {
		super(left, right);
	}
}
