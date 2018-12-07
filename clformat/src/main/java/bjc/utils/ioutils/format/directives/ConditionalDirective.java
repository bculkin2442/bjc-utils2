package bjc.utils.ioutils.format.directives;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IllegalFormatConversionException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

import bjc.utils.ioutils.format.CLPattern;
import bjc.utils.ioutils.format.EscapeException;

/**
 * Implements the [ directive.
 * 
 * @author student
 *
 */
public class ConditionalDirective implements Directive {
	@Override
	public void format(FormatParameters dirParams) throws IOException {
		StringBuilder condBody = new StringBuilder();

		List<String> clauses = new ArrayList<>();

		String defClause  = null;
		boolean isDefault = false;

		int nestLevel = 1;

		Iterator<String> dirIter = dirParams.dirIter;
		while (dirIter.hasNext()) {
			String direc = dirIter.next();
			if (!direc.startsWith("~")) {
				condBody.append(direc);
				continue;
			}

			Matcher dirMat = CLPattern.getDirectiveMatcher(direc);
			/* Process a list of clauses. */
			String dirName = dirMat.group("name");
			String dirMods = dirMat.group("modifiers");

			if (dirName != null) {
				if (dirName.equals("[")) {
					if (nestLevel > 0) {
						condBody.append(dirMat.group());
					}
					nestLevel += 1;
				} else if (Directive.isOpening(dirName)) {
					nestLevel += 1;

					condBody.append(dirMat.group());
				} else if (dirName.equals("]")) {
					nestLevel = Math.max(0, nestLevel - 1);

					if (nestLevel == 0) {
						/* End the conditional. */
						String clause = condBody.toString();
						condBody      = new StringBuilder();

						if (isDefault) {
							defClause = clause;
						}
						clauses.add(clause);

						break;
					} else {
						/* Not a special directive. */
						condBody.append(dirMat.group());
					}
				} else if (Directive.isClosing(dirName)) {
					nestLevel = Math.max(0, nestLevel - 1);

					condBody.append(dirMat.group());
				} else if (dirName.equals(";")) {
					if (nestLevel == 1) {
						/* End the clause. */
						String clause = condBody.toString();
						condBody      = new StringBuilder();

						if (isDefault) {
							defClause = clause;
						}
						clauses.add(clause);

						/*
						 * Mark the next clause as the default.
						 */
						if (dirMods.contains(":")) {
							isDefault = true;
						}
					} else {
						/* Not a special directive. */
						condBody.append(dirMat.group());
					}
				} else {
					/* Not a special directive. */
					condBody.append(dirMat.group());
				}
			}
		}
		
		if (dirParams.mods.starMod && clauses.size() > 0) defClause = clauses.get(0);

		try {
			if (dirParams.mods.colonMod) {
				dirParams.tParams.right();

				boolean res = false;
				if (dirParams.item == null) {
					//throw new IllegalArgumentException("No parameter provided for [ directive.");
				} else if (!(dirParams.item instanceof Boolean)) {
					throw new IllegalFormatConversionException('[', dirParams.item.getClass());
				} else {
					res = (Boolean) dirParams.item;
				}

				String frmt;
				if (res)
					frmt = clauses.get(1);
				else
					frmt = clauses.get(0);

				dirParams.fmt.doFormatString(frmt, dirParams.rw, dirParams.tParams, false);
			} else if (dirParams.mods.atMod) {
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
				if (dirParams.arrParams.length() >= 1) {
					dirParams.arrParams.mapIndices("choice");

					res = dirParams.arrParams.getInt("choice", "conditional choice", "[", 0);
				} else {
					if (dirParams.item == null) {
						throw new IllegalArgumentException("No parameter provided for [ directive.");
					} else if (!(dirParams.item instanceof Number)) {
						throw new IllegalFormatConversionException('[', dirParams.item.getClass());
					}
					res = ((Number) dirParams.item).intValue();

					dirParams.tParams.right();
				}

				if (dirParams.mods.dollarMod) res -= 1;

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
					String frmt = clauses.get(res);

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
