package bjc.utils.dice.ast;

import java.util.Map;

import bjc.utils.parserutils.AST;

public class DiceASTFreezer {
	public static AST<IDiceASTNode> freezeAST(AST<IDiceASTNode> tree,
			Map<String, AST<IDiceASTNode>> env) {
		return tree.collapse((nod) -> {
			if (nod instanceof VariableDiceNode) {
				return expandNode((VariableDiceNode) nod, env);
			} else {
				// Type is specified here so compiler can know the type
				// we're using
				return new AST<IDiceASTNode>(nod);
			}
		} , (op) -> (left, right) -> {
			return new AST<IDiceASTNode>(op, left, right);
		} , (r) -> r);
	}

	public static AST<IDiceASTNode> freezeAST(DiceASTExpression tree,
			Map<String, DiceASTExpression> env) {
		return tree.getAst().collapse((nod) -> {
			if (nod instanceof VariableDiceNode) {
				return expandNode2((VariableDiceNode) nod, env);
			} else {
				// Type is specified here so compiler can know the type
				// we're using
				return new AST<IDiceASTNode>(nod);
			}
		} , (op) -> (left, right) -> {
			return new AST<IDiceASTNode>(op, left, right);
		} , (r) -> r);
	}

	private static AST<IDiceASTNode> expandNode(VariableDiceNode vnode,
			Map<String, AST<IDiceASTNode>> env) {
		return env.get(vnode.getVariable());
	}

	private static AST<IDiceASTNode> expandNode2(VariableDiceNode vnode,
			Map<String, DiceASTExpression> env) {
		return env.get(vnode.getVariable()).getAst();
	}
}
