package bjc.utils.parserutils.delims;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bjc.data.Pair;
import bjc.data.SimplePair;

/**
 * A predicated opener for use with {@link RegexCloser}
 *
 * @author bjculkin
 *
 */
public class RegexOpener implements Function<String, Pair<String, String[]>> {
	/* The name of the group. */
	private final String name;
	/* The pattern that marks an opening group. */
	private final Pattern patt;

	/**
	 * Create a new regex opener.
	 *
	 * @param groupName
	 *                   The name of the opened group.
	 *
	 * @param groupRegex
	 *                   The regex that matches the opener.
	 */
	public RegexOpener(final String groupName, final String groupRegex) {
		name = groupName;

		patt = Pattern.compile(groupRegex);
	}

	@Override
	public Pair<String, String[]> apply(final String str) {
		final Matcher m = patt.matcher(str);

		if (m.matches()) {
			final int numGroups = m.groupCount();

			final String[] parms = new String[numGroups + 1];

			for (int i = 0; i <= numGroups; i++) {
				parms[i] = m.group(i);
			}

			return new SimplePair<>(name, parms);
		}

		return new SimplePair<>(null, null);
	}
}
