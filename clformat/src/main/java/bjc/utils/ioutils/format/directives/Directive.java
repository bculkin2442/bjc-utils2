package bjc.utils.ioutils.format.directives;

import java.io.IOException;

/**
 * A CL format directive.
 * 
 * @author Ben Culkin
 */
public interface Directive {
	/**
	 * Execute this format directive.
	 *
	 * @param dirParams
	 * 	The parameters for the directive.
	 * @throws IOException If something goes wrong.
	 */
	public default void format(FormatParameters dirParams) throws IOException {
		Edict edt = compile(dirParams.toCompileCTX());

		edt.format(dirParams.toFormatCTX());
	}

	/**
	 * Compile this directive.
	 *
	 * @param compCTX
	 * 	The state necessary to compile this directive.
	 *
	 * @return A compiled form of this directive.
	 */
	public default Edict compile(CompileContext compCTX) {
		throw new IllegalArgumentException("This directive does not support compilation yet");
	}

	/**
	 * Check if a particular directive is an opening directive.
	 *
	 * @param str
	 * 	The directive to check.
	 *
	 * @return Whether or not the directive is opening.
	 */
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

	/**
	 * Check if a particular directive is an opening directive.
	 *
	 * @param str
	 * 	The directive to check.
	 *
	 * @return
	 * 	Whether or not the directive is opening.
	 */
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

	// @TODO 9/19/18 Ben Culkin :ParseContained
	//
	// Implement something for parsing contained bodies, abstracting the
	// stuff that Iteration/Conditional/CaseDirective do.
	//
	// The main issue is thinking of a good interface to it. 
}
