package bjc.utils.parserutils.pratt.commands;

/**
 * A right-associative binary operator.
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
public class RightBinaryCommand<K, V, C> extends BinaryCommand<K, V, C> {
	/**
	 * Create a new right-associative operator.
	 * 
	 * @param precedence
	 *                The precedence of the operator.
	 */
	public RightBinaryCommand(int precedence) {
		super(precedence);
	}

	@Override
	protected int rightBinding() {
		return leftBinding();
	}
}