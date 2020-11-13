package bjc.utils.ioutils.format;

import java.io.*;
import java.util.*;

import bjc.utils.ioutils.ReportWriter;

/**
 * A decree that represents a single clause in a {@link GroupDecree}.
 *
 * Has a list of decrees for a body, and then a single decree as the
 * 'terminator' if this was a terminated clause.
 *
 * @author Ben Culkin
 */
public class ClauseDecree implements IDecree {
	/**
	 * The decrees that make up the body of this clause.
	 */
	public List<Decree> body;

	/**
	 * The decree that terminated this clause.
	 */
	public Decree terminator;

	/**
	 * Create a new blank clause decree.
	 *
	 */
	public ClauseDecree() {
		body = new ArrayList<>();
	}

	/**
	 * Create a new clause decree with specific contents.
	 *
	 * @param children
	 *                 The decrees to form the body of the clause.
	 */
	public ClauseDecree(Decree... children) {
		this();

		for (Decree child : children) body.add(child);
	}

	/**
	 * Create a new clause with both a body and a terminator.
	 *
	 * @param term
	 *                 The decree that terminates the clause.
	 *
	 * @param children
	 *                 The decrees that form the body of the clause.
	 */
	public ClauseDecree(Decree term, Decree... children) {
		this(children);

		this.terminator = term;
	}

	/**
	 * Add a decree to this clause.
	 *
	 * @param child
	 *              The decree to add to this clause.
	 */
	public void addChild(Decree child) {
		body.add(child);
	}

	@Override
	public String toString() {
		try (ReportWriter writer = new ReportWriter()) {
			toReportWriter(writer);

			return writer.toString();
		} catch (IOException ioex) {
			return "<IOEXCEPTION>";
		}
		// return String.format("ClauseDecree [body=%s, terminator=%s]", body,
		// terminator);
	}
	
	/**
	 * Write the string version of this decree to a report writer.
	 * @param writer The report write to write to.
	 * @throws IOException If something goes wrong
	 */
	public void toReportWriter(ReportWriter writer) throws IOException {
		String term = "<null>";
		if (terminator != null) term = terminator.toString();

		writer.writef("ClauseDecree (terminator %s)", term);
		writer.indent();
		writer.write("\n");

		int idx = 0;
		for (Decree kid : body) 
			writer.writef("Child %d: %s\n", idx, kid.toString());

		writer.dedent();
	}
}
