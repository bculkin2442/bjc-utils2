package bjc.utils.exceptions;

/**
 * Exception thrown when using a non-constant power.
 * 
 * @author Ben Culkin
 *
 */
public class NonConstantPower extends RuntimeException {
	private static final long serialVersionUID = 1640883448305031149L;

	/**
	 * Create a new non-constant power exception.
	 */
	public NonConstantPower() {
		super("Cannot raise an expression to a non-constant power");
	}
}