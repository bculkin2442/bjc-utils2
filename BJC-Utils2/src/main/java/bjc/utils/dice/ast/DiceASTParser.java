package bjc.utils.dice.ast;

import org.apache.commons.lang3.StringUtils;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.FunctionalStringTokenizer;
import bjc.utils.parserutils.AST;
import bjc.utils.parserutils.ShuntingYard;
import bjc.utils.parserutils.TreeConstructor;

public class DiceASTParser {
	private static ShuntingYard<String> yard;

	static {
		yard = new ShuntingYard<>();

		yard.addOp("d", 5); // dice operator: use for creating variable
		// size dice groups
		yard.addOp("c", 6); // compound operator: use for creating compound
		// dice from expressions
		yard.addOp(":=", 0); // binding operator: Bind a name to a variable
		// expression
	}

	public AST<IDiceASTNode> buildAST(String exp) {
		FunctionalList<String> tokens = FunctionalStringTokenizer
				.fromString(exp).toList((s) -> s);

		FunctionalList<String> shunted = yard.postfix(tokens, (s) -> s);

		AST<String> rawAST = TreeConstructor.constructTree(shunted,
				this::isOperator);

		AST<IDiceASTNode> bakedAST = rawAST.transmuteAST((tok) -> {
			if (isOperator(tok)) {
				return OperatorDiceNode.fromString(tok);
			} else if (isLiteral(tok)) {
				return new LiteralDiceNode(tok);
			} else {
				return new VariableDiceNode(tok);
			}
		});

		return bakedAST;
	}

	private boolean isOperator(String tok) {
		switch (tok) {
			case ":=":
			case "+":
			case "-":
			case "*":
			case "/":
			case "c":
			case "d":
				return true;
			default:
				return false;
		}
	}

	private boolean isLiteral(String tok) {
		if (StringUtils.countMatches(tok, 'c') == 1
				&& !tok.equalsIgnoreCase("c")) {
			return true;
		} else if (StringUtils.countMatches(tok, 'd') == 1
				&& !tok.equalsIgnoreCase("d")) {
			return true;
		} else {
			try {
				Integer.parseInt(tok);
				return true;
			} catch (NumberFormatException nfx) {
				return false;
			}
		}
	}
}
