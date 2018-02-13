package bjc.utils.ioutils.format;

import bjc.utils.esodata.Tape;

import java.util.IllegalFormatConversionException;
import java.util.regex.Matcher;

class IterationDirective implements Directive {

	@Override
	public void format(StringBuffer sb, Object item, CLModifiers mods, CLParameters arrParams, Tape<Object> tParams,
			Matcher dirMatcher, CLFormatter fmt) {
		CLFormatter.checkItem(item, '{');

		StringBuffer condBody = new StringBuffer();

		while(dirMatcher.find()) {
			/* Process a list of clauses. */
			String dirName = dirMatcher.group("name");

			if(dirName != null) {
				/* Append everything up to this directive. */
				dirMatcher.appendReplacement(condBody, "");

				if(dirName.equals("}")) {
					/* End the iteration. */
					break;
				}

				/* Not a special directive. */
				condBody.append(dirMatcher.group());
			}
		}

		String frmt = condBody.toString();
		Object iter = item;

		if(frmt.equals("")) {
			/* Grab an argument. */
			if(!(item instanceof String)) {
				throw new IllegalFormatConversionException('{', String.class);
			}

			frmt = (String) item;

			if(!tParams.right()) {
				throw new IllegalArgumentException("Not enough parameters to '{' directive");
			}

			iter = tParams.item();
		}

		int maxItr = Integer.MAX_VALUE;

		if(arrParams.length() > 0) {
			maxItr = arrParams.getInt(0, "maximum iterations", '{');
		}

		if(mods.atMod && mods.colonMod) {

		} else if(mods.atMod) {

		} else if(mods.colonMod) {
			if(!(item instanceof Iterable<?>)) {
				throw new IllegalFormatConversionException('{', item.getClass());
			}
		} else {
			if(!(item instanceof Iterable<?>)) {
				throw new IllegalFormatConversionException('{', item.getClass());
			}
		}

		tParams.right();
	}

}
