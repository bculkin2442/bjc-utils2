package bjc.utils.cli;

/**
 * Status codes output by {@link Terminal}.
 * 
 * Format is a two-letter system code, a two letter subsystem code, a severity
 * letter and a five digit error code.
 * 
 * @author bjcul
 *
 */
public enum TerminalCodes {
	// TODO convert these into a class, and add an ability to read from the file
	// The general idea of the format would be a tab-separated value file, with the
	// first value being a command, and then the rest being the body of that command.
	// Would also have the line-continuation feature
	INFO_STARTCOMPROC("IOLPI00001", "STARTING PROCESSING"),
	INFO_ENDCOMPROC("IOLPI00002", "ENDING PROCESSING"),
	
	ERROR_UNRECCOM("IOINE00001", "UNRECOGNIZED COMMAND"),
	ERROR_INVREPNO("IOINE00002", "INVALID REPLY NUMBER FORMAT"),
	ERROR_UNKREPNO("IOINE00002", "UNKNOWN REPLY NUMBER"),
	;
	public final String code;
	public final String message;

	private TerminalCodes(String code, String message) {
		this.code = code;
		this.message = message;
	}
	
	@Override
	public String toString() {
		return code + " " + message;
	}
}
