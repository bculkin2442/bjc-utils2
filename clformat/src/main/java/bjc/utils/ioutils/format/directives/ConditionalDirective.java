package bjc.utils.ioutils.format.directives;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import bjc.utils.esodata.*;
import bjc.utils.ioutils.format.*;

/**
 * Implements the [ directive.
 * 
 * This does varying sorts of conditional dispatches on which string to use for formatting, allowing
 * it to be based off of general conditions in varying ways.
 *
 * @author Ben Culkin
 */
public class ConditionalDirective implements Directive {
	@Override
	public Edict compile(CompileContext compCTX) {
		CLModifiers mods = compCTX.decr.modifiers;
		CLParameters params = compCTX.decr.parameters;

		GroupDecree clauses = compCTX.directives.nextGroup(compCTX.decr, "]", ";");

		ClauseDecree defClause  = null;
		boolean isDefault = false;

		for (ClauseDecree clause : clauses) {
			if (isDefault) defClause = clause;

			if (clause.terminator != null && clause.terminator.modifiers.colonMod) {
				isDefault = true;
			}
		}

		if (mods.starMod && clauses.size() > 0) defClause = clauses.clause();

		CLValue index = null;

		if (params.length() >= 1) {
			params.mapIndices("choice");

			index = params.resolveKey("choice");
		}

		ConditionalEdict.Mode mode;

		if (mods.colonMod) {
			mode = ConditionalEdict.Mode.FIRST_SECOND;
		} else if (mods.atMod) {
			mode = ConditionalEdict.Mode.OUTPUT_TRUE;
		} else {
			mode = ConditionalEdict.Mode.INDEX_CLAUSE;
		}

		return new ConditionalEdict(mode, mods.dollarMod, index, clauses,
				defClause, compCTX.formatter);
	}
}

class ConditionalEdict implements Edict {
	public static enum Mode {
		FIRST_SECOND,
		OUTPUT_TRUE,
		INDEX_CLAUSE
	}

	private Mode condMode;

	private boolean decrementIndex;
	private CLValue index;

	private List<CLString> clauses;
	private CLString defClause;

	private CLFormatter formatter;

	public ConditionalEdict(Mode condMode, boolean decrementIndex,
			CLValue index, GroupDecree clauses, ClauseDecree defClause,
			CLFormatter fmt) {
		this.condMode = condMode;

		this.decrementIndex = decrementIndex;
		this.index = index;

		this.clauses = new ArrayList<>();
		for (ClauseDecree clause : clauses) {
			this.clauses.add(new CLString(fmt.compile(clause)));
		}
		this.defClause = new CLString(fmt.compile(defClause));

		this.formatter = fmt;
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		Tape<Object> items = formCTX.items;

		try {
			switch (condMode) {
			case FIRST_SECOND:
				{
					Object o = items.item();
					items.right();

					boolean res = false;
					if (o == null) {
						//throw new IllegalArgumentException("No parameter provided for [ directive.");
					} else if (!(o instanceof Boolean)) {
						throw new IllegalFormatConversionException('[', o.getClass());
					} else {
						res = (Boolean) o;
					}

					CLString frmt;
					if (res) {
						frmt = clauses.get(1);
					} else {
						frmt = clauses.get(0);
					}

					frmt.format(formCTX);
				}
				break;
			case OUTPUT_TRUE:
				{
					boolean res = false;
					Object o = items.item();

					if (o == null) {
						// throw new IllegalArgumentException("No parameter provided for [ directive.");
					} else if (o instanceof Integer) {
						if ((Integer)o != 0) {
							res = true;
						}
					} else if (o instanceof Boolean) {
						res = (Boolean) o;
					} else {
						throw new IllegalFormatConversionException('[', o.getClass());
					}

					if (res) {
						clauses.get(0).format(formCTX);
					} else {
						items.right();
					}
				}
				break;
			case INDEX_CLAUSE:
				{
					int res;

					if (index != null) {
						res = index.asInt(items, "conditional choice", "[", 0);
					} else {
						Object o = items.item();

						if (o == null) {
							throw new IllegalArgumentException("No parameter provided for [ directive.");
						} else if (!(o instanceof Number)) {
							throw new IllegalFormatConversionException('[', o.getClass());
						}

						res = ((Number) o).intValue();

						items.right();
					}

					if (decrementIndex) res -= 1;

					if (clauses.size() == 0 || res < 0 || res >= clauses.size()) {
						if (defClause != null) {
							defClause.format(formCTX.writer, items);
						}
					} else {
						CLString frmt = clauses.get(res);

						frmt.format(formCTX.writer, items);
					}
				}
				break;
			}
		} catch (DirectiveEscape dex) {
			// Conditionals are transparent to iteration-escapes
			throw dex;
		}
	}
}
