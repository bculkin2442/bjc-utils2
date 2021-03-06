package bjc.utils.ioutils.format.directives;

import bjc.esodata.*;
import bjc.utils.ioutils.ReportWriter;
import bjc.utils.ioutils.format.*;

/**
 * The set of parameters used during formatting.
 *
 * Since the refactor to use compilation (e.g {@link CLString} and it's ilk),
 * this is now mostly used as a way of creating the {@link FormatContext} and
 * {@link CompileContext} objects.
 *
 * @author bjculkin
 *
 */
public class FormatParameters {
	/**
	 * The {@link ReportWriter} used for output.
	 */
	public ReportWriter writer;

	/**
	 * The current format parameter.
	 */
	public Object item;

	/**
	 * The current decree.
	 */
	public SimpleDecree decr;

	/**
	 * The current format parameters.
	 */
	public Tape<Object> tParams;

	/**
	 * The set of directives we are using.
	 */
	public CLTokenizer dirIter;

	/**
	 * The formatter we are going from.
	 */
	public CLFormatter formatter;

	/**
	 * Create a new set of format parameters.
	 *
	 * @param writer
	 *                The writer we are sending output to.
	 *
	 * @param item
	 *                The current format parameter.
	 *
	 * @param decr
	 *                The decree being formatted.
	 *
	 * @param tParams
	 *                The list of all the format parameters.
	 *
	 * @param dirIter
	 *                The set of format decrees.
	 *
	 * @param formatter
	 *                The formatter we are using
	 */
	public FormatParameters(ReportWriter writer, Object item, SimpleDecree decr,
			Tape<Object> tParams, CLTokenizer dirIter, CLFormatter formatter) {
		this.writer = writer;

		this.item = item;

		this.decr = decr;

		this.tParams = tParams;

		this.dirIter = dirIter;

		this.formatter = formatter;
	}

	/**
	 * Get the parameters for the current decree.
	 *
	 * @return The parameters to the current decree.
	 */
	public CLParameters getParams() {
		return decr.parameters;
	}

	/**
	 * Get the modifiers for the current decree.
	 *
	 * @return The modifiers for the current decree.
	 */
	public CLModifiers getMods() {
		return decr.modifiers;
	}

	/**
	 * Convert this set of parameters into a compilation context.
	 *
	 * @return The compilation context from these parameters.
	 */
	public CompileContext toCompileCTX() {
		return new CompileContext(dirIter, formatter, decr);
	}

	/**
	 * Convert this set of parameters into a format context.
	 *
	 * @return The format context from these parameters.
	 */
	public FormatContext toFormatCTX() {
		return new FormatContext(writer, tParams);
	}
}
