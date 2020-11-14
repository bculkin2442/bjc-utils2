package bjc.utils.ioutils.format.directives;

import bjc.utils.ioutils.format.*;

/**
 * Implement the I directive.
 * 
 * @author student
 *
 */
public class IndentDirective implements Directive {
	@Override
	public Edict compile(CompileContext compCTX) {
		CLParameters params = compCTX.decr.parameters;
		CLModifiers mods = compCTX.decr.modifiers;

		if (mods.dollarMod) return new IndentConfigureEdict();

		CLValue indentCount = CLValue.nil();
		if (params.length() >= 1) {
			params.mapIndices("count");

			indentCount = params.resolveKey("count");
		}

		return new IndentEdict(indentCount, mods.colonMod);
	}
}

class IndentEdict implements Edict {
	private CLValue numIndentsVal;

	private boolean isRelative;

	public IndentEdict(CLValue numIndents, boolean isRelative) {
		this.numIndentsVal = numIndents;

		this.isRelative = isRelative;
	}

	@Override
	public void format(FormatContext formCTX) {
		int numIndents = numIndentsVal.asInt(formCTX.items, "indent count", "I", 1);

		boolean dedent = false;
		if (numIndents < 0) {
			numIndents = -numIndents;

			dedent = true;
		}

		if (isRelative) {
			if (dedent) formCTX.writer.dedent(numIndents);
			else        formCTX.writer.indent(numIndents);
		} else {
			if (dedent) {
				throw new IllegalArgumentException("Cannot have negative indent level");
			}

			formCTX.writer.setLevel(numIndents);
		}
	}
}

class IndentConfigureEdict implements Edict {
	@Override
	public void format(FormatContext formCTX) {
		// @TODO implement me - Ben Culkin, 1/5/20
	}
}
