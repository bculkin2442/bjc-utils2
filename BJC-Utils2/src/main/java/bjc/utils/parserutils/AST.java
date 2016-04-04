package bjc.utils.parserutils;

import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import bjc.utils.funcdata.bst.ITreePart.TreeLinearizationMethod;

/**
 * A simple binary tree meant for use as an AST
 * 
 * @author ben
 *
 * @param <T>
 *            The type of token in this AST
 */
public class AST<T> {
	/**
	 * Indent a string n levels
	 * 
	 * @param builder
	 *            The string to indent
	 * @param levels
	 *            The number of levels to indent
	 */
	protected static void indentNLevels(StringBuilder builder,
			int levels) {
		for (int i = 0; i <= levels; i++) {
			builder.append("\t");
		}
	}

	private AST<T>	left;
	private AST<T>	right;

	private T		token;

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
	 * @param lft
	 *            The left child of this AST
	 * @param rght
	 *            The right child of this AST
	 */
	public AST(T tokn, AST<T> lft, AST<T> rght) {
		token = tokn;

		left = lft;
		right = rght;
	}

	/**
	 * Collapse this tree into a single node
	 * 
	 * @param <E>
	 *            The final value of the collapsed tree
	 * @param <T2>
	 * 
	 * @param tokenTransformer
	 *            The function to transform nodes into data
	 * @param nodeTransformer
	 *            A map of functions for operator collapsing
	 * @param resultTransformer
	 *            The function for transforming the result
	 * @return The collapsed value of the tree
	 */
	@SuppressWarnings("unchecked")
	public <E, T2> E collapse(Function<T, T2> tokenTransformer,
			Function<T, BinaryOperator<T2>> nodeTransformer,
			Function<T2, E> resultTransformer) {
		if (tokenTransformer == null) {
			throw new NullPointerException(
					"Token transformer must not be null");
		} else if (nodeTransformer == null) {
			throw new NullPointerException(
					"Node transformer must not be null");
		}

		if (resultTransformer != null) {
			return resultTransformer.apply(
					internalCollapse(tokenTransformer, nodeTransformer));
		} else {
			// This is valid because if the user passes null as the last
			// parameter, E will be inferred as Object, but will actually
			// be T2
			return (E) internalCollapse(tokenTransformer, nodeTransformer);
		}
	}

	/**
	 * Expand the nodes in an AST
	 * 
	 * @param expander
	 *            The function to use for expanding nodes
	 * @return The expanded AST
	 */
	public AST<T> expand(Function<T, AST<T>> expander) {
		if (expander == null) {
			throw new NullPointerException("Expander must not be null");
		}

		return collapse(expander, (operator) -> (leftAST, rightAST) -> {
			return new AST<>(operator, leftAST, rightAST);
		}, null);
	}

	/*
	 * Internal recursive collapser
	 */
	protected <T2> T2 internalCollapse(Function<T, T2> tokenTransformer,
			Function<T, BinaryOperator<T2>> nodeTransformer) {
		if (left == null && right == null) {
			return tokenTransformer.apply(token);
		} else {
			T2 leftCollapsed;

			if (left == null) {
				leftCollapsed = null;
			} else {
				leftCollapsed = left.internalCollapse(tokenTransformer,
						nodeTransformer);
			}

			T2 rightCollapsed;

			if (right == null) {
				rightCollapsed = null;
			} else {
				rightCollapsed = right.internalCollapse(tokenTransformer,
						nodeTransformer);
			}

			return nodeTransformer.apply(token).apply(leftCollapsed,
					rightCollapsed);
		}
	}

	/**
	 * Internal version of toString for proper rendering
	 * 
	 * @param builder
	 *            The string rendering being built
	 * @param indentLevel
	 *            The current level to indent the tree
	 */
	protected void internalToString(StringBuilder builder,
			int indentLevel) {
		indentNLevels(builder, indentLevel);

		if (left == null && right == null) {
			builder.append("Node: ");
			builder.append(token.toString());
			builder.append("\n");
		} else {
			builder.append("Node: ");
			builder.append(token.toString());
			builder.append("\n");

			if (left != null) {
				left.internalToString(builder, indentLevel + 2);
			} else {
				indentNLevels(builder, indentLevel + 2);
				builder.append("No left node\n");
			}
			if (right != null) {
				right.internalToString(builder, indentLevel + 2);
			} else {
				indentNLevels(builder, indentLevel + 2);
				builder.append("No right node\n");
			}
		}
	}

	/**
	 * Execute a transform on selective nodes of the tree
	 * 
	 * @param transformerPredicate
	 *            The predicate to pick nodes to transform
	 * @param transformer
	 *            The thing to use to transform the nodes
	 */
	public void selectiveTransform(Predicate<T> transformerPredicate,
			UnaryOperator<T> transformer) {
		if (transformerPredicate == null) {
			throw new NullPointerException("Predicate must not be null");
		} else if (transformer == null) {
			throw new NullPointerException("Transformer must not be null");
		}

		if (transformerPredicate.test(token)) {
			token = transformer.apply(token);
		}

		if (left != null) {
			left.selectiveTransform(transformerPredicate, transformer);
		}

		if (right != null) {
			right.selectiveTransform(transformerPredicate, transformer);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		internalToString(builder, -1);

		builder.deleteCharAt(builder.length() - 1);

		return builder.toString();
	}

	/**
	 * Transmute the tokens in an AST into a different sort of token
	 * 
	 * @param <E>
	 *            The type of the transformed tokens
	 * 
	 * @param tokenTransformer
	 *            The transform to run on the tokens
	 * @return The AST with transformed tokens
	 */
	public <E> AST<E> transmuteAST(Function<T, E> tokenTransformer) {
		if (tokenTransformer == null) {
			throw new NullPointerException("Transformer must not be null");
		}

		AST<E> leftBranch = null;
		AST<E> rightBranch = null;

		if (left != null) {
			leftBranch = left.transmuteAST(tokenTransformer);
		}

		if (right != null) {
			rightBranch = right.transmuteAST(tokenTransformer);
		}

		return new AST<>(tokenTransformer.apply(token), leftBranch,
				rightBranch);
	}

	/**
	 * Traverse an AST
	 * 
	 * @param linearizationMethod
	 *            The way to traverse the tree
	 * @param action
	 *            The function to call on each traversed element
	 */
	public void traverse(TreeLinearizationMethod linearizationMethod,
			Consumer<T> action) {
		if (linearizationMethod == null) {
			throw new NullPointerException(
					"Linearization method must not be null");
		} else if (action == null) {
			throw new NullPointerException("Action must not be null");
		}

		if (left != null && right != null) {
			switch (linearizationMethod) {
				case INORDER:
					left.traverse(linearizationMethod, action);
					action.accept(token);
					right.traverse(linearizationMethod, action);
					break;
				case POSTORDER:
					left.traverse(linearizationMethod, action);
					right.traverse(linearizationMethod, action);
					action.accept(token);
					break;
				case PREORDER:
					action.accept(token);
					left.traverse(linearizationMethod, action);
					right.traverse(linearizationMethod, action);
					break;
				default:
					throw new IllegalArgumentException(
							"Got a invalid tree linearizer "
									+ linearizationMethod + ". WAT");
			}
		} else {
			action.accept(token);
		}
	}

	/**
	 * Apply an action to the head node of this AST
	 * 
	 * @param <E>
	 *            The type of the returned value
	 * @param headAction
	 *            The action to apply to the head node
	 * @return The result of applying the action
	 */
	public <E> E applyToHead(Function<T, E> headAction) {
		if (headAction == null) {
			throw new NullPointerException("Action must not be null");
		}

		return headAction.apply(token);
	}

	/**
	 * Apply an action to the left side of this AST
	 * 
	 * @param <E>
	 *            The type of the returned value
	 * @param leftAction
	 *            The action to apply to the left side
	 * @return The result of applying the action
	 */
	public <E> E applyToLeft(Function<AST<T>, E> leftAction) {
		if (leftAction == null) {
			throw new NullPointerException("Action must not be null");
		}

		return leftAction.apply(left);
	}

	/**
	 * Apply an action to the right side of this AST
	 * 
	 * @param <E>
	 *            The type of the returned value
	 * @param rightAction
	 *            The action to apply to the right side
	 * @return The result of applying the action
	 */
	public <E> E applyToRight(Function<AST<T>, E> rightAction) {
		if (rightAction == null) {
			throw new NullPointerException("Action must not be null");
		}

		return rightAction.apply(right);
	}
}
