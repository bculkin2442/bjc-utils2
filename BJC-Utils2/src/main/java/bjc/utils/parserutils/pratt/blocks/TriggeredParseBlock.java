package bjc.utils.parserutils.pratt.blocks;

import java.util.function.UnaryOperator;

import bjc.utils.data.ITree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.ParseBlock;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

/**
 * A parse block that can adjust the state before handling its context.
 * 
 * @author bjculkin
 *
 * @param <K>
 *                The key type of the tokens.
 * @param <V>
 *                The value type of the tokens.
 * @param <C>
 *                The state type of the parser.
 */
public class TriggeredParseBlock<K, V, C> implements ParseBlock<K, V, C> {
	private UnaryOperator<C>	onEnter;
	private UnaryOperator<C>	onExit;

	private ParseBlock<K, V, C> source;

	/**
	 * Create a new triggered parse block.
	 * 
	 * @param onEnter
	 *                The action to fire before parsing the block.
	 * 
	 * @param onExit
	 *                The action to fire after parsing the block.
	 * 
	 * @param source
	 *                The block to use for parsing.
	 */
	public TriggeredParseBlock(UnaryOperator<C> onEnter, UnaryOperator<C> onExit, ParseBlock<K, V, C> source) {
		super();
		this.onEnter = onEnter;
		this.onExit = onExit;
		this.source = source;
	}

	@Override
	public ITree<Token<K, V>> parse(ParserContext<K, V, C> ctx) throws ParserException {
		C newState = onEnter.apply(ctx.state);

		ParserContext<K, V, C> newCtx = new ParserContext<>(ctx.tokens, ctx.parse, newState);

		ITree<Token<K, V>> res = source.parse(newCtx);

		ctx.state = onExit.apply(newState);

		return res;
	}

}
