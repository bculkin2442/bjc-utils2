package bjc.utils.parserutils.delims;

/**
 * The superclass for exceptions thrown during sequence delimitation.
 */
public class DelimiterException extends RuntimeException {
	private static final long serialVersionUID = 2079514406049040888L;

	/**
	 * Create a new generic delimiter exception.
	 *
	 * @param res
	 *        The reason for this exception.
	 */
	public DelimiterException(final String res) {
		super(res);
	}
}
