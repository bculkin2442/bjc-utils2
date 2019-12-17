package bjc.utils.ioutils.format;

import java.io.*;
import java.util.*;

import bjc.utils.esodata.*;
import bjc.utils.ioutils.*;
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
	 * 	The compiled directives that make up the format.
	 */
	public CLString(List<Edict> edts) {
		edicts = edts;
	}

	public String format(Object... parms) throws IOException {
		StringWriter sw = new StringWriter();
		ReportWriter rw = new ReportWriter(sw);

		return format(rw, parms);
	}

	public String format(ReportWriter rw, Tape<Object> itms) throws IOException {
		FormatContext formCTX = new FormatContext(rw, itms);

		return format(formCTX);
	}

	public String format(ReportWriter rw, Object... parms) throws IOException {
		Tape<Object> itms = new SingleTape<>(parms);

		FormatContext formCTX = new FormatContext(rw, itms);

		return format(formCTX);
	}

	public String format(FormatContext formCTX) throws IOException {
		try {
			for (Edict edt : edicts) {
				edt.format(formCTX);
			}
		} catch (EscapeException eex) {
			// General escape exception, so stop formatting.
		}

		return formCTX.writer.toString();
	}
}
