package bjc.utils.parserutils.pratt.commands;

import bjc.utils.data.ITree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.InitialCommand;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

public class DenestingCommand<C, V, K> extends AbstractInitialCommand<K, V, C> {
	private InitialCommand<K, V, C> internal;

	/**
	 * Create a new transforming initial command.
	 * 
	 * @param internal
	 *                The initial command to delegate to.
	 * 
	 * @param transform
	 *                The transform to apply to the returned tree.
	 */
	public DenestingCommand(InitialCommand<K, V, C> internal) {
		super();
		this.internal = internal;
	}

	@Override
	protected ITree<Token<K, V>> intNullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		return internal.denote(operator, ctx).getChild(0);
	}
}