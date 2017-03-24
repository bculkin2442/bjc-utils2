package bjc.utils.parserutils.pratt;

/**
 * Represents a simple parsing token.
 * 
 * @author EVE
 * 
 * @param <K>
 *                The key type of this token. Represents the type of the token.
 * 
 * @param <V>
 *                The value type of this token. Represents any additional data
 *                for the token.
 *
 */
public interface Token<K, V> {
	/**
	 * Get the key for this token.
	 * 
	 * @return The key for this token
	 */
	K getKey();

	/**
	 * Get the value for this token.
	 * 
	 * @return The value for this token.
	 */
	V getValue();
}
