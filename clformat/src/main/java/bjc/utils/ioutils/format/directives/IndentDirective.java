package bjc.utils.ioutils.format.directives;

import java.io.*;

import bjc.utils.esodata.*;
import bjc.utils.ioutils.format.*;

/**
 * Implement the I directive.
 * @author student
 *
 */
public class IndentDirective implements Directive {
	@Override
	public void format(FormatParameters dirParams) throws IOException {
		Edict edt = compile(dirParams.toCompileCTX());

		edt.format(dirParams.toFormatCTX());
	}

	public void formatF(FormatParameters dirParams) throws IOException {
		Tape<Object> itemTape = dirParams.tParams;

		CLModifiers mods = dirParams.getMods();
		CLParameters params = dirParams.getParams();

		// Dollar mod is indent configuration
		if (mods.dollarMod) {
			return;
		}

		int nIndents = 1;

		if(params.length() >= 1) {
			params.mapIndices("count");

			nIndents = params.getInt(itemTape, "count", "indent count", "I", 1);
		}

		boolean dedent = false;
		if (nIndents < 0) {
			nIndents = -nIndents;

			dedent = true;
		}

		if (mods.colonMod) {
			if (dedent) dirParams.rw.dedent(nIndents);
			else        dirParams.rw.indent(nIndents);
		} else {
			if (dedent) throw new IllegalArgumentException("Cannot have negative indent level");

			dirParams.rw.setLevel(nIndents);
		}
	}

	@Override
	public Edict compile(CompileContext compCTX) {
		CLParameters params = compCTX.decr.parameters;
		CLModifiers mods = compCTX.decr.modifiers;

		if (mods.dollarMod) {
			return new IndentConfigureEdict();
		}

		CLValue indentCount = CLValue.nil();
		if(params.length() >= 1) {
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
			if (dedent) {
				formCTX.writer.dedent(numIndents);
			} else {
				formCTX.writer.indent(numIndents);
			}
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

	}
}
