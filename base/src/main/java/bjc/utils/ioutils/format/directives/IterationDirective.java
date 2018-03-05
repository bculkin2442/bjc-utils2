package bjc.utils.ioutils.format.directives;

import bjc.utils.esodata.SingleTape;
import bjc.utils.esodata.Tape;
import bjc.utils.ioutils.format.CLFormatter;
import bjc.utils.ioutils.format.CLModifiers;
import bjc.utils.ioutils.format.CLParameters;

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
	public void format(StringBuffer sb, Object item, CLModifiers mods, CLParameters arrParams, Tape<Object> tParams,
			Matcher dirMatcher, CLFormatter fmt) {
		CLFormatter.checkItem(item, '{');

		StringBuffer condBody = new StringBuffer();

		while (dirMatcher.find()) {
			/* Process a list of clauses. */
			String dirName = dirMatcher.group("name");

			if (dirName != null) {
				/* Append everything up to this directive. */
				dirMatcher.appendReplacement(condBody, "");

				if (dirName.equals("}")) {
					/* End the iteration. */
					break;
				}

				/* Not a special directive. */
				condBody.append(dirMatcher.group());
			}
		}

		String frmt = condBody.toString();
		Object iter = item;

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
			do {
				if (numItr > maxItr)
					break;
				numItr += 1;

				if (!(iter instanceof Iterable<?>)) {
					throw new IllegalFormatConversionException('{', iter.getClass());
				}

				@SuppressWarnings("unchecked")
				Iterable<Object> nitr = (Iterable<Object>) iter;
				Tape<Object> nParams = new SingleTape<>(nitr);

				fmt.doFormatString(frmt, sb, nParams);

				iter = tParams.right();
			} while (tParams.position() < tParams.size());
		} else if (mods.atMod) {
			while (tParams.position() < tParams.size()) {
				if (numItr > maxItr)
					break;
				numItr += 1;

				fmt.doFormatString(frmt, sb, tParams);
			}
		} else if (mods.colonMod) {
			if (!(item instanceof Iterable<?>)) {
				throw new IllegalFormatConversionException('{', item.getClass());
			}

			@SuppressWarnings("unchecked")
			Iterable<Object> itr = (Iterable<Object>) item;

			for (Object obj : itr) {
				if (numItr > maxItr)
					break;
				numItr += 1;

				if (!(obj instanceof Iterable<?>)) {
					throw new IllegalFormatConversionException('{', obj.getClass());
				}

				@SuppressWarnings("unchecked")
				Iterable<Object> nitr = (Iterable<Object>) obj;
				Tape<Object> nParams = new SingleTape<>(nitr);

				fmt.doFormatString(frmt, sb, nParams);
			}
		} else {
			if (!(item instanceof Iterable<?>)) {
				throw new IllegalFormatConversionException('{', item.getClass());
			}

			@SuppressWarnings("unchecked")
			Iterable<Object> itr = (Iterable<Object>) item;

			Tape<Object> nParams = new SingleTape<>(itr);

			while (nParams.position() < nParams.size()) {
				if (numItr > maxItr)
					break;
				numItr += 1;

				fmt.doFormatString(frmt, sb, nParams);
			}
		}

		tParams.right();
	}

}
