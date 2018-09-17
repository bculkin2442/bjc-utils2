package bjc.utils.ioutils.format;

/**
 * An exception thrown to escape CL iteration directives.
 * 
 * @author EVE
 *
 */
public class EscapeException extends RuntimeException {
	private static final long serialVersionUID = -4552821131068559005L;

	/**
	 * Whether or not this exception should end iteration.
	 */
	public final boolean endIteration;

	/**
	 * Create a new escape exception.
	 */
	public EscapeException() {
		endIteration = false;
	}

	/**
	 * Create a new escape exception.
	 * 
	 * @param end
	 *        Whether or not to end the iteration.
	 */
	public EscapeException(boolean end) {
		endIteration = end;
	}
}