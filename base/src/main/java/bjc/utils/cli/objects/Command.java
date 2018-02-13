package bjc.utils.cli.objects;

/**
 * A single-line command read from the user.
 *
 * @author Ben Culkin
 */
public class Command {
	/**
	 * Command status values.
	 *
	 * @author Ben Culkin
	 */
	public static enum CommandStatus {
		/**
		 * The command succeded.
		 */
		SUCCESS,
		/**
		 * The command failed non-fatally.
		 */
		FAIL,
		/**
		 * The command failed fatally.
		 */
		ERROR,
		/**
		 * The command was the last one.
		 */
		FINISH,
	}

	/**
	 * The line number of this command.
	 */
	public final int lineNo;

	/**
	 * The full text of this command.
	 */
	public final String	fullCommand;
	/**
	 * The text of this command without its name.
	 */
	public final String	remnCommand;
	/**
	 * The name of this command.
	 */
	public final String	nameCommand;

	/**
	 * The name of the I/O source this command was read from.
	 */
	public final String ioSource;

	/**
	 * Create a new command.
	 *
	 * @param ln
	 *        The string to get the command from.
	 *
	 * @param lno
	 *        The number of the line the command came from.
	 *
	 * @param ioSrc
	 *        The name of where the I/O came from.
	 */
	public Command(String ln, int lno, String ioSrc) {
		int idx = ln.indexOf(' ');

		if(idx == -1) idx = ln.length();

		fullCommand = ln;
		nameCommand = ln.substring(0, idx).trim();
		remnCommand = ln.substring(idx).trim();

		lineNo = lno;

		ioSource = ioSrc;
	}

	/**
	 * Parse a command from a string.
	 *
	 * The main thing this does is ignore blank lines, as well as comments
	 * marked by #'s either at the start of the line or part of the way
	 * through the line.
	 *
	 * @param ln
	 *        The string to get the command from.
	 *
	 * @param lno
	 *        The line number of the command.
	 *
	 * @param ioSource
	 *        The name of where the I/O came from.
	 * @return The parsed command
	 */
	public static Command fromString(String ln, int lno, String ioSource) {
		/* Ignore blank lines and comments. */
		if(ln.equals("")) return null;
		if(ln.startsWith("#")) return null;

		/* Trim off comments part-way through the line. */
		int idxHash = ln.indexOf('#');
		if(idxHash != -1) {
			ln = ln.substring(0, idxHash).trim();
		}

		return new Command(ln, lno, ioSource);
	}

	/**
	 * Give an informational message about something in relation to this
	 * command.
	 *
	 * @param info
	 *        The informational message.
	 *
	 * @param parms
	 *        The parameters for the informational message.
	 * @return The information message.
	 */
	public String info(String info, Object... parms) {
		String msg = String.format(info, parms);

		return String.format("INFO (%s:%d): %s", ioSource, lineNo, msg);
	}

	/**
	 * Warn about something in relation to this command.
	 *
	 * @param warning
	 *        The warning message.
	 *
	 * @param parms
	 *        The parameters for the warning message.
	 * 
	 * @return The formatted warning.
	 */
	public String warn(String warning, Object... parms) {
		String msg = String.format(warning, parms);

		return String.format("WARNING (%s:%d): %s", ioSource, lineNo, msg);
	}

	/**
	 * Give an error about something in relation to this command.
	 *
	 * @param err
	 *        The error message.
	 *
	 * @param parms
	 *        The parameters for the error message.
	 * 
	 * @return The formatted error
	 */
	public String error(String err, Object... parms) {
		String msg = String.format(err, parms);

		return String.format("ERROR (%s:%d): %s", ioSource, lineNo, msg);
	}
}
