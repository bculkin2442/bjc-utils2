package bjc.utils.ioutils;

/**
 * Exception thrown when there is a duplicate key, when they are forbidden.
 *
 * @author 15405
 *
 */
public class DuplicateKeys extends RuntimeException {
	private static final long serialVersionUID = -5521190136366024804L;

	/**
	 * Create a new duplicate key exception.
	 *
	 * @param keyName
	 *                The name of the key that has been duplicated.
	 */
	public DuplicateKeys(String keyName) {
		super(String.format("Duplicate value encountered for key '%s'", keyName));
	}
}