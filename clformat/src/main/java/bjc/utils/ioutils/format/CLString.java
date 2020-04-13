package bjc.utils.ioutils.format;

import java.io.*;
import java.util.*;

import bjc.esodata.*;
import bjc.utils.ioutils.ReportWriter;
import bjc.utils.ioutils.format.directives.*;

/**
 * Represents a compiled format string.
 *
 * @author Ben Culkin
 */
public class CLString {
	private List<Edict> edicts;

	/**
	 * Create a new compiled format string.
	 *
	 * @param edts
	 *             The compiled directives that make up the format.
	 */
	public CLString(List<Edict> edts) {
		edicts = edts;
	}

	/**
	 * Execute this string with the given parameters.
	 *
	 * @param parms
	 *              The format parameters for the string.
	 *
	 * @return The result from executing the format string.
	 *
	 * @throws IOException
	 *                     If something I/O related has gone wrong.
	 */
	public String format(Object... parms) throws IOException {
		StringWriter sw = new StringWriter();
		try (ReportWriter rw = new ReportWriter(sw)) {
			return format(rw, parms);
		}
	}

	/**
	 * Execute this string with the given parameters.
	 *
	 * @param rw
	 *             The writer to write the string to.
	 *
	 * @param itms
	 *             The format parameters to use.
	 *
	 * @return The result of executing the string.
	 *
	 * @throws IOException
	 *                     If something I/O related goes wrong.
	 */
	public String format(ReportWriter rw, Tape<Object> itms) throws IOException {
		FormatContext formCTX = new FormatContext(rw, itms);

		return format(formCTX);
	}

	/**
	 * Execute this string with the given parameters.
	 *
	 * @param rw
	 *              The writer to write the string to.
	 *
	 * @param parms
	 *              The format parameters to use.
	 *
	 * @return The result of executing the string.
	 *
	 * @throws IOException
	 *                     If something I/O related goes wrong.
	 */
	public String format(ReportWriter rw, Object... parms) throws IOException {
		Tape<Object> itms = new SingleTape<>(parms);

		FormatContext formCTX = new FormatContext(rw, itms);

		return format(formCTX);
	}

	/**
	 * Execute a format string in a given context.
	 *
	 * @param formCTX
	 *                The context to use for formatting.
	 * 
	 * @return The result of executing the format string.
	 *
	 * @throws IOException
	 *                     If something I/O related goes wrong.
	 */
	public String format(FormatContext formCTX) throws IOException {
		try {
			for (Edict edt : edicts) {
				edt.format(formCTX);
			}
		} catch (DirectiveEscape eex) {
			// General escape exception, so stop formatting.
		}

		return formCTX.writer.toString();
	}

	/**
	 * Is this format string empty? (does it have 0 edicts?)
	 *
	 * @return If this format string is empty.
	 */
	public boolean isEmpty() {
		if (edicts.size() == 0)
			return true;

		return false;
	}

	@Override
	public String toString() {
		return String.format("CLString [edicts=%s]", edicts);
	}
}
