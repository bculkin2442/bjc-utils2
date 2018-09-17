package bjc.utils.ioutils.format.directives;

import java.io.IOException;

/**
 * A CL format directive.
 * 
 * @author EVE
 *
 */
@FunctionalInterface
public interface Directive {
	/**
	 * Execute this format directive.
	 * @param dirParams TODO
	 * @param sb
	 *        The buffer the string is being output to.
	 */
	public void format(FormatParameters dirParams) throws IOException;

	public static boolean isOpening(String str) {
		switch(str) {
		case "(":
		case "<":
		case "[":
		case "{":
		case "`(":
			return true;
		default:
			return false;
		}
	}

	public static boolean isClosing(String str) {
		switch(str) {
		case ")":
		case ">":
		case "]":
		case "}":
		case "`)":
			return true;
		default:
			return false;
		}
	}
}
