package bjc.utils.ioutils.format;

import java.io.*;
import java.util.*;

import bjc.utils.ioutils.ReportWriter;

/**
 * A decree that represents a single clause in a {@link GroupDecree}.
 *
 * Has a list of decrees for a body, and then a single decree as the 'terminator' if this was a
 * terminated clause.
 *
 * @author Ben Culkin
 */
public class ClauseDecree {
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
	 * 	The decrees to form the body of the clause.
	 */
	public ClauseDecree(Decree... children) {
		this();

		for (Decree child : children) {
			body.add(child);
		}
	}

	/**
	 * Create a new clause with both a body and a terminator.
	 *
	 * @param term
	 * 	The decree that terminates the clause.
	 * 
	 * @param children
	 * 	The decrees that form the body of the clause.
	 */
	public ClauseDecree(Decree term, Decree... children) {
		this(children);

		this.terminator = term;
	}

	/**
	 * Add a decree to this clause.
	 *
	 * @param child
	 * 	The decree to add to this clause.
	 */
	public void addChild(Decree child) {
		body.add(child);
	}

	@Override
	public String toString() {
		try (ReportWriter rw = new ReportWriter()) {
			String term = "<null>";
			if (terminator != null) term = terminator.toString();

			rw.write("ClauseDecree (terminator " + term.toString() + ")");
			rw.indent();
			rw.write("\n");

			for (Decree kid : body) {
				rw.write("Child: " + kid.toString() + "\n");
			}

			rw.dedent();

			return rw.toString();
		} catch (IOException ioex) {
			return "<IOEXCEPTION>";
		}
		// return String.format("ClauseDecree [body=%s, terminator=%s]", body, terminator);
	}
}
