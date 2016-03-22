package bjc.utils.dice.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

import org.apache.commons.lang3.StringUtils;

import bjc.utils.data.Pair;
import bjc.utils.dice.ComplexDice;
import bjc.utils.dice.CompoundDice;
import bjc.utils.dice.IDiceExpression;
import bjc.utils.parserutils.AST;

public class DiceASTExpression implements IDiceExpression {
	private AST<IDiceASTNode>				ast;
	private Map<String, DiceASTExpression>	env;

	public DiceASTExpression(AST<IDiceASTNode> ast,
			Map<String, DiceASTExpression> env) {
		this.ast = ast;
		this.env = env;
	}

	private Pair<Integer, AST<IDiceASTNode>> evalLeaf(IDiceASTNode tokn) {
		if (tokn instanceof VariableDiceNode) {
			String varName = ((VariableDiceNode) tokn).getVariable();
			
			if (env.containsKey(varName)) {
				return new Pair<>(env.get(varName).roll(), new AST<>(tokn));
			} else {
				// Handle special case for defining variables
				return new Pair<>(0, new AST<>(tokn));
			}
		} else {
			LiteralDiceNode lnod = (LiteralDiceNode) tokn;
			String dat = lnod.getData();

			if (StringUtils.countMatches(dat, 'c') == 1
					&& !dat.equalsIgnoreCase("c")) {
				String[] strangs = dat.split("c");
				return new Pair<>(new CompoundDice(strangs).roll(),
						new AST<>(tokn));
			} else if (StringUtils.countMatches(dat, 'd') == 1
					&& !dat.equalsIgnoreCase("d")) {
				/*
				 * Handle dice groups
				 */
				return new Pair<>(ComplexDice.fromString(dat).roll(),
						new AST<>(tokn));
			} else {
				return new Pair<>(Integer.parseInt(dat), new AST<>(tokn));
			}
		}
	}

	private static
			Map<IDiceASTNode, BinaryOperator<Pair<Integer, AST<IDiceASTNode>>>>
			buildOperations(Map<String, DiceASTExpression> env) {
		Map<IDiceASTNode, BinaryOperator<Pair<Integer, AST<IDiceASTNode>>>> opCollapsers =
				new HashMap<>();

		opCollapsers.put(OperatorDiceNode.ADD, (left, right) -> {
			return left.merge((lval, last) -> right.merge((rval, rast) -> {
				return new Pair<>(lval + rval,
						new AST<>(OperatorDiceNode.ADD, last, rast));
			}));

		});
		opCollapsers.put(OperatorDiceNode.SUBTRACT, (left, right) -> {
			return left.merge((lval, last) -> right.merge((rval, rast) -> {
				return new Pair<>(lval - rval,
						new AST<>(OperatorDiceNode.SUBTRACT, last, rast));
			}));

		});
		opCollapsers.put(OperatorDiceNode.MULTIPLY, (left, right) -> {
			return left.merge((lval, last) -> right.merge((rval, rast) -> {
				return new Pair<>(lval * rval,
						new AST<>(OperatorDiceNode.MULTIPLY, last, rast));
			}));

		});
		opCollapsers.put(OperatorDiceNode.DIVIDE, (left, right) -> {
			return left.merge((lval, last) -> right.merge((rval, rast) -> {
				return new Pair<>(lval / rval,
						new AST<>(OperatorDiceNode.DIVIDE, last, rast));
			}));
		});

		opCollapsers.put(OperatorDiceNode.ASSIGN, (left, right) -> {
			return left.merge((lval, last) -> right.merge((rval, rast) -> {
				String nam = last.collapse((nod) -> {
					return ((VariableDiceNode) nod).getVariable();
				} , (v) -> (lv, rv) -> null, (r) -> r);

				env.put(nam, new DiceASTExpression(rast, env));

				return new Pair<>(rval,
						new AST<>(OperatorDiceNode.ASSIGN, last, rast));
			}));
		});

		opCollapsers.put(OperatorDiceNode.COMPOUND, (left, right) -> {
			return left.merge((lval, last) -> right.merge((rval, rast) -> {
				int ival = Integer.parseInt(
						Integer.toString(lval) + Integer.toString(rval));

				return new Pair<>(ival,
						new AST<>(OperatorDiceNode.COMPOUND, last, rast));
			}));
		});
		opCollapsers.put(OperatorDiceNode.GROUP, (left, right) -> {
			return left.merge((lval, last) -> right.merge((rval, rast) -> {

				return new Pair<>(new ComplexDice(lval, rval).roll(),
						new AST<>(OperatorDiceNode.GROUP, last, rast));
			}));
		});

		return opCollapsers;
	}

	@Override
	public int roll() {
		Map<IDiceASTNode, BinaryOperator<Pair<Integer, AST<IDiceASTNode>>>> operations =
				buildOperations(env);

		return ast.collapse(this::evalLeaf, operations::get,
				(r) -> r.merge((left, right) -> left));
	}

	@Override
	public String toString() {
		return ast.toString();
	}

	/**
	 * @return the ast
	 */
	public AST<IDiceASTNode> getAst() {
		return ast;
	}
}
