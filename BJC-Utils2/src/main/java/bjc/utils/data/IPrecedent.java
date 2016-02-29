package bjc.utils.data;

/**
 * Represents something that has a set precedence
 * 
 * @author ben
 *
 */
public interface IPrecedent {
	/**
	 * Get the precedence of the attached object
	 * 
	 * @return The precedence of the attached object
	 */
	public int getPrecedence();

	/**
	 * Create a new object with set precedence
	 * 
	 * @param prec
	 *            The precedence of the object to handle
	 * @return A new object with set precedence
	 */
	public static IPrecedent newSimplePrecedent(int prec) {
		return () -> prec;
	}
}