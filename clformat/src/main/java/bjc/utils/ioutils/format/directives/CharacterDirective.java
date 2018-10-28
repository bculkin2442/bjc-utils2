package bjc.utils.ioutils.format.directives;

import java.io.IOException;
import java.util.IllegalFormatConversionException;

import bjc.utils.ioutils.format.CLFormatter;

/**
 * Implements the C directive.
 * 
 * @author student
 *
 */
public class CharacterDirective implements Directive {

	@Override
	public void format(FormatParameters dirParams) throws IOException {
		CLFormatter.checkItem(dirParams.item, 'C');

		if (!(dirParams.item instanceof Character)) {
			throw new IllegalFormatConversionException('C', dirParams.item.getClass());
		}

		char ch = (Character) dirParams.item;
		int codepoint = ch;

		if (dirParams.mods.colonMod) {
			/*
			 * Colon mod means print Unicode character name.
			 */
			dirParams.rw.write(Character.getName(codepoint));
		} else {
			dirParams.rw.write(ch);
		}

		dirParams.tParams.right();
	}

}
