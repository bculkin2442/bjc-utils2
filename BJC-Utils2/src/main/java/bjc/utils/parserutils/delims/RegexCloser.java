package bjc.utils.parserutils.delims;

import java.util.function.BiPredicate;

/**
 * A predicated closer for use with {@link RegexOpener}.
 * 
 * @author bjculkin
 *
 */
public class RegexCloser implements BiPredicate<String, String[]> {
	private String rep;

	/**
	 * Create a new regex closer.
	 * 
	 * @param closer
	 *                The format string to use for closing.
	 */
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