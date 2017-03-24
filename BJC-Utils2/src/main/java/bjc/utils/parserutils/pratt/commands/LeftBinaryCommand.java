package bjc.utils.parserutils.pratt.commands;

public class LeftBinaryCommand<K, V, C> extends BinaryCommand<K, V, C> {
	public LeftBinaryCommand(int leftPower) {
		super(leftPower);
	}

	@Override
	protected int rightBinding() {
		return 1 + leftBinding();
	}
}