package bjc.utils.ioutils.format.directives;

import bjc.utils.esodata.*;
import bjc.utils.ioutils.format.*;

/**
 * Implementation for the ^ directive.
 * @author student
 *
 */
public class EscapeDirective implements Directive {

	@Override
	public void format(FormatParameters dirParams) {
		Tape<Object> itemTape = dirParams.tParams;

		boolean shouldExit;

		if (dirParams.mods.dollarMod) dirParams.tParams.right();

		CLParameters params = dirParams.arrParams;
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

		if (dirParams.mods.dollarMod) dirParams.tParams.left();

		/* At negates it. */
		if(dirParams.mods.atMod) shouldExit = !shouldExit;

		if(shouldExit) throw new EscapeException(dirParams.mods.colonMod);
	}

}
