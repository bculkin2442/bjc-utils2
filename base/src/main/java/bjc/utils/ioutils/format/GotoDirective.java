package bjc.utils.ioutils.format;

import bjc.utils.esodata.Tape;

import java.util.regex.Matcher;

class GotoDirective implements Directive {

	@Override
	public void format(StringBuffer sb, Object item, CLModifiers mods, CLParameters params, Tape<Object> formatParams,
			Matcher dirMatcher, CLFormatter fmt) {
		if(mods.colonMod) {
			int num = 1;
			if(params.length() > 1) {
				num = params.getIntDefault(0, "number of arguments backward", '*', 1);
			}

			formatParams.left(num);
		} else if(mods.atMod) {
			int num = 0;
			if(params.length() > 1) {
				num = params.getIntDefault(0, "argument index", '*', 0);
			}

			formatParams.first();
			formatParams.right(num);
		} else {
			int num = 1;
			if(params.length() > 1) {
				num = params.getIntDefault(0, "number of arguments forward", '*', 1);
			}

			formatParams.right(num);
		}
	}

}