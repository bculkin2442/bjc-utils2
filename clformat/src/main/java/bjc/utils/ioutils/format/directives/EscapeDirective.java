package bjc.utils.ioutils.format.directives;

import bjc.utils.ioutils.format.EscapeException;

/**
 * Implementation for the ^ directive.
 * @author student
 *
 */
public class EscapeDirective implements Directive {

	@Override
	public void format(FormatParameters dirParams) {
		boolean shouldExit;

		if (dirParams.mods.dollarMod) dirParams.tParams.right();

		switch(dirParams.arrParams.length()) {
		case 0:
			shouldExit = dirParams.tParams.atEnd();
			break;
		case 1:
			int num = dirParams.arrParams.getInt(0, "condition count", "^");

			shouldExit = num == 0;
			break;
		case 2:
			int left  = dirParams.arrParams.getInt(0, "left-hand condition", "^");
			int right = dirParams.arrParams.getInt(1, "right-hand condition", "^");

			shouldExit = left == right;
			break;
		case 3:
		default:
			int low  = dirParams.arrParams.getInt(0, "lower-bound condition", "^");
			int mid  = dirParams.arrParams.getInt(1, "interval condition", "^");
			int high = dirParams.arrParams.getInt(2, "upper-bound condition", "^");

			shouldExit = (low <= mid) && (mid <= high);
			break;
		}

		if (dirParams.mods.dollarMod) dirParams.tParams.left();

		/* At negates it. */
		if(dirParams.mods.atMod) shouldExit = !shouldExit;

		if(shouldExit) throw new EscapeException(dirParams.mods.colonMod);
	}

}
