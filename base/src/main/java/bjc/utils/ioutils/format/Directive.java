package bjc.utils.ioutils.format;

import java.util.regex.Matcher;

import bjc.utils.esodata.Tape;

@FunctionalInterface
public interface Directive {
	/*
	 * @TODO fill in parameters
	 */
	public void format(StringBuffer sb, Object item, CLModifiers mods,
			CLParameters arrParams, Tape<Object> tParams, Matcher dirMatcher, CLFormatter fmt);
}