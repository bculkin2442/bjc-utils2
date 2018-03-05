package bjc.utils.ioutils.format.directives;

import bjc.utils.esodata.Tape;
import bjc.utils.ioutils.format.CLFormatter;
import bjc.utils.ioutils.format.CLModifiers;
import bjc.utils.ioutils.format.CLParameters;

import java.util.ArrayList;
import java.util.IllegalFormatConversionException;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Implements the [ directive.
 * 
 * @author student
 *
 */
public class ConditionalDirective implements Directive {

	@Override
	public void format(StringBuffer sb, Object item, CLModifiers mods, CLParameters arrParams,
			Tape<Object> formatParams, Matcher dirMatcher, CLFormatter fmt) {
		StringBuffer condBody = new StringBuffer();

		List<String> clauses = new ArrayList<>();
		String defClause = null;
		boolean isDefault = false;

		while (dirMatcher.find()) {
			/* Process a list of clauses. */
			String dirName = dirMatcher.group("name");
			String dirMods = dirMatcher.group("modifiers");

			if (dirName != null) {
				/* Append everything up to this directive. */
				dirMatcher.appendReplacement(condBody, "");

				if (dirName.equals("]")) {
					/* End the conditional. */
					String clause = condBody.toString();
					if (isDefault) {
						defClause = clause;
					} else {
						clauses.add(clause);
					}

					break;
				} else if (dirName.equals(";")) {
					/* End the clause. */
					String clause = condBody.toString();
					if (isDefault) {
						defClause = clause;
					} else {
						clauses.add(clause);
					}

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
			}
		}

		Object par = formatParams.item();
		if (mods.colonMod) {
			formatParams.right();

			if (par == null) {
				throw new IllegalArgumentException("No parameter provided for [ directive.");
			} else if (!(par instanceof Boolean)) {
				throw new IllegalFormatConversionException('[', par.getClass());
			}
			boolean res = (Boolean) par;

			String frmt;
			if (res)
				frmt = clauses.get(1);
			else
				frmt = clauses.get(0);

			fmt.doFormatString(frmt, sb, formatParams);
		} else if (mods.atMod) {
			if (par == null) {
				throw new IllegalArgumentException("No parameter provided for [ directive.");
			} else if (!(par instanceof Boolean)) {
				throw new IllegalFormatConversionException('[', par.getClass());
			}
			boolean res = (Boolean) par;

			if (res) {
				fmt.doFormatString(clauses.get(0), sb, formatParams);
			} else {
				formatParams.right();
			}
		} else {
			int res;
			if (arrParams.length() >= 1) {
				res = arrParams.getInt(0, "conditional choice", '[');
			} else {
				if (par == null) {
					throw new IllegalArgumentException("No parameter provided for [ directive.");
				} else if (!(par instanceof Number)) {
					throw new IllegalFormatConversionException('[', par.getClass());
				}
				res = ((Number) par).intValue();

				formatParams.right();
			}

			if (res < 0 || res > clauses.size()) {
				if (defClause != null)
					fmt.doFormatString(defClause, sb, formatParams);
			} else {
				fmt.doFormatString(clauses.get(res), sb, formatParams);
			}
		}
		return;
	}

}
