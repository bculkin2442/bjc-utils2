package bjc.utils.ioutils.format;

import bjc.utils.esodata.Tape;

import java.util.regex.Matcher;

class FreshlineDirective implements Directive {

	@Override
	public void format(StringBuffer buff, Object item, CLModifiers mods, CLParameters params, Tape<Object> tParams,
			Matcher dirMatcher, CLFormatter fmt) {
		int nTimes = 1;

		if(params.length() > 1) {
			nTimes = params.getInt(0, "occurance count", '&');
		}

		if(buff.charAt(buff.length() - 1) == '\n') nTimes -= 1;

		for(int i = 0; i < nTimes; i++) {
			buff.append("\n");
		}
	}

}
