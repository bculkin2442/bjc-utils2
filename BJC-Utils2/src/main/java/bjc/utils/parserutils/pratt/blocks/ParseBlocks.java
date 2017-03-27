package bjc.utils.parserutils.pratt.blocks;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import bjc.utils.data.ITree;
import bjc.utils.parserutils.pratt.ParseBlock;
import bjc.utils.parserutils.pratt.Token;

/**
 * Utility class for creating common implementations of {@link ParseBlock}
 * 
 * @author bjculkin
 *
 */
public class ParseBlocks {
	/**
	 * Create a new repeating parse block.
	 * 
	 * @param inner
	 *                The parse block to repeat.
	 * 
	 * @param delim
	 *                The token type that seperates repetitions.
	 * 
	 * @param term
	 *                The token type that terminates repititions.
	 * 
	 * @param mark
	 *                The token to use as the node in the AST.
	 * 
	 * @param action
	 *                The action to perform on the state after every
	 *                repitition.
	 * 
	 * @return A configured repeating parse block.
	 */
	public static <K, V, C> ParseBlock<K, V, C> repeating(ParseBlock<K, V, C> inner, K delim, K term,
			Token<K, V> mark, UnaryOperator<C> action) {
		return new RepeatingParseBlock<>(inner, delim, term, mark, action);
	}

	/**
	 * Create a new triggered parse block.
	 * 
	 * @param source
	 *                The block to trigger around.
	 * 
	 * @param onEnter
	 *                The action to perform upon the state before entering
	 *                the block.
	 * 
	 * @param onExit
	 *                The action to perform upon the state after exiting the
	 *                block.
	 * 
	 * @return A configured trigger parse block.
	 */
	public static <K, V, C> ParseBlock<K, V, C> trigger(ParseBlock<K, V, C> source, UnaryOperator<C> onEnter,
			UnaryOperator<C> onExit) {
		return new TriggeredParseBlock<>(onEnter, onExit, source);
	}

	/**
	 * Create a new simple parse block.
	 * 
	 * @param precedence
	 *                The precedence of the expression inside the block.
	 * 
	 * @param terminator
	 *                The key type of the token expected after this block,
	 *                or null if none is expected.
	 * 
	 * @param validator
	 *                The predicate to use to validate parsed expressions,
	 *                or null if none is used.
	 * 
	 * @return A configured simple parse block.
	 */
	public static <K, V, C> ParseBlock<K, V, C> simple(int precedence, K terminator,
			Predicate<ITree<Token<K, V>>> validator) {
		return new SimpleParseBlock<>(precedence, terminator, validator);
	}
}