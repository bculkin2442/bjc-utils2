package bjc.utils.ioutils.format.directives;

import java.io.*;
import java.util.*;

import bjc.esodata.*;
import bjc.utils.ioutils.format.*;

/**
 * Implements the [ directive.
 *
 * This does varying sorts of conditional dispatches on which string to use for
 * formatting, allowing it to be based off of general conditions in varying
 * ways.
 *
 * @author Ben Culkin
 */
public class ConditionalDirective implements Directive {
	@Override
	public Edict compile(CompileContext compCTX) {
		CLModifiers mods    = compCTX.decr.modifiers;
		CLParameters params = compCTX.decr.parameters;

		// :ConfigDirectives
		GroupDecree clauses = compCTX.directives.nextGroup(compCTX.decr, "]", ";");

		ClauseDecree defClause = null;
		boolean isDefault      = false;

		for (ClauseDecree clause : clauses) {
			if (isDefault) defClause = clause;

			if (clause.terminator != null && clause.terminator.modifiers.colonMod) {
				isDefault = true;
			}
		}

		if (mods.starMod && clauses.size() > 0)defClause = clauses.clause();

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
		FIRST_SECOND, OUTPUT_TRUE, INDEX_CLAUSE
	}

	private Mode condMode;

	private boolean decrementIndex;
	private CLValue index;

	private List<CLString> clauses;
	private CLString defClause;

	// Think I might need this for something...
	@SuppressWarnings("unused")
	private CLFormatter formatter;

	public ConditionalEdict(Mode condMode, boolean decrementIndex, CLValue index,
			GroupDecree clauses, ClauseDecree defClause, CLFormatter fmt) {
		this.condMode = condMode;

		this.decrementIndex = decrementIndex;
		this.index = index;

		this.clauses = new ArrayList<>();
		for (ClauseDecree clause : clauses) {
			List<Edict> compiled = fmt.compile(clause);
			
			this.clauses.add(new CLString(compiled));
		}
		this.defClause = new CLString(fmt.compile(defClause));

		this.formatter = fmt;
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		Tape<Object> items = formCTX.items;

		try {
			switch (condMode) {
			case FIRST_SECOND: {
				Object item = items.item();
				items.right();

				boolean conditionResult = false;
				if (item == null) {
					// throw new IllegalArgumentException("No parameter provided for [
					// directive.");
				} else if (!(item instanceof Boolean)) {
					throw new IllegalFormatConversionException('[', item.getClass());
				} else {
					conditionResult = (Boolean) item;
				}

				CLString pickedFormat;
				if (conditionResult) {
					pickedFormat = clauses.get(1);
				} else {
					pickedFormat = clauses.get(0);
				}

				pickedFormat.format(formCTX);
			}
				break;
			case OUTPUT_TRUE: {
				boolean conditionResult = false;
				Object item = items.item();

				if (item == null) {
					// throw new IllegalArgumentException("No parameter provided for [
					// directive.");
				} else if (item instanceof Integer) {
					if ((Integer) item != 0) {
						conditionResult = true;
					}
				} else if (item instanceof Boolean) {
					conditionResult = (Boolean) item;
				} else {
					throw new IllegalFormatConversionException('[', item.getClass());
				}

				if (conditionResult) {
					clauses.get(0).format(formCTX);
				} else {
					items.right();
				}
			}
				break;
			case INDEX_CLAUSE: {
				int clauseIndex;

				if (index != null) {
					clauseIndex = index.asInt(items, "conditional choice", "[", 0);
				} else {
					Object item = items.item();

					if (item == null) {
						throw new IllegalArgumentException(
								"No parameter provided for [ directive.");
					} else if (!(item instanceof Number)) {
						throw new IllegalFormatConversionException('[', item.getClass());
					}

					clauseIndex = ((Number) item).intValue();

					items.right();
				}

				if (decrementIndex)
					clauseIndex -= 1;

				if (clauses.size() == 0 || clauseIndex < 0 || clauseIndex >= clauses.size()) {
					if (defClause != null) defClause.format(formCTX.writer, items);
				} else {
					CLString frmt = clauses.get(clauseIndex);

					frmt.format(formCTX.writer, items);
				}
			}
				break;
			default:
				// IMPROVE Should probably handle this
				// -- Ben Culkin, 4/14/2020
				break;
			}
		} catch (DirectiveEscape dex) {
			// Conditionals are transparent to iteration-escapes
			throw dex;
		}
	}
}
