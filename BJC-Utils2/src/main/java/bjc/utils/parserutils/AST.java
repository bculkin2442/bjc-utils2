package bjc.utils.parserutils;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;

import bjc.utils.funcdata.ITreePart.TreeLinearizationMethod;

/**
 * A simple binary tree meant for use as an AST
 * 
 * @author ben
 *
 * @param <T>
 *            The type of token in this AST
 */
public class AST<T> {
	private T		token;

	private AST<T>	left;
	private AST<T>	right;

	/**
	 * Create a new leaf AST node
	 * 
	 * @param tokn
	 *            The token in this node
	 */
	public AST(T tokn) {
		token = tokn;

		left = null;
		right = null;
	}

	/**
	 * Create a new AST node with the specified data and children
	 * 
	 * @param tokn
	 *            The token in this node
	 * @param left
	 *            The left child of this AST
	 * @param right
	 *            The right child of this AST
	 */
	public AST(T tokn, AST<T> lft, AST<T> rght) {
		token = tokn;

		left = lft;
		right = rght;
	}

	public void traverse(TreeLinearizationMethod tlm, Consumer<T> con) {
		switch (tlm) {
			case INORDER:
				left.traverse(tlm, con);
				con.accept(token);
				right.traverse(tlm, con);
				break;
			case POSTORDER:
				left.traverse(tlm, con);
				right.traverse(tlm, con);
				con.accept(token);
				break;
			case PREORDER:
				con.accept(token);
				left.traverse(tlm, con);
				right.traverse(tlm, con);
				break;
			default:
				throw new IllegalArgumentException(
						"Got a invalid tree linearizer " + tlm + ". WAT");
		}
	}

	/**
	 * Collapse this tree into a single node
	 * 
	 * @param tokenTransform
	 *            The function to transform nodes into data
	 * @param nodeTransform
	 *            A map of functions for operator collapsing
	 * @param resultTransform
	 *            The function for transforming the result
	 * @return The collapsed value of the tree
	 */
	public <E, T2> E collapse(Function<T, T2> tokenTransform,
			Map<T, BinaryOperator<T2>> nodeTransform,
			Function<T2, E> resultTransform) {
		return resultTransform
				.apply(internalCollapse(tokenTransform, nodeTransform));
	}

	/*
	 * Internal recursive collapser
	 */
	private <T2> T2 internalCollapse(Function<T, T2> tokenTransform,
			Map<T, BinaryOperator<T2>> nodeTransform) {
		if (left == null && right == null) {
			return tokenTransform.apply(token);
		} else {
			T2 leftCollapsed = left.internalCollapse(tokenTransform,
					nodeTransform);
			T2 rightCollapsed = right.internalCollapse(tokenTransform,
					nodeTransform);

			return nodeTransform.get(token).apply(leftCollapsed,
					rightCollapsed);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		internalToString(sb, -1);

		return sb.toString();
	}

	private void internalToString(StringBuilder sb, int indentLevel) {
		indentNLevels(sb, indentLevel);

		if (left == null && right == null) {
			sb.append("Node: ");
			sb.append(token.toString());
			sb.append("\n");
		} else {
			sb.append("Node: ");
			sb.append(token.toString());
			sb.append("\n");

			// indentNLevels(sb, indentLevel + 2);
			//
			// sb.append("Left: \n");

			left.internalToString(sb, indentLevel + 2);
			//
			// indentNLevels(sb, indentLevel + 2);
			//
			// sb.append("Right: \n");

			right.internalToString(sb, indentLevel + 2);
		}
	}

	private void indentNLevels(StringBuilder sb, int n) {
		for (int i = 0; i <= n; i++) {
			sb.append("\t");
		}
	}
}
