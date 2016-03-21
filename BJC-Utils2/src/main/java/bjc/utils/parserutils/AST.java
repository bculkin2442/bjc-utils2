package bjc.utils.parserutils;

import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

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

	/**
	 * Internal version of toString for proper rendering
	 * 
	 * @param sb
	 *            The string rendering being built
	 * @param indentLevel
	 *            The current level to indent the tree
	 */
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

			left.internalToString(sb, indentLevel + 2);
			right.internalToString(sb, indentLevel + 2);
		}
	}

	/**
	 * Indent a string n levels
	 * 
	 * @param sb
	 *            The string to indent
	 * @param n
	 *            The number of levels to indent
	 */
	private void indentNLevels(StringBuilder sb, int n) {
		for (int i = 0; i <= n; i++) {
			sb.append("\t");
		}
	}

	/**
	 * Execute a transform on selective nodes of the tree
	 * 
	 * @param transformPred
	 *            The predicate to pick nodes to transform
	 * @param transformer
	 *            The thing to use to transform the nodes
	 */
	public void selectiveTransform(Predicate<T> transformPred,
			UnaryOperator<T> transformer) {
		if (transformPred.test(token)) {
			token = transformer.apply(token);
		}

		if (left != null) {
			left.selectiveTransform(transformPred, transformer);
		}

		if (right != null) {
			right.selectiveTransform(transformPred, transformer);
		}
	}

	/**
	 * Transmute the tokens in an AST into a different sort of token
	 * 
	 * @param tokenTransformer
	 *            The transform to run on the tokens
	 * @return The AST with transformed tokens
	 */
	public <E> AST<E> transmuteAST(Function<T, E> tokenTransformer) {
		AST<E> l = null;
		AST<E> r = null;

		if (left != null) {
			l = left.transmuteAST(tokenTransformer);
		}

		if (right != null) {
			r = right.transmuteAST(tokenTransformer);
		}

		return new AST<E>(tokenTransformer.apply(token), l, r);
	}
}
