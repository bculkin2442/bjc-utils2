package bjc.utils.parserutils.splitter;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Simple implementation of {@link TokenSplitter}
 *
 * @author EVE
 */
@Deprecated
public class SimpleTokenSplitter implements TokenSplitter {
	/*
	 * This string is a format template for the delimiter matching regex
	 *
	 * It does two things:
	 *
	 * <ol> <li> Match to the left of the provided delimiter by positive
	 * lookahead </li> <li> Match to the right of the provided delimiter by
	 * positive lookbehind </li> </ol>
	 *
	 * Thus, it will only match in places where the delimiter is, but won't
	 * actually match the delimiter, leaving split to put it into the stream
	 */
	private static String WITH_DELIM = "(?:(?<=%1$s)|(?=%1$s))";

	/*
	 * This string is a format template for the multi-delimiter matching
	 * regex.
	 *
	 * It does the same thing as the single delimiter regex, but has to have
	 * some negative lookahead/lookbehind assertions to avoid splitting a
	 * delimiter into pieces.
	 */
	private static String WITH_MULTI_DELIM = "(?:(?<=%1$s+)(?!%1$s)|(?<!%1$s)(?=%1$s+))";

	/*
	 * These represent the internal state of the splitter.
	 */
	private StringBuilder	currPatt;
	private StringBuilder	currExclusionPatt;

	/*
	 * These represent the external state of the splitter.
	 *
	 * Compilation causes internal to become external.
	 */
	private Pattern	compPatt;
	private Pattern	exclusionPatt;

	/*
	 * These represent info for debugging.
	 */
	private final Set<String>	delimSet;
	private final Set<String>	multidelimSet;
	private final Set<String>	exclusionSet;

	/**
	 * Create a new token splitter.
	 */
	public SimpleTokenSplitter() {
		delimSet = new HashSet<>();
		multidelimSet = new HashSet<>();
		exclusionSet = new HashSet<>();
	}

	@Override
	public String[] split(final String inp) {
		if (compPatt == null) throw new IllegalStateException("Token splitter has not been compiled yet");

		/*
		 * Don't split something that we should exclude from being
		 * split.
		 */
		if (exclusionPatt.matcher(inp).matches()) return new String[] { inp };

		return compPatt.split(inp);
	}

	/**
	 * Adds one or more strings as matched delimiters to split on.
	 *
	 * Only works for fixed length delimiters.
	 *
	 * The provided strings are regex-escaped before being used.
	 *
	 * @param delims
	 *                The delimiters to match on.
	 */
	public void addDelimiter(final String... delims) {
		for (final String delim : delims) {
			if (delim == null) throw new NullPointerException("Delim must not be null");

			final String quoteDelim = Pattern.quote(delim);
			final String delimPat = String.format(WITH_DELIM, quoteDelim);

			if (currPatt == null) {
				currPatt = new StringBuilder();
				currExclusionPatt = new StringBuilder();

				currPatt.append("(?:" + delimPat + ")");
				currExclusionPatt.append("(?:" + quoteDelim + ")");
			} else {
				currPatt.append("|(?:" + delimPat + ")");
				currExclusionPatt.append("|(?:" + quoteDelim + ")");
			}

			delimSet.add(delim);
		}
	}

	/**
	 * Adds a character class as a matched delimiter to split on.
	 *
	 * The provided string should be a pattern to match one or more
	 * occurances of.
	 *
	 * @param delims
	 *                The delimiter to split on.
	 */
	public void addMultiDelimiter(final String... delims) {
		for (final String delim : delims) {
			if (delim == null) throw new NullPointerException("Delim must not be null");

			final String delimPat = String.format(WITH_MULTI_DELIM, "(?:" + delim + ")");

			if (currPatt == null) {
				currPatt = new StringBuilder();
				currExclusionPatt = new StringBuilder();

				currPatt.append("(?:" + delimPat + ")");
				currExclusionPatt.append("(?:(?:" + delim + ")+)");

			} else {
				currPatt.append("|(?:" + delimPat + ")");
				currExclusionPatt.append("|(?:(?:" + delim + ")+)");
			}

			multidelimSet.add(delim);
		}
	}

	/**
	 * Marks strings matching the pattern delim as non-splittable.
	 *
	 * @param delims
	 *                The regex to not splitting matching strings.
	 */
	public void addNonMatcher(final String... delims) {
		for (final String delim : delims) {
			if (delim == null) throw new NullPointerException("Delim must not be null");

			if (currPatt == null) {
				currPatt = new StringBuilder();
				currExclusionPatt = new StringBuilder();

				currExclusionPatt.append("(?:" + delim + ")");
			} else {
				currExclusionPatt.append("|(?:" + delim + ")");
			}

			exclusionSet.add(delim);
		}
	}

	/**
	 * Compiles the current set of delimiters to a pattern.
	 *
	 * Makes this splitter ready to use.
	 */
	public void compile() {
		if (currPatt == null) {
			currPatt = new StringBuilder();
		}
		if (currExclusionPatt == null) {
			currExclusionPatt = new StringBuilder();
		}

		compPatt = Pattern.compile(currPatt.toString());
		exclusionPatt = Pattern.compile(currExclusionPatt.toString());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		builder.append("SimpleTokenSplitter [");

		if (currPatt != null) {
			builder.append("currPatt=");
			builder.append(currPatt);
			builder.append("\n\t, ");
		}

		if (currExclusionPatt != null) {
			builder.append("currExclusionPatt=");
			builder.append(currExclusionPatt);
			builder.append("\n\t, ");
		}

		if (compPatt != null) {
			builder.append("compPatt=");
			builder.append(compPatt);
			builder.append("\n\t, ");
		}

		if (exclusionPatt != null) {
			builder.append("exclusionPatt=");
			builder.append(exclusionPatt);
			builder.append("\n\t, ");
		}

		if (delimSet != null) {
			builder.append("delimSet=");
			builder.append(delimSet);
			builder.append("\n\t, ");
		}

		if (multidelimSet != null) {
			builder.append("multidelimSet=");
			builder.append(multidelimSet);
			builder.append("\n\t, ");
		}

		if (exclusionSet != null) {
			builder.append("exclusionSet=");
			builder.append(exclusionSet);
		}

		builder.append("]");
		return builder.toString();
	}
}
