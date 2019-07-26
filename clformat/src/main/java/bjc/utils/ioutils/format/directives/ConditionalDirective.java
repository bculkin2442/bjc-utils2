package bjc.utils.ioutils.format.directives;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import bjc.utils.ioutils.format.*;

/**
 * Implements the [ directive.
 * 
 * @author student
 *
 */
public class ConditionalDirective implements Directive {
	@Override
	public void format(FormatParameters dirParams) throws IOException {
		CLModifiers mods = dirParams.getMods();
		CLParameters params = dirParams.getParams();

		List<Decree> condBody = new ArrayList<>();

		List<List<Decree>> clauses = new ArrayList<>();

		List<Decree> defClause  = null;
		boolean isDefault = false;

		int nestLevel = 1;

		Iterator<Decree> dirIter = dirParams.dirIter;
		while (dirIter.hasNext()) {
			Decree decr = dirIter.next();
			if (decr.isLiteral) {
				condBody.add(decr);
				continue;
			}

			String dirName = decr.name;
			if (dirName != null) {
				if (dirName.equals("[")) {
					if (nestLevel > 0) {
						condBody.add(decr);
					}
					nestLevel += 1;
				} else if (Directive.isOpening(dirName)) {
					nestLevel += 1;

					condBody.add(decr);
				} else if (dirName.equals("]")) {
					nestLevel = Math.max(0, nestLevel - 1);

					if (nestLevel == 0) {
						/* End the conditional. */
						List<Decree> clause = condBody;

						condBody = new ArrayList<>();

						if (isDefault) {
							defClause = clause;
						}
						clauses.add(clause);

						break;
					} else {
						/* Not a special directive. */
						condBody.add(decr);
					}
				} else if (Directive.isClosing(dirName)) {
					nestLevel = Math.max(0, nestLevel - 1);

					condBody.add(decr);
				} else if (dirName.equals(";")) {
					if (nestLevel == 1) {
						/* End the clause. */
						List<Decree> clause = condBody;

						condBody = new ArrayList<>();

						if (isDefault) {
							defClause = clause;
						}
						clauses.add(clause);

						/*
						 * Mark the next clause as the default.
						 */
						if (decr.modifiers.colonMod) {
							isDefault = true;
						}
					} else {
						/* Not a special directive. */
						condBody.add(decr);
					}
				} else {
					/* Not a special directive. */
					condBody.add(decr);
				}
			}
		}
		
		if (mods.starMod && clauses.size() > 0) defClause = clauses.get(0);

		try {
			if (mods.colonMod) {
				dirParams.tParams.right();

				boolean res = false;
				if (dirParams.item == null) {
					//throw new IllegalArgumentException("No parameter provided for [ directive.");
				} else if (!(dirParams.item instanceof Boolean)) {
					throw new IllegalFormatConversionException('[', dirParams.item.getClass());
				} else {
					res = (Boolean) dirParams.item;
				}

				List<Decree> frmt;
				if (res)
					frmt = clauses.get(1);
				else
					frmt = clauses.get(0);

				dirParams.fmt.doFormatString(frmt, dirParams.rw, dirParams.tParams, false);
			} else if (mods.atMod) {
				boolean res = false;
				if (dirParams.item == null) {
					// throw new IllegalArgumentException("No parameter provided for [ directive.");
				} else if (dirParams.item instanceof Integer) {
					if ((Integer)dirParams.item != 0) res = true;
				} else if (dirParams.item instanceof Boolean) {
					res = (Boolean) dirParams.item;
				} else {
					throw new IllegalFormatConversionException('[', dirParams.item.getClass());
				}

				if (res) {
					dirParams.fmt.doFormatString(clauses.get(0), dirParams.rw, dirParams.tParams, false);
				} else {
					dirParams.tParams.right();
				}
			} else {
				int res;
				if (params.length() >= 1) {
					params.mapIndices("choice");

					res = params.getInt(dirParams.tParams, 
							"choice", "conditional choice", "[", 0);
				} else {
					if (dirParams.item == null) {
						throw new IllegalArgumentException("No parameter provided for [ directive.");
					} else if (!(dirParams.item instanceof Number)) {
						throw new IllegalFormatConversionException('[', dirParams.item.getClass());
					}
					res = ((Number) dirParams.item).intValue();

					dirParams.tParams.right();
				}

				if (mods.dollarMod) res -= 1;

				// System.err.printf("Attempting selection of clause %d of %d (%s) (default %s)\n",
				//		res, clauses.size(), clauses, defClause);
				if (clauses.size() == 0 || res < 0 || res >= clauses.size()) {
					// System.err.printf("Selecting default clause (res %d, max %d): %s\n", res, clauses.size(), defClause);
					// int clauseNo = 0;
					// for (String clause : clauses) {
						// System.err.printf("... clause %d: %s\n", ++clauseNo, clause); 
					// }

					if (defClause != null) dirParams.fmt.doFormatString(defClause, dirParams.rw, dirParams.tParams, false);
				} else {
					List<Decree> frmt = clauses.get(res);

					// System.out.printf("Selecting clause %d of %d (params %s): %s\n", res, clauses.size(), formatParams, frmt);
					dirParams.fmt.doFormatString(frmt, dirParams.rw, dirParams.tParams, false);
				}
			}
		} catch (EscapeException eex) {
			// @NOTE 9/5/18
			//
			// I am not sure if it is valid to error here. I'm not
			// even sure that we need to handle this here, but I
			// dunno
			//if (eex.endIteration)
			//	throw new UnsupportedOperationException("Colon mod not allowed on escape marker without colon mod on iteration");
			throw eex;
		}

		return;
	}

}
