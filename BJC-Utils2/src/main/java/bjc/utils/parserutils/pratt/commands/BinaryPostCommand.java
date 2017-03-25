package bjc.utils.parserutils.pratt.commands;

import bjc.utils.parserutils.pratt.NonInitialCommand;

/**
 * A operator with fixed precedence.
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
public abstract class BinaryPostCommand<K, V, C> extends NonInitialCommand<K, V, C> {
	private final int leftPower;

	/**
	 * Create a new operator with fixed precedence.
	 * 
	 * @param precedence
	 *                The precedence of the operator.
	 */
	public BinaryPostCommand(int precedence) {
		if (precedence < 0) {
			throw new IllegalArgumentException("Precedence must be non-negative");
		}

		leftPower = precedence;
	}

	@Override
	public int leftBinding() {
		return leftPower;
	}
}