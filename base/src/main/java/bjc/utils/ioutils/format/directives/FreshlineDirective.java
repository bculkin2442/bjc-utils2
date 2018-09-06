package bjc.utils.ioutils.format.directives;

import bjc.utils.esodata.Tape;
import bjc.utils.ioutils.format.CLFormatter;
import bjc.utils.ioutils.format.CLModifiers;
import bjc.utils.ioutils.format.CLParameters;
import bjc.utils.ioutils.ReportWriter;

import java.io.IOException;
import java.util.regex.Matcher;

/**
 * Implement the &amp; directive.
 * @author student
 *
 */
public class FreshlineDirective implements Directive {

	@Override
	public void format(ReportWriter rw, Object item, CLModifiers mods, CLParameters params, Tape<Object> tParams,
			Matcher dirMatcher, CLFormatter fmt) throws IOException {
		int nTimes = 1;

		if(params.length() >= 1) {
			nTimes = params.getInt(0, "occurance count", '&');
		}

		if(rw.isLastCharNL()) nTimes -= 1;

		for(int i = 0; i < nTimes; i++) {
			rw.write("\n");
		}
	}
}
