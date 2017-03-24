package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.parserutils.pratt.InitialCommand;
import bjc.utils.parserutils.pratt.Token;

import java.util.function.UnaryOperator;

/**
 * * Contains factory methods for producing common implementations of
 * {@link InitialCommand}
 * 
 * @author EVE
 *
 */
public class InitialCommands {
	/**
	 * Create a new unary operator.
	 * 
	 * @param precedence
	 *                The precedence of the operator.
	 * 
	 * @return A command implementing that operator.
	 */
	public static <K, V, C> InitialCommand<K, V, C> unary(int precedence) {
		return new UnaryCommand<>(precedence);
	}

	/**
	 * Create a new grouping operator.
	 * 
	 * @param precedence
	 *                The precedence of the expression in the operator.
	 * 
	 * @param term
	 *                The type that closes the group.
	 * 
	 * @param mark
	 *                The token for the AST node of the group.
	 * 
	 * @return A command implementing the operator.
	 */
	public static <K, V, C> InitialCommand<K, V, C> grouping(int precedence, K term, Token<K, V> mark) {
		return new GroupingCommand<>(precedence, term, mark);
	}

	/**
	 * Create a new leaf operator.
	 * 
	 * @return A command implementing the operator.
	 */
	public static <K, V, C> InitialCommand<K, V, C> leaf() {
		return new LeafCommand<>();
	}

	/**
	 * Create a new pre-ternary operator, like an if-then-else statement.
	 * 
	 * @param cond1
	 *                The priority of the first block.
	 * 
	 * @param block1
	 *                The priority of the second block.
	 * 
	 * @param block2
	 *                The priority of the third block.
	 * 
	 * @param mark1
	 *                The marker that ends the first block.
	 * 
	 * @param mark2
	 *                The marker that ends the second block.
	 * 
	 * @param term
	 *                The token for the AST node of the group.
	 * 
	 * @return A command implementing the operator.
	 */
	public static <K, V, C> InitialCommand<K, V, C> preTernary(int cond1, int block1, int block2, K mark1, K mark2,
			Token<K, V> term) {
		return new PreTernaryCommand<>(cond1, block1, block2, mark1, mark2, term);
	}

	/**
	 * Create a new named constant.
	 * 
	 * @param val
	 *                The value of the constant.
	 * 
	 * @return A command implementing the constant.
	 */
	public static <K, V, C> InitialCommand<K, V, C> constant(ITree<Token<K, V>> val) {
		return new ConstantCommand<>(val);
	}

	/**
	 * Create a new delimited command. This is for block-like constructs.
	 * 
	 * @param inner
	 *                The precedence of the inner blocks.
	 * 
	 * @param delim
	 *                The marker between sub-blocks.
	 * 
	 * @param mark
	 *                The block terminator.
	 * 
	 * @param term
	 *                The token for the AST node of the group.
	 * 
	 * @param onEnter
	 *                The function to apply to the state on entering the
	 *                block.
	 * 
	 * @param onDelim
	 *                The function to apply to the state on finishing a
	 *                sub-block.
	 * 
	 * @param onExit
	 *                The function to apply to the state on exiting the
	 *                block.
	 * 
	 * @param statement
	 *                Whether or not the sub-blocks are statements or
	 *                expressions.
	 * 
	 * @return A command implementing the operator.
	 */
	public static <K, V, C> InitialCommand<K, V, C> delimited(int inner, K delim, K mark, Token<K, V> term,
			UnaryOperator<C> onEnter, UnaryOperator<C> onDelim, UnaryOperator<C> onExit,
			boolean statement) {
		return new DelimitedCommand<>(inner, delim, mark, term, onEnter, onDelim, onExit, statement);
	}
}
