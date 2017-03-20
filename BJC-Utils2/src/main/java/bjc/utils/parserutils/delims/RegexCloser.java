package bjc.utils.parserutils.delims;

import java.util.function.BiPredicate;

public class RegexCloser implements BiPredicate<String, String[]> {
	private String rep;

	public RegexCloser(String closer) {
		rep = closer;
	}

	@Override
	public boolean test(String closer, String[] params) {
		/*
		 * Confirm passing an array instead of a single var-arg.
		 */
		String work = String.format(rep, (Object[]) params);

		return work.equals(closer);
	}

}