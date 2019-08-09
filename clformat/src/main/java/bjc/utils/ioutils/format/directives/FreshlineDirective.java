package bjc.utils.ioutils.format.directives;

import java.io.*;

import bjc.utils.ioutils.format.*;

/**
 * Implement the &amp; directive.
 *
 * This directive will print out a specified number of newlines, and will print
 * one less if the last thing printed was a newline.
 *
 * @author Ben Culkin
 */
public class FreshlineDirective implements Directive {
	@Override
	public void format(FormatParameters dirParams) throws IOException {
		Edict edt = compile(dirParams.toCompileCTX());

		edt.format(dirParams.toFormatCTX());
	}

	@Override
	public Edict compile(CompileContext compCTX) {
		CLParameters params = compCTX.decr.parameters;

		CLValue times = CLValue.nil();
		if (params.length() >= 1) {
			params.mapIndices("count");
			times = params.resolveKey("count");
		}

		return new FreshlineEdict(times);
	}
}

class FreshlineEdict implements Edict {
	private CLValue times;

	public FreshlineEdict(CLValue times) {
		this.times = times;
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		int nTimes = times.asInt(formCTX.items, "occurance count", "&", 1);

		if (formCTX.writer.isLastCharNL()) nTimes -= 1;

		for(int i = 0; i < nTimes; i++) {
			formCTX.writer.write("\n");
		}
	}
}
