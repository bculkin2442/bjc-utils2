package bjc.utils.parserutils.delims;

import bjc.utils.data.IPair;
import bjc.utils.data.Pair;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A predicated opener for use with {@link RegexOpener}
 * 
 * @author bjculkin
 *
 */
public class RegexOpener implements Function<String, IPair<String, String[]>> {
	private String name;

	private Pattern patt;

	/**
	 * Create a new regex opener.
	 * 
	 * @param groupName
	 *                The name of the opened group.
	 * 
	 * @param groupRegex
	 *                The regex that matches the opener.
	 */
	public RegexOpener(String groupName, String groupRegex) {
		name = groupName;

		patt = Pattern.compile(groupRegex);
	}

	@Override
	public IPair<String, String[]> apply(String str) {
		Matcher m = patt.matcher(str);

		if (m.matches()) {
			int numGroups = m.groupCount();

			String[] parms = new String[numGroups + 1];

			for (int i = 0; i <= numGroups; i++) {
				parms[i] = m.group(i);
			}

			return new Pair<>(name, parms);
		}

		return new Pair<>(null, null);
	}
}