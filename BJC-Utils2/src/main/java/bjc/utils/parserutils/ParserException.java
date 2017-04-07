package bjc.utils.parserutils;

/**
 * General superclass for exceptions thrown during parsing.
 * 
 * @author EVE
 *
 */
public class ParserException extends Exception {
	/**
	 * Create a new exception with the provided message.
	 * 
	 * @param msg The message for the exception.
	 */
	public ParserException(String msg) {
		super(msg);
	}
	
	/**
	 * Create a new exception with the provided message and cause.
	 * 
	 * @param msg The message for the exception.
	 * @param cause The cause of the exception.
	 */
	public ParserException(String msg, Exception cause) {
		super(msg, cause);
	}
}