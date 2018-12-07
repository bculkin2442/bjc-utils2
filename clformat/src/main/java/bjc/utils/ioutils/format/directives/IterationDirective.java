package bjc.utils.ioutils.format.directives;

import java.io.IOException;
import java.util.IllegalFormatConversionException;
import java.util.Iterator;
import java.util.regex.Matcher;

import bjc.utils.esodata.SingleTape;
import bjc.utils.esodata.Tape;
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

		StringBuilder condBody = new StringBuilder();

		Iterator<String> dirIter = dirParams.dirIter;
		while (dirIter.hasNext()) {
			String direc = dirIter.next();
			if (!direc.startsWith("~")) {
				condBody.append(direc);
				continue;
			}

			Matcher dirMat = CLPattern.getDirectiveMatcher(direc);
			dirMat.find();
			/* Process a list of clauses. */
			String dirName = dirMat.group("name");

			if (dirName != null) {
				if (dirName.equals("}")) {
					break;
				} else {
					/* Not a special directive. */
					condBody.append(dirMat.group());
				}
			}
		}

		String frmt = condBody.toString();
		Object iter = dirParams.item;

		// System.err.printf("Iteration format \"%s\" (iter %s)\n", frmt, item);
		if (frmt.equals("")) {
			/* Grab an argument. */
			if (!(dirParams.item instanceof String)) {
				throw new IllegalFormatConversionException('{', String.class);
			}

			frmt = (String) dirParams.item;

			if (!dirParams.tParams.right()) {
				throw new IllegalArgumentException("Not enough parameters to '{' directive");
			}

			iter = dirParams.tParams.item();
		}

		int maxItr = Integer.MAX_VALUE;

		CLParameters params = dirParams.arrParams;
		if (params.length() > 0) {
			params.mapIndices("maxitr");

			maxItr = params.getInt("maxitr", "maximum iterations", "{", Integer.MAX_VALUE);
		}

		int numItr = 0;

		if (dirParams.mods.atMod && dirParams.mods.colonMod) {
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
						dirParams.fmt.doFormatString(frmt, dirParams.rw, nParams, false);
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
		} else if (dirParams.mods.atMod) {
			try {
				while (!dirParams.tParams.atEnd()) {
					// System.err.printf("Iterating with format \"%s\"\n", frmt);
					if (numItr > maxItr) break;
					numItr += 1;

					dirParams.fmt.doFormatString(frmt, dirParams.rw, dirParams.tParams, false);
				}
			} catch (EscapeException eex) {
				if (eex.endIteration)
					throw new UnsupportedOperationException("Colon mod not allowed on escape marker without colon mod on iteration");
			}
		} else if (dirParams.mods.colonMod) {
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
						dirParams.fmt.doFormatString(frmt, dirParams.rw, nParams, false);
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

					dirParams.fmt.doFormatString(frmt, dirParams.rw, nParams, false);
				}
			} catch (EscapeException eex) {
				if (eex.endIteration)
					throw new UnsupportedOperationException("Colon mod not allowed on escape marker without colon mod on iteration");
			}
		}

		dirParams.tParams.right();
	}
}
