package bjc.utils.exceptions;

public class InvalidToken extends RuntimeException {
	private static final long serialVersionUID = -5077165766341244689L;

	public InvalidToken(String tok) {
		super(String.format("Did not recognize token '%s' as a valid token", tok));
	}
}