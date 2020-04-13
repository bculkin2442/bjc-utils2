package bjc.utils.ioutils.format.directives;

import java.io.*;

import bjc.esodata.*;
import bjc.utils.ioutils.ReportWriter;

/**
 * Encapsulates all of the state that is provided to edicts when they are
 * formatted.
 *
 * @author Ben Culkin
 */
public class FormatContext {
	/**
	 * The place where we write all of out outputs to.
	 */
	public ReportWriter writer;

	/**
	 * The parameters passed into invocation of formatting.
	 */
	public Tape<Object> items;

	/**
	 * Create a new format context.
	 *
	 * @param rw
	 *             The writer to store output into.
	 * @param itms
	 *             The items that shall serve as format parameters.
	 */
	public FormatContext(ReportWriter rw, Tape<Object> itms) {
		writer = rw;

		items = itms;
	}

	/**
	 * Get a new scratch writer, with the same format settings as the current
	 * writer.
	 *
	 * @return A new writer, as described above.
	 */
	public ReportWriter getScratchWriter() {
		return writer.duplicate(new StringWriter());
	}
}
