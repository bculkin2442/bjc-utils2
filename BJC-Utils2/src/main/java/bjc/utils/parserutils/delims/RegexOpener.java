package bjc.utils.parserutils.delims;

import bjc.utils.data.IPair;
import bjc.utils.data.Pair;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexOpener implements Function<String, IPair<String, String[]>> {
	private String name;

	private Pattern patt;

	public RegexOpener(String groupName, String groupRegex) {
		name = groupName;

		patt = Pattern.compile(groupRegex);
	}

	@Override
	public IPair<String, String[]> apply(String str) {
		Matcher m = patt.matcher(str);

		if(m.matches()) {
			int numGroups = m.groupCount();

			String[] parms = new String[numGroups + 1];

			for(int i = 0; i <= numGroups; i++) {
				parms[i] = m.group(i);
			}

			return new Pair<>(name, parms);
		}

		return new Pair<>(null, null);
	}
}