package bjc.utils.ioutils.format;

import bjc.utils.esodata.Tape;

import java.util.regex.Matcher;

class LiteralDirective implements Directive {

	private char directive;
	private String lit;

	public LiteralDirective(String lit, char directive) {
		this.directive = directive;
		this.lit = lit;
	}


	@Override
	public void format(StringBuffer buff, Object item, CLModifiers mods, CLParameters params, Tape<Object> tParams,
			Matcher dirMatcher, CLFormatter fmt) {
		int nTimes = 1;

		if(params.length() > 1) {
			nTimes = params.getInt(0, "occurance count", directive);
		}

		for(int i = 0; i < nTimes; i++) {
			buff.append(lit);
		}

	}

}
