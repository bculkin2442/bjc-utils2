package bjc.utils.ioutils.format.directives;

import bjc.utils.esodata.Tape;
import bjc.utils.ioutils.format.CLFormatter;
import bjc.utils.ioutils.format.CLModifiers;
import bjc.utils.ioutils.format.CLParameters;
import bjc.utils.ioutils.ReportWriter;

import java.io.IOException;
import java.util.IllegalFormatConversionException;
import java.util.regex.Matcher;

/**
 * Implements the C directive.
 * 
 * @author student
 *
 */
public class CharacterDirective implements Directive {

	@Override
	public void format(ReportWriter rw, Object parm, CLModifiers mods, CLParameters arrParams, Tape<Object> tParams,
			Matcher dirMatcher, CLFormatter fmt) throws IOException {
		CLFormatter.checkItem(parm, 'C');

		if (!(parm instanceof Character)) {
			throw new IllegalFormatConversionException('C', parm.getClass());
		}

		char ch = (Character) parm;
		int codepoint = ch;

		if (mods.colonMod) {
			/*
			 * Colon mod means print Unicode character name.
			 */
			rw.write(Character.getName(codepoint));
		} else {
			rw.write(ch);
		}

		tParams.right();
	}

}
