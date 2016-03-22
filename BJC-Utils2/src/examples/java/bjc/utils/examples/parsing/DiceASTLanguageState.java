package bjc.utils.examples.parsing;

import java.util.Map;

import bjc.utils.data.Pair;
import bjc.utils.dice.DiceExpressionParser;
import bjc.utils.dice.ast.DiceASTExpression;

/**
 * Internal state of the AST-based dice langugae
 * @author ben
 *
 */
public class DiceASTLanguageState
		extends Pair<DiceExpressionParser, Map<String, DiceASTExpression>> {

	/**
	 * Create a new state
	 */
	public DiceASTLanguageState() {
	}

	/**
	 * Create a new state with the given contents
	 * @param left The parser to use
	 * @param right The enviroment to use
	 */
	public DiceASTLanguageState(DiceExpressionParser left,
			Map<String, DiceASTExpression> right) {
		super(left, right);
	}
}
