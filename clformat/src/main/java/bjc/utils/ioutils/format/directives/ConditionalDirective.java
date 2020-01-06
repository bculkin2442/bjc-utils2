package bjc.utils.ioutils.format.directives;

import java.io.*;
import java.util.*;
import bjc.utils.esodata.*;
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
		CLModifiers mods = compCTX.decr.modifiers;
		CLParameters params = compCTX.decr.parameters;

		List<Decree> condBody = new ArrayList<>();
		List<List<Decree>> clauses = new ArrayList<>();

		List<Decree> defClause = null;
		boolean isDefault = false;

		int nestLevel = 1;

		Iterator<Decree> dirIter = compCTX.directives;
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
						 * Mark the next clause as the
						 * default.
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

		return new ConditionalEdict(mode, mods.dollarMod, index, clauses, defClause, compCTX.formatter);
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

	public ConditionalEdict(Mode condMode, boolean decrementIndex, CLValue index, List<List<Decree>> clauses,
			List<Decree> defClause, CLFormatter fmt) {
		this.condMode = condMode;

		this.decrementIndex = decrementIndex;
		this.index = index;

		this.clauses = new ArrayList<>();
		for (List<Decree> clause : clauses) {
			this.clauses.add(new CLString(fmt.compile(clause)));
		}
		this.defClause = new CLString(fmt.compile(defClause));
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		Tape<Object> items = formCTX.items;

		try {
			switch (condMode) {
			case FIRST_SECOND: {
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
			case OUTPUT_TRUE: {
				boolean res = false;
				Object o = items.item();

				if (o == null) {
					// throw new IllegalArgumentException("No parameter provided for [ directive.");
				} else if (o instanceof Integer) {
					if ((Integer) o != 0) {
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
			case INDEX_CLAUSE: {
				int res;

				if (index != null) {
					res = index.asInt(items, "conditional choice", "[", 0);
				} else {
					Object o = items.item();

					if (o == null) {
						throw new IllegalArgumentException(
								"No parameter provided for [ directive.");
					} else if (!(o instanceof Number)) { throw new IllegalFormatConversionException(
							'[', o.getClass()); }

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
			default:
				throw new IllegalArgumentException("INTERNAL ERROR: ConditionalEdict mode " + condMode
						+ " is not supported. This is a bug.");
			}
		} catch (EscapeException eex) {
			// Conditionals are transparent to iteration-escapes
			throw eex;
		}
	}
}
