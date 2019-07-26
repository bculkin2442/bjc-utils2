package bjc.utils.ioutils.format.directives;

import java.io.*;

import bjc.utils.ioutils.format.*;

/**
 * Implement the &amp; directive.
 * @author student
 *
 */
public class FreshlineDirective implements Directive {

	@Override
	public void format(FormatParameters dirParams) throws IOException {
		int nTimes = 1;

		CLParameters params = dirParams.getParams();

		if(params.length() >= 1) {
			params.mapIndices("count");
			nTimes = params.getInt(dirParams.tParams, "count", "occurance count", "&", 1);
		}

		if(dirParams.rw.isLastCharNL()) nTimes -= 1;

		for(int i = 0; i < nTimes; i++) {
			dirParams.rw.write("\n");
		}
	}
}
