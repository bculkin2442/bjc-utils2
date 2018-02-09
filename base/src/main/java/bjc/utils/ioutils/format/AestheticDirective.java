package bjc.utils.ioutils.format;

import java.util.regex.Matcher;

import bjc.utils.esodata.Tape;

public class AestheticDirective implements Directive {

	@Override
	public void format(StringBuffer sb, Object item, CLModifiers mods, CLParameters params, Tape<Object> tParams,
			Matcher dirMatcher) {
		CLFormatter.checkItem(item, 'A');
		
		int mincol = 0, colinc = 1, minpad = 0;
		char padchar = ' ';

		if (params.length() > 1) {
			mincol = params.getIntDefault(0, "minimum column count", 'A', 0);
		}

		if (params.length() < 4) {
			throw new IllegalArgumentException(
					"Must provide either zero, one or four arguments to A directive");
		}

		colinc = params.getIntDefault(1, "padding increment", 'A', 1);
		minpad = params.getIntDefault(2, "minimum amount of padding", 'A', 0);
		padchar = params.getCharDefault(3, "padding character", 'A', ' ');

		StringBuilder work = new StringBuilder();

		if (mods.atMod) {
			for (int i = 0; i < minpad; i++) {
				work.append(padchar);
			}

			for (int i = work.length(); i < mincol; i++) {
				for (int k = 0; k < colinc; k++) {
					work.append(padchar);
				}
			}
		}

		work.append(item.toString());

		if (!mods.atMod) {
			for (int i = 0; i < minpad; i++) {
				work.append(padchar);
			}

			for (int i = work.length(); i < mincol; i++) {
				for (int k = 0; k < colinc; k++) {
					work.append(padchar);
				}
			}
		}
		
		tParams.right();
	}

}
