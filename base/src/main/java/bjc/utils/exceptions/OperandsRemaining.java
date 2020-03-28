package bjc.utils.exceptions;

/**
 * Exception thrown when an operation has finished, but still has more input
 * that has not been processed.
 * 
 * @author Ben Culkin
 *
 */
public class OperandsRemaining extends RuntimeException {
	private static final long serialVersionUID = 4848222659854671315L;

	/**
	 * Create a new OperandsRemaining exception with a default message.
	 */
	public OperandsRemaining() {
		super("Operation had input left-over");
	}
	
	/**
	 * Create a new OperandsRemaining exception with a specific message.
	 * 
	 * @param msg The message of the exception.
	 */
	public OperandsRemaining(String msg) {
		super(msg);
	}
}
