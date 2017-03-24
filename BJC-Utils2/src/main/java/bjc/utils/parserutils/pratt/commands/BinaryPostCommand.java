package bjc.utils.parserutils.pratt.commands;

import bjc.utils.parserutils.pratt.NonInitialCommand;

/*
 * A command with constant binding power.
 */
public abstract class BinaryPostCommand<K, V, C> extends NonInitialCommand<K, V, C> {
	private final int leftPower;

	public BinaryPostCommand(int power) {
		leftPower = power;
	}

	@Override
	public int leftBinding() {
		return leftPower;
	}
}