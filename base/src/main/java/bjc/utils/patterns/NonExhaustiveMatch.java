package bjc.utils.patterns;

/**
 * Exception thrown when a non-exhaustive match happens.
 * @author Ben Culkin
 *
 */
public class NonExhaustiveMatch extends Exception {
	private static final long serialVersionUID = 3892904574888418544L;

	/**
	 * Create a new non-exhaustive match.
	 * 
	 * @param message The message for the exception.
	 */
	public NonExhaustiveMatch(String message) {
		super(message);
	}

}
