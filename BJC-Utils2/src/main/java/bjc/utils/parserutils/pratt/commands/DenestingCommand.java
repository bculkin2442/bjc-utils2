package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.InitialCommand;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

/**
 * A command that denests a input tree.
 * 
 * Useful for processing the result of passing a complex parse group to a
 * command.
 * 
 * @author bjculkin
 * 
 * @param <K>
 *                The key type of the tokens.
 * 
 * @param <V>
 *                The value type of the tokens.
 * 
 * @param <C>
 *                The state type of the parser.
 *
 */
public class DenestingCommand<K, V, C> extends AbstractInitialCommand<K, V, C> {
	private InitialCommand<K, V, C> wrapped;

	/**
	 * Create a new transforming initial command.
	 * 
	 * @param internal
	 *                The initial command to delegate to.
	 */
	public DenestingCommand(InitialCommand<K, V, C> internal) {
		wrapped = internal;
	}

	@Override
	protected ITree<Token<K, V>> intNullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		return wrapped.denote(operator, ctx).getChild(0);
	}
}