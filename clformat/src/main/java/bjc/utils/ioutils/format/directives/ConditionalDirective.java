package bjc.utils.ioutils.format.directives;

import bjc.utils.esodata.Tape;
import bjc.utils.ioutils.format.*;
import bjc.utils.ioutils.ReportWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IllegalFormatConversionException;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Implements the [ directive.
 * 
 * @author student
 *
 */
public class ConditionalDirective implements Directive {
	private static Logger LOG = Logger.getLogger(ConditionalDirective.class.getName());

	@Override
	public void format(ReportWriter rw, Object item, CLModifiers mods, CLParameters arrParams,
			Tape<Object> formatParams, Matcher dirMatcher, CLFormatter fmt) throws IOException {
		StringBuffer condBody = new StringBuffer();

		List<String> clauses = new ArrayList<>();

		String defClause  = null;
		boolean isDefault = false;

		int nestLevel = 1;

		while (dirMatcher.find()) {
			/* Process a list of clauses. */
			String dirName = dirMatcher.group("name");
			String dirMods = dirMatcher.group("modifiers");

			//System.err.printf("Found conditional directive %s with %s mods and level %d\n", dirName, dirMods, nestLevel);
			if (dirName != null) {
				/* Append everything up to this directive. */
				dirMatcher.appendReplacement(condBody, "");

				if (dirName.equals("[")) {
					if (nestLevel > 0) {
						condBody.append(dirMatcher.group());
					}
					nestLevel += 1;
				} else if (Directive.isOpening(dirName)) {
					nestLevel += 1;

					condBody.append(dirMatcher.group());
				} else if (dirName.equals("]")) {
					nestLevel = Math.max(0, nestLevel - 1);

					if (nestLevel == 0) {
						/* End the conditional. */
						String clause = condBody.toString();
						// System.err.printf("Found clause \"%s]\"\n", clause);
						condBody      = new StringBuffer();

						if (isDefault) {
							defClause = clause;
						}
						clauses.add(clause);

						break;
					} else {
						/* Not a special directive. */
						condBody.append(dirMatcher.group());
					}
				} else if (Directive.isClosing(dirName)) {
					nestLevel = Math.max(0, nestLevel - 1);

					condBody.append(dirMatcher.group());
				} else if (dirName.equals(";")) {
					if (nestLevel == 1) {
						/* End the clause. */
						String clause = condBody.toString();
						// System.err.printf("Found clause \"%s;\"\n", clause);
						condBody      = new StringBuffer();

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
						condBody.append(dirMatcher.group());
					}
				} else {
					/* Not a special directive. */
					condBody.append(dirMatcher.group());
				}
			}
		}
		
		if (mods.starMod && clauses.size() > 0) defClause = clauses.get(0);

		try {
			if (mods.colonMod) {
				formatParams.right();

				boolean res = false;
				if (item == null) {
					//throw new IllegalArgumentException("No parameter provided for [ directive.");
				} else if (!(item instanceof Boolean)) {
					throw new IllegalFormatConversionException('[', item.getClass());
				} else {
					res = (Boolean) item;
				}

				String frmt;
				if (res)
					frmt = clauses.get(1);
				else
					frmt = clauses.get(0);

				fmt.doFormatString(frmt, rw, formatParams, false);
			} else if (mods.atMod) {
				boolean res = false;
				if (item == null) {
					// throw new IllegalArgumentException("No parameter provided for [ directive.");
				} else if (item instanceof Integer) {
					if ((Integer)item != 0) res = true;
				} else if (item instanceof Boolean) {
					res = (Boolean) item;
				} else {
					throw new IllegalFormatConversionException('[', item.getClass());
				}

				if (res) {
					fmt.doFormatString(clauses.get(0), rw, formatParams, false);
				} else {
					formatParams.right();
				}
			} else {
				int res;
				if (arrParams.length() >= 1) {
					res = arrParams.getInt(0, "conditional choice", '[');
				} else {
					if (item == null) {
						throw new IllegalArgumentException("No parameter provided for [ directive.");
					} else if (!(item instanceof Number)) {
						throw new IllegalFormatConversionException('[', item.getClass());
					}
					res = ((Number) item).intValue();

					formatParams.right();
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

					if (defClause != null) fmt.doFormatString(defClause, rw, formatParams, false);
				} else {
					String frmt = clauses.get(res);

					// System.out.printf("Selecting clause %d of %d (params %s): %s\n", res, clauses.size(), formatParams, frmt);
					fmt.doFormatString(frmt, rw, formatParams, false);
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
