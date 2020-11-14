package bjc.utils.ioutils.format.directives;

import java.io.*;
import java.util.*;

import bjc.utils.ioutils.ReportWriter;
import bjc.utils.ioutils.format.*;

/**
 * Implements the C directive.
 *
 * This serves to print out a single character, in the way that the '%c' printf
 * directive does.
 *
 * @author Ben Culkin
 */
public class CharacterDirective implements Directive {
	@Override
	public Edict compile(CompileContext compCTX) {
		return new CharacterEdict(compCTX.decr.modifiers.colonMod);
	}
}

class CharacterEdict implements Edict {
	private boolean printCharName;

	public CharacterEdict(boolean printCharName) {
		this.printCharName = printCharName;
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		Object item = formCTX.items.item();

		CLFormatter.checkItem(item, 'C');

		if (!(item instanceof Character)) {
			throw new IllegalFormatConversionException('C', item.getClass());
		}

		char ch = (Character) item;
		int codepoint = ch;

		ReportWriter rw = formCTX.writer;

		if (printCharName) rw.write(Character.getName(codepoint));
		else               rw.write(ch);

		formCTX.items.right();
	}
}
