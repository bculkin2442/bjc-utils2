package bjc.utils.parserutils;

/**
 * The superclass for exceptions thrown during sequence delimitation.
 */
public class DelimiterException extends RuntimeException {
	/**
	 * Create a new generic delimiter exception.
	 * 
	 * @param res
	 *                The reason for this exception.
	 */
	public DelimiterException(String res) {
		super(res);
	}
}