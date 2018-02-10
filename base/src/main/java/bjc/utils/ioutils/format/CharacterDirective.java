package bjc.utils.ioutils.format;

import bjc.utils.esodata.Tape;

import java.util.IllegalFormatConversionException;
import java.util.regex.Matcher;

class CharacterDirective implements Directive {

	@Override
	public void format(StringBuffer buff, Object parm, CLModifiers mods, CLParameters arrParams,
			Tape<Object> tParams, Matcher dirMatcher, CLFormatter fmt) {
		CLFormatter.checkItem(parm, 'C');
		
		if(!(parm instanceof Character)) {
			throw new IllegalFormatConversionException('C', parm.getClass());
		}

		char ch = (Character) parm;
		int codepoint = ch;

		if(mods.colonMod) {
			/*
			 * Colon mod means print Unicode character name.
			 */
			buff.append(Character.getName(codepoint));
		} else {
			buff.append(ch);
		}
		
		tParams.right();
	}

}
