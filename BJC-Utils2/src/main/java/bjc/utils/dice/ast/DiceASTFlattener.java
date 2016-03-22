package bjc.utils.dice.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

import org.apache.commons.lang3.StringUtils;

import bjc.utils.dice.BindingDiceExpression;
import bjc.utils.dice.ComplexDice;
import bjc.utils.dice.CompoundDice;
import bjc.utils.dice.CompoundDiceExpression;
import bjc.utils.dice.DiceExpressionType;
import bjc.utils.dice.IDiceExpression;
import bjc.utils.dice.ReferenceDiceExpression;
import bjc.utils.dice.ScalarDie;
import bjc.utils.parserutils.AST;

public class DiceASTFlattener {
	public static IDiceExpression flatten(AST<IDiceASTNode> ast,
			Map<String, IDiceExpression> env) {
		Map<IDiceASTNode, BinaryOperator<IDiceExpression>> opCollapsers = buildOperations(
				env);

		return ast.collapse((nod) -> {
			if (nod instanceof LiteralDiceNode) {
				return expFromLiteral((LiteralDiceNode) nod);
			} else {
				return new ReferenceDiceExpression(
						((VariableDiceNode) nod).getVariable(), env);
			}
		} , opCollapsers::get, (r) -> r);
	}

	private static Map<IDiceASTNode, BinaryOperator<IDiceExpression>> buildOperations(
			Map<String, IDiceExpression> env) {
		Map<IDiceASTNode, BinaryOperator<IDiceExpression>> opCollapsers = new HashMap<>();
		opCollapsers.put(OperatorDiceNode.ADD, (left, right) -> {
			return new CompoundDiceExpression(right, left,
					DiceExpressionType.ADD);
		});
		opCollapsers.put(OperatorDiceNode.SUBTRACT, (left, right) -> {
			return new CompoundDiceExpression(right, left,
					DiceExpressionType.SUBTRACT);
		});
		opCollapsers.put(OperatorDiceNode.MULTIPLY, (left, right) -> {
			return new CompoundDiceExpression(right, left,
					DiceExpressionType.MULTIPLY);
		});
		opCollapsers.put(OperatorDiceNode.DIVIDE, (left, right) -> {
			return new CompoundDiceExpression(right, left,
					DiceExpressionType.DIVIDE);
		});
		opCollapsers.put(OperatorDiceNode.ASSIGN, (left, right) -> {
			return new BindingDiceExpression(left, right, env);
		});
		opCollapsers.put(OperatorDiceNode.COMPOUND, (left, right) -> {
			return new CompoundDice(left, right);
		});
		opCollapsers.put(OperatorDiceNode.GROUP, (left, right) -> {
			return new ComplexDice(left, right);
		});

		return opCollapsers;
	}

	private static IDiceExpression expFromLiteral(LiteralDiceNode tok) {
		String data = tok.getData();

		if (StringUtils.countMatches(data, 'c') == 1
				&& !data.equalsIgnoreCase("c")) {
			String[] strangs = data.split("c");

			return new CompoundDice(ComplexDice.fromString(strangs[0]),
					ComplexDice.fromString(strangs[1]));
		} else if (StringUtils.countMatches(data, 'd') == 1
				&& !data.equalsIgnoreCase("d")) {
			return ComplexDice.fromString(data);
		} else {
			return new ScalarDie(Integer.parseInt(data));
		}
	}
}
