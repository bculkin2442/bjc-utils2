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
		CLFormatter.checkItem(dirParams.item, '{');

		List<Decree> condBody = new ArrayList<>();

		Iterator<Decree> dirIter = dirParams.dirIter;
		while (dirIter.hasNext()) {
			Decree decr = dirIter.next();
			if (decr.isLiteral) {
				condBody.add(decr);
				continue;
			}

			String dirName = decr.name;

			if (dirName != null) {
				if (dirName.equals("}")) {
					break;
				} else {
					/* Not a special directive. */
					condBody.add(decr);
				}
			}
		}

		Object iter = dirParams.item;

		boolean usingString = false;
		String strang = "";

		if (condBody.size() == 0) {
			/* Grab an argument. */
			if (!(dirParams.item instanceof String)) {
				throw new IllegalFormatConversionException('{', String.class);
			}

			usingString = true;
			strang = (String) dirParams.item;

			if (!dirParams.tParams.right()) {
				throw new IllegalArgumentException("Not enough parameters to '{' directive");
			}

			iter = dirParams.tParams.item();
		}

		int maxItr = Integer.MAX_VALUE;

		CLParameters params = dirParams.getParams();
		CLModifiers mods = dirParams.getMods();

		if (params.length() > 0) {
			params.mapIndices("maxitr");

			maxItr = params.getInt(dirParams.tParams, "maxitr",
					"maximum iterations", "{", Integer.MAX_VALUE);
		}

		int numItr = 0;

		if (mods.atMod &&mods.colonMod) {
			try {
				do {
					if (numItr > maxItr) break;
					numItr += 1;

					if (!(iter instanceof Iterable<?>)) {
						throw new IllegalFormatConversionException('{', iter.getClass());
					}

					@SuppressWarnings("unchecked")
					Iterable<Object> nitr = (Iterable<Object>) iter;
					Tape<Object> nParams = new SingleTape<>(nitr);

					try {
						if (usingString) {
						dirParams.fmt.doFormatString(strang, dirParams.rw, nParams, false);
						} else {
						dirParams.fmt.doFormatString(condBody, dirParams.rw, nParams, false);
						}
					} catch (EscapeException eex) {
						if (eex.endIteration) {
							if (dirParams.tParams.atEnd()) {
								throw eex;
							}
						}
					}

					dirParams.tParams.right();
					iter = dirParams.tParams.item();
				} while (dirParams.tParams.position() < dirParams.tParams.size());
			} catch (EscapeException eex) {
				// Do nothing
			}
		} else if (mods.atMod) {
			try {
				while (!dirParams.tParams.atEnd()) {
					// System.err.printf("Iterating with format \"%s\"\n", frmt);
					if (numItr > maxItr) break;
					numItr += 1;

					if (usingString) {
					dirParams.fmt.doFormatString(strang, dirParams.rw, dirParams.tParams, false);
					} else {
					dirParams.fmt.doFormatString(condBody, dirParams.rw, dirParams.tParams, false);
					}
				}
			} catch (EscapeException eex) {
				if (eex.endIteration)
					throw new UnsupportedOperationException("Colon mod not allowed on escape marker without colon mod on iteration");
			}
		} else if (mods.colonMod) {
			if (!(dirParams.item instanceof Iterable<?>)) {
				throw new IllegalFormatConversionException('{', dirParams.item.getClass());
			}

			try {
				@SuppressWarnings("unchecked")
				Iterable<Object> itb = (Iterable<Object>) dirParams.item;
				Iterator<Object> itr = itb.iterator();
				while (itr.hasNext()) {
					Object obj = itr.next();

					if (numItr > maxItr) break;
					numItr += 1;

					if (!(obj instanceof Iterable<?>)) {
						throw new IllegalFormatConversionException('{', obj.getClass());
					}

					@SuppressWarnings("unchecked")
					Iterable<Object> nitr = (Iterable<Object>) obj;
					Tape<Object> nParams = new SingleTape<>(nitr);

					try {
						if (usingString) {
						dirParams.fmt.doFormatString(strang, dirParams.rw, nParams, false);
						} else {
						dirParams.fmt.doFormatString(condBody, dirParams.rw, nParams, false);
						}
					} catch (EscapeException eex) {
						if(eex.endIteration && !itr.hasNext()) throw eex;
					}
				}
			} catch (EscapeException eex) {
				// Do nothing
			}
		} else {
			if (!(dirParams.item instanceof Iterable<?>)) {
				throw new IllegalFormatConversionException('{', dirParams.item.getClass());
			}

			try {
				@SuppressWarnings("unchecked")
				Iterable<Object> itr = (Iterable<Object>) dirParams.item;
				Tape<Object> nParams = new SingleTape<>(itr);

				while (!nParams.atEnd()) {
					if (numItr > maxItr) break;
					numItr += 1;

					if (usingString) {
					dirParams.fmt.doFormatString(strang, dirParams.rw, nParams, false);
					} else {
					dirParams.fmt.doFormatString(condBody, dirParams.rw, nParams, false);
					}
				}
			} catch (EscapeException eex) {
				if (eex.endIteration)
					throw new UnsupportedOperationException("Colon mod not allowed on escape marker without colon mod on iteration");
			}
		}

		dirParams.tParams.right();
	}
}
