package bjc.utils.ioutils.format.directives;

import bjc.esodata.*;
import bjc.utils.ioutils.format.*;

/**
 * Implement the * directive.
 *
 * @author student
 *
 */
public class GotoDirective implements Directive {
	@Override
	public Edict compile(CompileContext compCTX) {
		CLParameters params = compCTX.decr.parameters;
		CLModifiers mods = compCTX.decr.modifiers;

		CLValue numVal = CLValue.nil();
		GotoEdict.Mode mode;

		if (mods.colonMod) {
			mode = GotoEdict.Mode.BACKWARD;

			if (params.length() >= 1) {
				params.mapIndices("numargs");
				numVal = params.resolveKey("numargs");
			}
		} else if (mods.atMod) {
			mode = GotoEdict.Mode.INDEX;

			if (params.length() >= 1) {
				params.mapIndices("argidx");
				numVal = params.resolveKey("argidx");
			}
		} else {
			mode = GotoEdict.Mode.FORWARD;

			if (params.length() >= 1) {
				params.mapIndices("numargs");
				numVal = params.resolveKey("numargs");
			}
		}

		return new GotoEdict(mode, numVal);
	}
}

class GotoEdict implements Edict {
	public static enum Mode {
		FORWARD, BACKWARD, INDEX
	}

	private Mode mode;

	private CLValue numVal;

	public GotoEdict(Mode mode, CLValue numVal) {
		this.mode = mode;

		this.numVal = numVal;
	}

	@Override
	public void format(FormatContext formCTX) {
		Tape<Object> items = formCTX.items;

		int num;
		switch (mode) {
		case FORWARD:
			num = numVal.asInt(items, "number of arguments forward", "*", 1);
			items.right(num);
			break;
		case BACKWARD:
			num = numVal.asInt(items, "number of arguments backward", "*", 1);
			items.left(num);
			break;
		case INDEX:
			num = numVal.asInt(items, "argument index", "*", 0);
			items.seekTo(num);
			break;
		default:
			throw new IllegalArgumentException("Unsupported goto mode " + mode);
		}

	}
}
