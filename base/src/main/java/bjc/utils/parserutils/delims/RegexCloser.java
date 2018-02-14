package bjc.utils.parserutils.delims;

import java.util.function.BiPredicate;

/**
 * A predicated closer for use with {@link RegexOpener}.
 *
 * @author bjculkin
 *
 */
public class RegexCloser implements BiPredicate<String, String[]> {
	/* Closing string. */
	private final String rep;

	/**
	 * Create a new regex closer.
	 *
	 * @param closer
	 *        The format string to use for closing.
	 */
	public RegexCloser(final String closer) {
		rep = closer;
	}

	@Override
	public boolean test(final String closer, final String[] params) {
		/*
		 * Confirm passing an array instead of a single var-arg.
		 */
		final String work = String.format(rep, (Object[]) params);

		return work.equals(closer);
	}
}
