package bjc.utils.parserutils.pratt.commands;

/**
 * A left-associative operator.
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
public class LeftBinaryCommand<K, V, C> extends BinaryCommand<K, V, C> {
	/**
	 * Create a new left-associative operator.
	 * 
	 * @param precedence
	 *                The precedence of the operator.
	 */
	public LeftBinaryCommand(int precedence) {
		super(precedence);
	}

	@Override
	protected int rightBinding() {
		return 1 + leftBinding();
	}
}