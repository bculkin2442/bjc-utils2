package bjc.utils.exceptions;

public class NonConstantPower extends RuntimeException {
	private static final long serialVersionUID = 1640883448305031149L;

	public NonConstantPower() {
		super("Cannot raise an expression to a non-constant power");
	}
}