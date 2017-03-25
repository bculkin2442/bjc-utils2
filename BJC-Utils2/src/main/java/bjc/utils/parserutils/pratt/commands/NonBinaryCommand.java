package bjc.utils.parserutils.pratt.commands;

/**
 * A non-associative operator.
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
public class NonBinaryCommand<K, V, C> extends BinaryCommand<K, V, C> {
	/**
	 * Create a new non-associative operator.
	 * 
	 * @param precedence
	 *                The precedence of the operator.
	 */
	public NonBinaryCommand(int precedence) {
		super(precedence);
	}

	@Override
	protected int rightBinding() {
		return 1 + leftBinding();
	}

	@Override
	public int nextBinding() {
		return leftBinding() - 1;
	}
}