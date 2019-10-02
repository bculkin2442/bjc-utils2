package bjc.utils.ioutils.format.directives;

import java.io.*;

/**
 * Edict that prints a provided string.
 *
 * @author Ben Culkin
 */
public class StringEdict implements Edict {
	private String val;

	/**
	 * Create a new string edict for a given string.
	 *
	 * @param vl
	 * 	The string to print.
	 */
	public StringEdict(String vl) {
		this.val = vl;
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		formCTX.writer.write(val);
	}
}
