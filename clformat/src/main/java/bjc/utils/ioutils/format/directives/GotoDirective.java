package bjc.utils.ioutils.format.directives;

/**
 * Implement the * directive.
 * 
 * @author student
 *
 */
public class GotoDirective implements Directive {

	@Override
	public void format(FormatParameters dirParams) {
		if (dirParams.mods.colonMod) {
			int num = 1;
			if (dirParams.arrParams.length() >= 1) {
				num = dirParams.arrParams.getIntDefault(0, "number of arguments backward", '*', 1);
			}

			dirParams.tParams.left(num);
		} else if (dirParams.mods.atMod) {
			int num = 0;
			if (dirParams.arrParams.length() >= 1) {
				num = dirParams.arrParams.getIntDefault(0, "argument index", '*', 0);
			}

			dirParams.tParams.first();
			dirParams.tParams.right(num);
		} else {
			int num = 1;
			if (dirParams.arrParams.length() >= 1) {
				num = dirParams.arrParams.getIntDefault(0, "number of arguments forward", '*', 1);
			}

			dirParams.tParams.right(num);
		}
	}

}
