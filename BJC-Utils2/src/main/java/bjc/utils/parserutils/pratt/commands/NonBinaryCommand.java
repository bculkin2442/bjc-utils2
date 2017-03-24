package bjc.utils.parserutils.pratt.commands;

public class NonBinaryCommand<K, V, C> extends BinaryCommand<K, V, C> {
	public NonBinaryCommand(int leftPower) {
		super(leftPower);
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