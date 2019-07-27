package bjc.utils.ioutils.format.directives;

import bjc.utils.esodata.*;
import bjc.utils.ioutils.*;
import bjc.utils.ioutils.format.*;

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

	public FormatContext(ReportWriter rw, Tape<Object> itms) {
		writer = rw;

		items = itms;
	}
}
