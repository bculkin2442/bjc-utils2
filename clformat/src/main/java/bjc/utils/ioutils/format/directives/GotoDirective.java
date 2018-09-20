package bjc.utils.ioutils.format.directives;

import bjc.utils.ioutils.format.CLParameters;

/**
 * Implement the * directive.
 * 
 * @author student
 *
 */
public class GotoDirective implements Directive {

	@Override
	public void format(FormatParameters dirParams) {
		CLParameters params = dirParams.arrParams;
		if (dirParams.mods.colonMod) {
			int num = 1;
			if (params.length() >= 1) {
				params.mapIndices("numargs");

				num = params.getInt("numargs", "number of arguments backward", "*", 1);
			}

			dirParams.tParams.left(num);
		} else if (dirParams.mods.atMod) {
			int num = 0;
			if (params.length() >= 1) {
				params.mapIndices("argidx");

				num = params.getInt("argidx", "argument index", "*", 0);
			}

			dirParams.tParams.first();
			dirParams.tParams.right(num);
		} else {
			int num = 1;
			if (params.length() >= 1) {
				params.mapIndices("numargs");

				num = params.getInt("numargs", "number of arguments forward", "*", 1);
			}

			dirParams.tParams.right(num);
		}
	}

}
