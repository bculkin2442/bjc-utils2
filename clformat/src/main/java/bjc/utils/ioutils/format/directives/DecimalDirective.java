package bjc.utils.ioutils.format.directives;

import java.io.*;
import java.text.*;

import bjc.esodata.*;
import bjc.utils.ioutils.format.*;

/**
 * Implementation of the `D directive.
 *
 * This is the most general directive for printing out decimal-numbers (floating
 * point).
 *
 * @author Ben Culkin
 */
public class DecimalDirective implements Directive {
	@Override
	public Edict compile(CompileContext compCTX) {
		CLParameters params = compCTX.decr.parameters;
		CLModifiers mods = compCTX.decr.modifiers;

		CLValue decForm = CLValue.nil();

		switch(params.length()) {
		case 0:
			// Use the default
			break;
		case 1:
			// Use the specified format
			params.mapIndices("format");

			decForm = params.resolveKey("format");
			break;
		default:
			// @TODO 16 Oct, 2020 - Ben Culkin - :Preformat
			// Add ability to specify a common/fixed set of formats
			//
			// @TODO 16 Oct, 2020 - Ben Culkin - :ErrorFix
			// Instead of using IllegalArgumentException here, use a custom
			// subtype of it with an appropriate name/auto-message forming
			throw new IllegalArgumentException("Must provide 0 or 1 arguments to `D directive");
		}

		return new DecimalEdict(decForm);
	}
}

class DecimalEdict implements Edict {
	private static final FieldPosition ZERO_FIELD = new FieldPosition(0);

	private CLValue decFormat;

	public DecimalEdict(CLValue decForm) {
		this.decFormat = decForm;
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		Tape<Object> itemTape = formCTX.items;

		CLFormatter.checkItem(itemTape.item(), "`D");

		NumberFormat numForm = NumberFormat.getInstance();

		String decFormString = decFormat.getValue(itemTape);

		if (decFormString == null || decFormString.equals("")) {
			// Use the default if not provided.
		} else {
			if (numForm instanceof DecimalFormat) {
				((DecimalFormat)numForm).applyPattern(decFormString);
			} else {
				String clsName = numForm.getClass().getName();

				String msg = String.format("INTERNAL ERROR: Unknown NumberFormat type %s, expected DecimalFormat or compatible", clsName);

				throw new UnsupportedOperationException(msg);
			}
		}

		StringBuffer work = new StringBuffer();

		numForm.format(itemTape.item(), work, ZERO_FIELD);

		formCTX.writer.write(work.toString());
		itemTape.right();
	}
}
