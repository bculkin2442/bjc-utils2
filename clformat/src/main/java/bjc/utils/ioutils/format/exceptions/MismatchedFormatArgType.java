package bjc.utils.ioutils.format.exceptions;

/**
 * Exception thrown when a provided format argument was not of the correct type.
 *
 * @author Ben Culkin
 */
public class MismatchedFormatArgType extends RuntimeException {
	private static final long serialVersionUID = 7013519754361279429L;

	/**
	 * Create a new format arg mismatch with a given message.
	 *
	 * @param msg
	 *            The message for the exception.
	 */
	public MismatchedFormatArgType(String msg) {
		super(msg);
	}

	/**
	 * Create a new standard format arg mismatch.
	 *
	 * @param dir
	 *                 The directive this argument was for.
	 *
	 * @param expected
	 *                 The class we expected to get.
	 *
	 * @param got
	 *                 The class we actually got.
	 */
	public MismatchedFormatArgType(String dir, Class<?> expected, Class<?> got) {
		this(String.format("Bad format argument to %s directive: got %s, expected %s",
				dir, got.getName(), expected.getName()));
	}
}
