package bjc.utils.ioutils.format;

import java.util.regex.Matcher;

import bjc.utils.esodata.Tape;

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
	public void format(StringBuffer sb, Object item, CLModifiers mods, CLParameters arrParams, Tape<Object> tParams,
			Matcher dirMatcher, CLFormatter fmt);
}