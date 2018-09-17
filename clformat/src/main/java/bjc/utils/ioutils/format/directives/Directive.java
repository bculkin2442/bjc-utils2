package bjc.utils.ioutils.format.directives;

import java.io.IOException;
import java.util.regex.Matcher;

import bjc.utils.esodata.Tape;
import bjc.utils.ioutils.format.CLFormatter;
import bjc.utils.ioutils.format.CLModifiers;
import bjc.utils.ioutils.format.CLParameters;
import bjc.utils.ioutils.ReportWriter;

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
	 * 
	 * @param sb
	 *        The buffer the string is being output to.
	 * @param item
	 *        The current parameter being passed
	 * @param mods
	 *        The directive modifiers
	 * @param arrParams
	 *        The prefix parameters to the directive
	 * @param tParams
	 *        All of the provided format parameters
	 * @param dirMatcher
	 *        The matcher for format directives
	 * @param fmt
	 *        The formatter itself.
	 */
	public void format(ReportWriter rw, Object item, CLModifiers mods, CLParameters arrParams, Tape<Object> tParams,
			Matcher dirMatcher, CLFormatter fmt) throws IOException;

	public static boolean isOpening(String str) {
		return isOpening(str.charAt(0));
	}

	public static boolean isOpening(char dir) {
		switch(dir) {
		case '(':
		case '<':
		case '[':
		case '{':
			return true;
		default:
			return false;
		}
	}

	public static boolean isClosing(String str) {
		return isClosing(str.charAt(0));
	}

	public static boolean isClosing(char dir) {
		switch(dir) {
		case ')':
		case '>':
		case ']':
		case '}':
			return true;
		default:
			return false;
		}
	}
}
