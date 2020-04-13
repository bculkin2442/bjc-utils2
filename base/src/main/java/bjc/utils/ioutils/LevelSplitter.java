package bjc.utils.ioutils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Splits a string on a delimiter, respecting grouping delimiters.
 *
 * By default, grouping delimiters are (), [], {}, and <>, as well as single and
 * double quoted strings.
 *
 * @author bjculkin
 *
 */
public class LevelSplitter {
	/**
	 * Defaultly configured level splitter.
	 */
	public final static LevelSplitter def = new LevelSplitter();

	/**
	 * Check if a string contains any one of a specified number of things,
	 * respecting groups.
	 *
	 * @param haystack
	 *                 The string to look in.
	 * @param needles
	 *                 The strings to look for.
	 * @return Whether or not any of the strings were contained outside of groups.
	 */
	public boolean levelContains(String haystack, String... needles) {
		int nestLevel = 0;
		int i = 0;

		boolean prevCharWasSlash = false;
		boolean inString = false;

		char stringEnder = ' ';

		while (i < haystack.length()) {
			if (inString == false && nestLevel == 0) {
				for (String needle : needles) {
					if (haystack.regionMatches(i, needle, 0, needle.length())) {
						return true;
					}
				}
			}

			if (inString) {
				if (prevCharWasSlash == true) {
					prevCharWasSlash = false;
				} else if (haystack.charAt(i) == stringEnder) {
					inString = false;
				}
			} else {
				switch (haystack.charAt(i)) {
				case '\'':
					inString = true;
					stringEnder = '\'';
					break;
				case '\"':
					inString = true;
					stringEnder = '\"';
					break;
				case '(':
				case '[':
				case '{':
				case '<':
					nestLevel += 1;
					break;
				case ')':
				case ']':
				case '}':
				case '>':
					nestLevel = Math.max(0, nestLevel - 1);
					break;
				}
			}

			i += 1;
		}

		return false;
	}

	/**
	 * Split a string, respecting groups.
	 *
	 * @param phrase
	 *               The string to split.
	 * @param splits
	 *               The strings to split on.
	 * @return A list of split strings. If keepDelims is true, it also includes the
	 *         delimiters in between the split strings.
	 */
	public List<String> levelSplit(String phrase, String... splits) {
		return levelSplit(phrase, false, splits);
	}

	/**
	 * Split a string, respecting groups.
	 *
	 * @param phrase
	 *                   The string to split.
	 * @param keepDelims
	 *                   Whether or not to include the delimiters in the results.
	 * @param splits
	 *                   The strings to split on.
	 * @return A list of split strings. If keepDelims is true, it also includes the
	 *         delimiters in between the split strings.
	 */
	public List<String> levelSplit(String phrase, boolean keepDelims, String... splits) {
		String work = phrase;

		List<String> strangs = new ArrayList<>();

		int nestLevel = 0;
		int i = 0;

		boolean prevCharWasSlash = false;
		boolean inString = false;

		char stringEnder = ' ';

		// Shortcut empty strings
		if (phrase.equals("")) {
			strangs.add("");

			return strangs;
		}

		while (i < work.length()) {
			if (inString == false && nestLevel == 0) {
				for (String split : splits) {
					if (work.regionMatches(i, split, 0, split.length())) {
						strangs.add(work.substring(0, i));

						if (keepDelims)
							strangs.add(split);

						work = work.substring(i + split.length());
						i = 0;

						continue;
					}
				}
			}

			if (inString) {
				if (prevCharWasSlash == true) {
					prevCharWasSlash = false;
				} else if (work.charAt(i) == stringEnder) {
					inString = false;
				}
			} else {
				/*
				 * @TODO Ben Culkin 9/4/18
				 *
				 * This currently crashes if the string ends with one of the delimiters in
				 * question.
				 */
				switch (work.charAt(i)) {
				case '\'':
					inString = true;
					stringEnder = '\'';
					break;
				case '\"':
					inString = true;
					stringEnder = '\"';
					break;
				case '(':
				case '[':
				case '{':
				case '<':
					nestLevel += 1;
					break;
				case ')':
				case ']':
				case '}':
				case '>':
					nestLevel = Math.max(0, nestLevel - 1);
					break;
				}
			}

			i += 1;
		}

		strangs.add(work);

		return strangs;
	}

	// @TODO @FIXME
	//
	// This doesn't seem like its working
	@SuppressWarnings("javadoc")
	public List<String> levelSplitRX(String phrase, String patt) {
		return levelSplitRX(phrase, false, patt);
	}

	@SuppressWarnings("javadoc")
	public List<String> levelSplitRX(String phrase, boolean keepDelims, String patt) {
		Pattern pat = Pattern.compile(patt);

		Matcher mat = pat.matcher(phrase);

		List<String> strangs = new ArrayList<>();

		int nestLevel = 0;
		int i = 0;
		int lastMatch = 0;

		boolean prevCharWasSlash = false;
		boolean inString = false;

		char stringEnder = ' ';

		while ((lastMatch + i) < phrase.length()) {
			int ai = lastMatch + i;

			mat.region(lastMatch + i, phrase.length());

			if (inString == false && nestLevel == 0) {
				if (mat.lookingAt()) {

					strangs.add(phrase.substring(lastMatch, mat.start()));
					if (keepDelims)
						strangs.add(mat.group());
					lastMatch = mat.end();
					// work = work.substring(mat.end());
					// i = 0;

					// mat = pat.matcher(work);
					continue;
				}
			}

			if (inString) {
				if (prevCharWasSlash == true) {
					prevCharWasSlash = false;
				} else if (phrase.charAt(ai) == stringEnder) {
					inString = false;
				}
			} else {
				switch (phrase.charAt(ai)) {
				case '\'':
					inString = true;
					stringEnder = '\'';
					break;
				case '\"':
					inString = true;
					stringEnder = '\"';
					break;
				case '(':
				case '[':
				case '{':
				case '<':
					nestLevel += 1;
					break;
				case ')':
				case ']':
				case '}':
				case '>':
					nestLevel = Math.max(0, nestLevel - 1);
					break;
				}
			}

			i += 1;
		}

		strangs.add(phrase.substring(lastMatch));

		return strangs;
	}
}
