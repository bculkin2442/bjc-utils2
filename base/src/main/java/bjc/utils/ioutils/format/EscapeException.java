package bjc.utils.ioutils.format;

public class EscapeException extends RuntimeException {
	private static final long serialVersionUID = -4552821131068559005L;

	public final boolean endIteration;

	public EscapeException() {
		endIteration = false;
	}

	public EscapeException(boolean end) {
		endIteration = end;
	}
}