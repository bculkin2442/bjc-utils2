package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.parserutils.pratt.InitialCommand;
import bjc.utils.parserutils.pratt.ParseBlock;
import bjc.utils.parserutils.pratt.Token;

import java.util.function.UnaryOperator;

import static bjc.utils.parserutils.pratt.blocks.ParseBlocks.*;

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
		ParseBlock<K, V, C> innerBlock = simple(precedence, term, null);

		return new GroupingCommand<>(innerBlock, mark);
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
		ParseBlock<K, V, C> condBlock = simple(cond1, mark1, null);
		ParseBlock<K, V, C> opblock1 = simple(block1, mark2, null);
		ParseBlock<K, V, C> opblock2 = simple(block2, null, null);

		return new PreTernaryCommand<>(condBlock, opblock1, opblock2, term);
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
		ParseBlock<K, V, C> innerBlock = simple(inner, null, null);
		ParseBlock<K, V, C> delimsBlock = repeating(innerBlock, delim, mark, term, onDelim);
		ParseBlock<K, V, C> scopedBlock = trigger(delimsBlock, onEnter, onExit);

		GroupingCommand<K, V, C> command = new GroupingCommand<>(scopedBlock, term);

		/*
		 * Remove the wrapper layer from grouping-command on top of
		 * RepeatingParseBlock.
		 */
		return denest(command);
	}

	/**
	 * Create a new denesting command.
	 * 
	 * This removes one tree-level, and is useful when combining complex
	 * parse blocks with commands.
	 * 
	 * @param comm
	 *                The command to denest.
	 * 
	 * @return A command that denests the result of the provided command.
	 */
	public static <K, V, C> InitialCommand<K, V, C> denest(InitialCommand<K, V, C> comm) {
		return new DenestingCommand<>(comm);
	}
}
