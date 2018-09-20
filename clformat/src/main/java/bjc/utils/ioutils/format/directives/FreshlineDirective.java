package bjc.utils.ioutils.format.directives;

import java.io.IOException;

/**
 * Implement the &amp; directive.
 * @author student
 *
 */
public class FreshlineDirective implements Directive {

	@Override
	public void format(FormatParameters dirParams) throws IOException {
		int nTimes = 1;

		if(dirParams.arrParams.length() >= 1) {
			dirParams.arrParams.mapIndices("count");
			nTimes = dirParams.arrParams.getInt("count", "occurance count", "&", 1);
		}

		if(dirParams.rw.isLastCharNL()) nTimes -= 1;

		for(int i = 0; i < nTimes; i++) {
			dirParams.rw.write("\n");
		}
	}
}
