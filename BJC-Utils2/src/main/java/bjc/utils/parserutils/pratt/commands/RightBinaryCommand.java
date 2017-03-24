package bjc.utils.parserutils.pratt.commands;

public class RightBinaryCommand<K, V, C> extends BinaryCommand<K, V, C> {
	public RightBinaryCommand(int leftPower) {
		super(leftPower);
	}

	@Override
	protected int rightBinding() {
		return leftBinding();
	}
}