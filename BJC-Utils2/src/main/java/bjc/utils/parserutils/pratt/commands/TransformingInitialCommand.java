package bjc.utils.parserutils.pratt.commands;

import java.util.function.UnaryOperator;

import bjc.utils.data.ITree;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.pratt.InitialCommand;
import bjc.utils.parserutils.pratt.ParserContext;
import bjc.utils.parserutils.pratt.Token;

/**
 * An initial command that transforms the result of another command.
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
 */
public class TransformingInitialCommand<K, V, C> extends AbstractInitialCommand<K, V, C> {
	private InitialCommand<K, V, C> internal;

	private UnaryOperator<ITree<Token<K, V>>> transform;

	/**
	 * Create a new transforming initial command.
	 * 
	 * @param internal
	 *                The initial command to delegate to.
	 * 
	 * @param transform
	 *                The transform to apply to the returned tree.
	 */
	public TransformingInitialCommand(InitialCommand<K, V, C> internal,
			UnaryOperator<ITree<Token<K, V>>> transform) {
		super();
		this.internal = internal;
		this.transform = transform;
	}

	@Override
	protected ITree<Token<K, V>> intNullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		return transform.apply(internal.denote(operator, ctx));
	}

}
