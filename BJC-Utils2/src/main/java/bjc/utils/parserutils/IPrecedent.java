package bjc.utils.parserutils;

/**
 * Represents something that has a set precedence
 * 
 * @author ben
 *
 */
@FunctionalInterface
public interface IPrecedent {
	/**
	 * Create a new object with set precedence
	 * 
	 * @param precedence
	 *            The precedence of the object to handle
	 * @return A new object with set precedence
	 */
	public static IPrecedent newSimplePrecedent(int precedence) {
		return () -> precedence;
	}

	/**
	 * Get the precedence of the attached object
	 * 
	 * @return The precedence of the attached object
	 */
	public int getPrecedence();
}