package bjc.utils.dice;

import java.util.Stack;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.FunctionalStringTokenizer;
import bjc.utils.parserutils.ShuntingYard;

public class DiceExpressionParser {
	public DiceExpression parse(String exp) {
		FunctionalStringTokenizer fst = new FunctionalStringTokenizer(exp);

		ShuntingYard<String> yard = new ShuntingYard<>();

		FunctionalList<String> ls = yard.postfix(fst.toList(s -> s),
				s -> s);

		Stack<DiceExpression> dexps = new Stack<>();

		ls.forEach((tok) -> {
			if (tok.contains("d")) {
				dexps.push(Dice.fromString(tok));
			} else {
				try {
					dexps.push(new ScalarDie(Integer.parseInt(tok)));
				} catch (NumberFormatException nfex) {

					DiceExpression l = dexps.pop();
					DiceExpression r = dexps.pop();

					switch (tok) {
						case "+":
							dexps.push(new CompoundDiceExpression(l, r,
									DiceExpressionType.ADD));
							break;
						case "-":
							dexps.push(new CompoundDiceExpression(l, r,
									DiceExpressionType.SUBTRACT));
							break;
						case "*":
							dexps.push(new CompoundDiceExpression(l, r,
									DiceExpressionType.MULTIPLY));
							break;
						case "/":
							dexps.push(new CompoundDiceExpression(l, r,
									DiceExpressionType.DIVIDE));
							break;
						default:
							throw new IllegalStateException("Detected invalid operator " + tok);
					}
				}
			}
		});

		return dexps.pop();
	}
}
