package bjc.utils.dice;

import java.util.Stack;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.FunctionalStringTokenizer;
import bjc.utils.parserutils.ShuntingYard;

/**
 * Parse a dice expression from a string
 * 
 * @author ben
 *
 */
public class DiceExpressionParser {
	/**
	 * Parse a dice expression from a string
	 * 
	 * @param exp
	 *            The string to parse an expression from
	 * @return The parsed dice expression
	 */
	public IDiceExpression parse(String exp) {
		/*
		 * Create a tokenizer over the strings
		 */
		FunctionalStringTokenizer fst = new FunctionalStringTokenizer(exp);

		/*
		 * Create a shunter to rewrite the expression
		 */
		ShuntingYard<String> yard = new ShuntingYard<>();

		/*
		 * Add our custom operators to the yard
		 */
		yard.addOp("d", 5); // dice operator: use for creating variable
							// size dice groups
		yard.addOp("c", 6); // compound operator: use for creating compound
							// dice from expressions

		/*
		 * Shunt the expression to postfix form
		 */
		FunctionalList<String> ls = yard.postfix(fst.toList(s -> s),
				s -> s);

		/*
		 * Create a stack for building an expression from parts
		 */
		Stack<IDiceExpression> dexps = new Stack<>();

		/*
		 * Create the expression from parts
		 */
		ls.forEach((tok) -> {
			/*
			 * Handle compound dice
			 */
			if (tok.contains("c") && !tok.equalsIgnoreCase("c")) {
				String[] strangs = tok.split("c");

				dexps.push(new CompoundDice(LazyDice.fromString(strangs[0]),
						LazyDice.fromString(strangs[1])));
			} else if (tok.contains("d") && !tok.equalsIgnoreCase("d")) {
				/*
				 * Handle dice groups
				 */
				dexps.push(LazyDice.fromString(tok));
			} else {
				try {
					/*
					 * Handle scalar numbers
					 */
					dexps.push(new ScalarDie(Integer.parseInt(tok)));
				} catch (NumberFormatException nfex) {

					/*
					 * Apply an operation to two dice
					 */
					IDiceExpression l = dexps.pop();
					IDiceExpression r = dexps.pop();

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
						case "c":
							dexps.push(new CompoundDice(l, r));
							break;
						case "d":
							dexps.push(new LazyDice(l, r));
							break;
						default:
							/*
							 * Tell the user the operator is invalid
							 */
							throw new IllegalStateException(
									"Detected invalid operator " + tok);
					}
				}
			}
		});

		/*
		 * Return the built expression
		 */
		return dexps.pop();
	}
}
