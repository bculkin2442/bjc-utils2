/**
 * 
 */
package bjc.utils.math;

import java.util.HashMap;
import java.util.Map;

import bjc.esodata.SimpleStack;
import bjc.esodata.Stack;
import bjc.esodata.Stack.StackUnderflow;
import bjc.utils.exceptions.InvalidToken;
import bjc.utils.exceptions.NonConstantPower;
import bjc.utils.exceptions.OperandsRemaining;
import bjc.utils.math.DualExpr.ExprType;

/**
 * Create DualExprs from strings.
 * 
 * @author Ben Culkin
 *
 */
public class DualExprParser {
	/**
	 * Result class from parsing exprs.
	 * @author Ben Culkin
	 *
	 */
	public static class Result {
		/**
		 * The resulting expression. 
		 */
		public DualExpr expr;
		/**
		 * Any variables we found in the expression.
		 */
		public Map<String, DualExpr> varMap;

		/**
		 * Create a new result.
		 */
		public Result() {
			this.varMap = new HashMap<>();
		}
	}

	/**
	 * Parses a dual expression from a postfix expression string.
	 * 
	 * @param expr
	 *             The string to parse the dual expression from.
	 * 
	 * @return Both the parsed expression, and a map of all the variables used
	 */
	public static Result parseExpression(String expr) {
		return parseExpression(expr, null);
	}

	/**
	 * Parses a dual expression from a postfix expression string.
	 * 
	 * @param expr
	 *                The string to parse the dual expression from.
	 *
	 * @param preVars
	 *                Any pre-existing variables to use.
	 * 
	 * @return Both the parsed expression, and a map of all the variables used
	 * 
	 * @throws StackUnderflow
	 *                                 If the expression is not properly formatted.
	 */
	public static Result parseExpression(String expr, Map<String, DualExpr> preVars) {
		Result res = new Result();

		if (preVars == null) {
		} else {
			res.varMap = preVars;
		}

		Map<String, DualExpr> vars = res.varMap;

		String[] tokens = expr.split(" ");

		Stack<DualExpr> exprStack = new SimpleStack<>();

		for (int idx = 0; idx < tokens.length; idx++) {
			String token = tokens[idx];

			switch (token) {
			case "add":
			case "+": {
				DualExpr rhs = exprStack.pop();
				DualExpr lhs = exprStack.pop();

				exprStack.push(new DualExpr(ExprType.ADDITION, lhs, rhs));
				break;
			}
			case "subtract":
			case "-": {
				DualExpr rhs = exprStack.pop();
				DualExpr lhs = exprStack.pop();

				exprStack.push(new DualExpr(ExprType.SUBTRACTION, lhs, rhs));
				break;
			}
			case "multiply":
			case "*": {
				DualExpr rhs = exprStack.pop();
				DualExpr lhs = exprStack.pop();

				exprStack.push(new DualExpr(ExprType.MULTIPLICATION, lhs, rhs));
				break;
			}
			case "divide":
			case "/": {
				DualExpr rhs = exprStack.pop();
				DualExpr lhs = exprStack.pop();

				exprStack.push(new DualExpr(ExprType.DIVISION, lhs, rhs));
				break;
			}
			case "abs": {
				DualExpr opr = exprStack.pop();

				exprStack.push(new DualExpr(ExprType.ABSOLUTE, opr));
				break;
			}
			case "log": {
				DualExpr opr = exprStack.pop();

				exprStack.push(new DualExpr(ExprType.LOGARITHM, opr));
				break;
			}
			case "sin": {
				DualExpr opr = exprStack.pop();

				exprStack.push(new DualExpr(ExprType.SIN, opr));
				break;
			}
			case "cos": {
				DualExpr opr = exprStack.pop();

				exprStack.push(new DualExpr(ExprType.COS, opr));
				break;
			}
			case "exp": {
				DualExpr opr = exprStack.pop();

				exprStack.push(new DualExpr(ExprType.EXPONENTIAL, opr));
				break;
			}
			case "pow": {
				DualExpr pow = exprStack.pop();
				DualExpr bod = exprStack.pop();

				{
					Dual val = pow.number;
					if (val.dual != 0)
						throw new NonConstantPower();
				}

				exprStack.push(new DualExpr(ExprType.POWER, bod, pow));
				break;
			}
			case "eval": {
				DualExpr exp = exprStack.pop();

				exprStack.push(new DualExpr(exp.evaluate()));
				break;
			}
			case "dual": {
				DualExpr dual = exprStack.pop();
				DualExpr real = exprStack.pop();

				exprStack.push(new DualExpr(
						new Dual(real.evaluate().real, dual.evaluate().real)));
				break;
			}
			default:
				if (token.matches("[a-zA-Z][a-zA-Z0-9]*")) {
					if (vars.containsKey(token)) {
						exprStack.push(vars.get(token));
					} else {
						Dual var = new Dual();
						DualExpr varExpr = new DualExpr(var);

						vars.put(token, varExpr);

						exprStack.push(varExpr);
					}
				} else {
					try {
						double d = Double.parseDouble(token);

						exprStack.push(new DualExpr(new Dual(d)));
					} catch (NumberFormatException nfex) {
						throw new InvalidToken(token);
					}
				}

				break;
			}
		}

		if (res.expr == null) {
			res.expr = exprStack.pop();
		}

		if (exprStack.size() > 0)
			throw new OperandsRemaining(String.format(
					"After processing expression, not all values had been consumed.\n\tRemaining values are '%s'",
					exprStack.toString()));

		return res;
	}
}