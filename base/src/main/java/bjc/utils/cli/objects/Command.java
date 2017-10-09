package bjc.utils.cli.objects;

public class Command {
	/**
	 * Command status values.
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

	public final int lineNo;

	public final String fullCommand;
	public final String remnCommand;
	public final String nameCommand;

	public final String ioSource;

	/**
	 * Create a new command.
	 *
	 * @param ln
	 * 	The line to get the command from.
	 * @param lno
	 * 	The number of the line the command came from.
	 * @param ioSrc
	 * 	The name of where the I/O came from.
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

	public static Command fromString(String ln, int lno, String ioSource) {
		/*
		 * Ignore blank lines and comments.
		 */
		if(ln.equals(""))      return null;
		if(ln.startsWith("#")) return null;

		/*
		 * Trim off comments part-way through the line.
		 */
		int idxHash = ln.indexOf('#');
		if(idxHash != -1) {
			ln = ln.substring(0, idxHash).trim();
		}

		return new Command(ln, lno, ioSource);
	}

	public String warn(String warning, Object... parms) {
		String msg = String.format(warning, parms);

		return String.format("WARNING (%s:%d): %s", ioSource, lineNo, msg);
	}

	public String error(String err, Object... parms) {
		String msg = String.format(err, parms);

		return String.format("ERROR (%s:%d): %s", ioSource, lineNo, msg);
	}
}
