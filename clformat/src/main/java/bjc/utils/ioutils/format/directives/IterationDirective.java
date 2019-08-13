package bjc.utils.ioutils.format.directives;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import bjc.utils.esodata.*;
import bjc.utils.ioutils.format.*;

/**
 * Implements the { directive.
 * 
 * @author student
 *
 */
public class IterationDirective implements Directive {

	@Override
	public void format(FormatParameters dirParams) throws IOException {
		Edict edt = compile(dirParams.toCompileCTX());

		edt.format(dirParams.toFormatCTX());
	}

	@Override
	public Edict compile(CompileContext compCTX) {
		IterationEdict.Mode mode;

		List<Decree> body = new ArrayList<>();

		Iterator<Decree> dirIter = compCTX.directives;
		while (dirIter.hasNext()) {
			Decree decr = dirIter.next();
			if (decr.isLiteral) {
				body.add(decr);
				continue;
			}

			String dirName = decr.name;

			if (dirName != null) {
				if (dirName.equals("}")) {
					break;
				} else {
					/* Not a special directive. */
					body.add(decr);
				}
			}
		}

		CLParameters params = compCTX.decr.parameters;
		CLModifiers mods = compCTX.decr.modifiers;

		CLValue maxItr = CLValue.nil();
		if (params.length() > 0) {
			params.mapIndices("maxitr");

			maxItr = params.resolveKey("maxitr");
		}

		if (mods.atMod && mods.colonMod) {
			mode = IterationEdict.Mode.ALL_SUBLISTS;
		} else if (mods.atMod) {
			mode = IterationEdict.Mode.ALL;
		} else if (mods.colonMod) {
			mode = IterationEdict.Mode.SUBLIST;
		} else {
			mode = IterationEdict.Mode.NORMAL;
		}

		return new IterationEdict(mode, body, compCTX.formatter, maxItr);
	}
}

class IterationEdict implements Edict {
	private static final char DIR_NAME = '{';

	public static enum Mode {
		ALL_SUBLISTS,
		ALL,
		SUBLIST,
		NORMAL
	}

	private Mode mode;

	private List<Decree> body;

	private CLFormatter fmt;

	private CLValue maxItrVal;

	public IterationEdict(Mode mode, List<Decree> body, CLFormatter fmt, CLValue maxItr) {
		this.mode = mode;
		this.body = body;

		this.fmt = fmt;

		this.maxItrVal = maxItr;
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		int maxIterations = maxItrVal.asInt(formCTX.items, "maximum iterations", "{", Integer.MAX_VALUE);

		int numIterations = 0;

		Object iter = formCTX.items.item();

		boolean usingString = false;
		String strang = null;

		if (body.size() == 0) {
			/* Grab an argument. */
			if (!(iter instanceof String)) {
				throw new IllegalFormatConversionException('{', String.class);
			}

			usingString = true;
			strang = (String) iter;

			if (!formCTX.items.right()) {
				throw new IllegalArgumentException("Not enough parameters to '{' directive");
			}

			iter = formCTX.items.item();
		}

		switch (mode) {
		case ALL_SUBLISTS:
			try {
				do {
					if (numIterations > maxIterations) break;
					numIterations += 1;

					if (!(iter instanceof Iterable<?>)) {
						throw new IllegalFormatConversionException(DIR_NAME, iter.getClass());
					}

					@SuppressWarnings("unchecked")
					Iterable<Object> nitr = (Iterable<Object>) iter;
					Tape<Object> nParams = new SingleTape<>(nitr);

					try {
						if (usingString) {
						fmt.doFormatString(strang, formCTX.writer, nParams, false);
						} else {
						fmt.doFormatString(body, formCTX.writer, nParams, false);
						}
					} catch (EscapeException eex) {
						if (eex.endIteration) {
							if (formCTX.items.atEnd()) {
								throw eex;
							}
						}
					}

					formCTX.items.right();
					iter = formCTX.items.item();
				} while (formCTX.items.position() < formCTX.items.size());
			} catch (EscapeException eex) {
				// Do nothing
			}
			break;
		case ALL:
			try {
				while (!formCTX.items.atEnd()) {
					// System.err.printf("Iterating with format \"%s\"\n", frmt);
					if (numIterations > maxIterations) break;
					numIterations += 1;

					if (usingString) {
					fmt.doFormatString(strang, formCTX.writer, formCTX.items, false);
					} else {
					fmt.doFormatString(body, formCTX.writer, formCTX.items, false);
					}
				}
			} catch (EscapeException eex) {
				if (eex.endIteration)
					throw new UnsupportedOperationException("Colon mod not allowed on escape marker without colon mod on iteration");
			}
			break;
		case SUBLIST:
			if (!(iter instanceof Iterable<?>)) {
				throw new IllegalFormatConversionException(DIR_NAME, iter.getClass());
			}

			try {
				@SuppressWarnings("unchecked")
				Iterable<Object> itb = (Iterable<Object>) iter;
				Iterator<Object> itr = itb.iterator();
				while (itr.hasNext()) {
					Object obj = itr.next();

					if (numIterations > maxIterations) break;
					numIterations += 1;

					if (!(obj instanceof Iterable<?>)) {
						throw new IllegalFormatConversionException(DIR_NAME, obj.getClass());
					}

					@SuppressWarnings("unchecked")
					Iterable<Object> nitr = (Iterable<Object>) obj;
					Tape<Object> nParams = new SingleTape<>(nitr);

					try {
						if (usingString) {
						fmt.doFormatString(strang, formCTX.writer, nParams, false);
						} else {
						fmt.doFormatString(body, formCTX.writer, nParams, false);
						}
					} catch (EscapeException eex) {
						if(eex.endIteration && !itr.hasNext()) throw eex;
					}
				}
			} catch (EscapeException eex) {
				// Do nothing
			}
			break;
		case NORMAL:
			if (!(iter instanceof Iterable<?>)) {
				throw new IllegalFormatConversionException(DIR_NAME, iter.getClass());
			}

			try {
				@SuppressWarnings("unchecked")
				Iterable<Object> itr = (Iterable<Object>) iter;
				Tape<Object> nParams = new SingleTape<>(itr);

				while (!nParams.atEnd()) {
					if (numIterations > maxIterations) break;
					numIterations += 1;

					if (usingString) {
					fmt.doFormatString(strang, formCTX.writer, nParams, false);
					} else {
					fmt.doFormatString(body, formCTX.writer, nParams, false);
					}
				}
			} catch (EscapeException eex) {
				if (eex.endIteration)
					throw new UnsupportedOperationException("Colon mod not allowed on escape marker without colon mod on iteration");
			}
			break;
		default:
			throw new IllegalArgumentException("Unimplemented iteration mode " + mode);
		}

		formCTX.items.right();
	}
}
