package bjc.utils.ioutils.format.directives;

import bjc.utils.esodata.SingleTape;
import bjc.utils.esodata.Tape;
import bjc.utils.ioutils.format.*;
import bjc.utils.ioutils.ReportWriter;

import java.io.IOException;

import java.util.Iterator;

import java.util.IllegalFormatConversionException;
import java.util.regex.Matcher;

/**
 * Implements the { directive.
 * 
 * @author student
 *
 */
public class IterationDirective implements Directive {

	@Override
	public void format(ReportWriter rw, Object item, CLModifiers mods, CLParameters arrParams, Tape<Object> tParams,
			Matcher dirMatcher, CLFormatter fmt) throws IOException {
		CLFormatter.checkItem(item, '{');

		StringBuffer condBody = new StringBuffer();

		while (dirMatcher.find()) {
			/* Process a list of clauses. */
			String dirName = dirMatcher.group("name");

			if (dirName != null) {
				/* Append everything up to this directive. */
				dirMatcher.appendReplacement(condBody, "");

				if (dirName.equals("}")) {
					break;
				} else {
					/* Not a special directive. */
					condBody.append(dirMatcher.group());
				}
			}
		}

		String frmt = condBody.toString();
		Object iter = item;

		// System.err.printf("Iteration format \"%s\" (iter %s)\n", frmt, item);
		if (frmt.equals("")) {
			/* Grab an argument. */
			if (!(item instanceof String)) {
				throw new IllegalFormatConversionException('{', String.class);
			}

			frmt = (String) item;

			if (!tParams.right()) {
				throw new IllegalArgumentException("Not enough parameters to '{' directive");
			}

			iter = tParams.item();
		}

		int maxItr = Integer.MAX_VALUE;

		if (arrParams.length() > 0) {
			maxItr = arrParams.getInt(0, "maximum iterations", '{');
		}

		int numItr = 0;

		if (mods.atMod && mods.colonMod) {
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
						fmt.doFormatString(frmt, rw, nParams, false);
					} catch (EscapeException eex) {
						if (eex.endIteration) {
							if (tParams.atEnd()) {
								throw eex;
							}
						}
					}

					tParams.right();
					iter = tParams.item();
				} while (tParams.position() < tParams.size());
			} catch (EscapeException eex) {
			}
		} else if (mods.atMod) {
			try {
				while (!tParams.atEnd()) {
					// System.err.printf("Iterating with format \"%s\"\n", frmt);
					if (numItr > maxItr) break;
					numItr += 1;

					fmt.doFormatString(frmt, rw, tParams, false);
				}
			} catch (EscapeException eex) {
				if (eex.endIteration)
					throw new UnsupportedOperationException("Colon mod not allowed on escape marker without colon mod on iteration");
			}
		} else if (mods.colonMod) {
			if (!(item instanceof Iterable<?>)) {
				throw new IllegalFormatConversionException('{', item.getClass());
			}

			try {
				@SuppressWarnings("unchecked")
				Iterable<Object> itb = (Iterable<Object>) item;
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
						fmt.doFormatString(frmt, rw, nParams, false);
					} catch (EscapeException eex) {
						if(eex.endIteration && !itr.hasNext()) throw eex;
					}
				}
			} catch (EscapeException eex) {
			}
		} else {
			if (!(item instanceof Iterable<?>)) {
				throw new IllegalFormatConversionException('{', item.getClass());
			}

			try {
				@SuppressWarnings("unchecked")
				Iterable<Object> itr = (Iterable<Object>) item;
				Tape<Object> nParams = new SingleTape<>(itr);

				while (!nParams.atEnd()) {
					if (numItr > maxItr) break;
					numItr += 1;

					fmt.doFormatString(frmt, rw, nParams, false);
				}
			} catch (EscapeException eex) {
				if (eex.endIteration)
					throw new UnsupportedOperationException("Colon mod not allowed on escape marker without colon mod on iteration");
			}
		}

		tParams.right();
	}
}
