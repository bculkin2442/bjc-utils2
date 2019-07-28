package bjc.utils.ioutils.format.directives;

import java.io.*;

import bjc.utils.esodata.*;
import bjc.utils.ioutils.format.*;

/**
 * Implementation for the ^ directive.
 *
 * This directive allows you to escape an iteration directive.
 *
 * @author Ben Culkin
 */
public class EscapeDirective implements Directive {
	public void format(FormatParameters dirParams) throws IOException {
		Edict edt = compile(dirParams.toCompileCTX());

		edt.format(dirParams.toFormatCTX());
	}

	public void formatF(FormatParameters dirParams) {
		Tape<Object> itemTape = dirParams.tParams;
		CLModifiers mods = dirParams.getMods();

		boolean shouldExit;

		if (mods.dollarMod) dirParams.tParams.right();

		CLParameters params = dirParams.getParams();
		switch(params.length()) {
		case 0:
			shouldExit = dirParams.tParams.atEnd();
			break;
		case 1: {
			params.mapIndices("count");
			int num = params.getInt(itemTape, "count", "condition count", "^", 0);

			shouldExit = num == 0;
			break;
		}
		case 2: {
			params.mapIndices("lhand", "rhand");
			int left  = params.getInt(itemTape, "lhand", "left-hand condition", "^", 0);
			int right = params.getInt(itemTape, "rhand", "right-hand condition", "^", 0);

			shouldExit = left == right;
			break;
		}
		case 3:
		default: {
			params.mapIndices("lower", "ival", "upper");

			int low  = params.getInt(itemTape, "lower", "lower-bound condition", "^", 0);
			int mid  = params.getInt(itemTape, "ival", "interval condition", "^", 0);
			int high = params.getInt(itemTape, "upper", "upper-bound condition", "^", 0);

			shouldExit = (low <= mid) && (mid <= high);
			break;
		}
		}

		if (mods.dollarMod) dirParams.tParams.left();

		/* At negates it. */
		if(mods.atMod) shouldExit = !shouldExit;

		if(shouldExit) throw new EscapeException(mods.colonMod);
	}

	@Override
	public Edict compile(CompileContext compCTX) {
		CLParameters params = compCTX.decr.parameters;
		CLModifiers mods = compCTX.decr.modifiers;

		CLValue param1 = CLValue.nil();
		CLValue param2 = CLValue.nil();
		CLValue param3 = CLValue.nil();

		EscapeEdict.Mode mode;
		switch (params.length()) {
			case 0:
				mode = EscapeEdict.Mode.END;
				break;
			case 1:
				mode = EscapeEdict.Mode.COUNT;
				params.mapIndices("count");
				param1 = params.resolveKey("count");
				break;
			case 2:
				params.mapIndices("lhand", "rhand");
				param1 = params.resolveKey("lhand");
				param2 = params.resolveKey("rhand");
				mode = EscapeEdict.Mode.EQUALITY;
				break;
			case 3:
				params.mapIndices("lower", "ival", "upper");
				param1 = params.resolveKey("lower");
				param2 = params.resolveKey("ival");
				param3 = params.resolveKey("upper");
				mode = EscapeEdict.Mode.RANGE;
				break;
			default:
				throw new IllegalArgumentException("Too many parameters to ^ directive");
		}

		return new EscapeEdict(mods.atMod, mode, mods.colonMod, param1, param2,
				param3, mods.dollarMod);
	}
}

class EscapeEdict implements Edict {
	public static enum Mode {
		END,
		COUNT,
		EQUALITY,
		RANGE
	}

	private Mode mode;

	private boolean isNegated;
	private boolean terminateIteration;

	private CLValue param1;
	private CLValue param2;
	private CLValue param3;

	private boolean advance;

	public EscapeEdict(boolean isNegated, Mode mode, boolean terminateIteration,
			CLValue param1, CLValue param2, CLValue param3, boolean advance) {
		this.mode = mode;

		this.isNegated = isNegated;
		this.terminateIteration = terminateIteration;

		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;

		this.advance = advance;
	}

	@Override
	public void format(FormatContext formCTX) {
		boolean shouldExit;

		Tape<Object> items = formCTX.items;

		if (advance) items.right();

		switch (mode) {
		case END:
			shouldExit = items.atEnd();
			break;
		case COUNT:
			{
				int num = param1.asInt(items, "condition count", "^", 0);

				shouldExit = (num == 0);
			}
			break;
		case EQUALITY:
			{
				int left  = param1.asInt(items, "left-hand condition", "^", 0);
				int right = param2.asInt(items, "right-hand condition", "^", 0);

				shouldExit = (left == right);
			}
			break;
		case RANGE:
			{
				int low  = param1.asInt(items, "lower-bound condition", "^", 0);
				int mid  = param2.asInt(items, "interval condition", "^", 0);
				int high = param3.asInt(items, "upper-bound condition", "^", 0);

				shouldExit = (low <= mid) && (mid <= high);
			}
			break;
		default:
			throw new IllegalArgumentException("Escape condition mode " + mode + " isn't supported");
		}

		if (advance) items.left();

		if (isNegated) {
			shouldExit = !shouldExit;
		}

		if (shouldExit) {
			throw new EscapeException(terminateIteration);
		}
	}
}
