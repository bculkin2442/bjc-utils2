package bjc.utils.ioutils.format.exceptions;

/**
 * Exception thrown when the colon modifier is used on the escape directive when
 * it shouldn't have been.
 *
 * @author Ben Culkin
 */
public class UnexpectedColonEscape extends RuntimeException {
	public UnexpectedColonEscape() {
		super("Colon mod not allowed on escape marker in this context");
	}
}
