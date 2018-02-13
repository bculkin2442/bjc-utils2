package bjc.utils.ioutils.format;

import bjc.utils.esodata.Tape;

import java.util.regex.Matcher;

class EscapeDirective implements Directive {

	@Override
	public void format(StringBuffer sb, Object item, CLModifiers mods, CLParameters params,
			Tape<Object> formatParams, Matcher dirMatcher, CLFormatter fmt) {
		boolean shouldExit;

		switch(params.length()) {
		case 0:
			shouldExit = formatParams.size() == 0;
			break;
		case 1:
			int num = params.getInt(0, "condition count", '^');
			shouldExit = num == 0;
			break;
		case 2:
			int left = params.getInt(0, "left-hand condition", '^');
			int right = params.getInt(1, "right-hand condition", '^');
			shouldExit = left == right;
			break;
		case 3:
		default:
			int low = params.getInt(0, "lower-bound condition", '^');
			int mid = params.getInt(1, "interval condition", '^');
			int high = params.getInt(2, "upper-bound condition", '^');
			shouldExit = (low <= mid) && (mid <= high);
			break;
		}

		/* At negates it. */
		if(mods.atMod) shouldExit = !shouldExit;

		if(shouldExit) throw new EscapeException(mods.colonMod);
	}

}
