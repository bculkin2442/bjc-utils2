package bjc.utils.examples.parsing;

import java.util.Map;

import bjc.utils.data.Pair;
import bjc.utils.dice.DiceExpressionParser;
import bjc.utils.dice.ast.DiceASTExpression;

public class DiceASTLanguageState
		extends Pair<DiceExpressionParser, Map<String, DiceASTExpression>> {

	public DiceASTLanguageState() {
	}

	public DiceASTLanguageState(DiceExpressionParser left,
			Map<String, DiceASTExpression> right) {
		super(left, right);
	}
}
