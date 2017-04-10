package bjc.utils.parserutils.splitter;

/**
 * Split a string and keep given delimiters.
 *
 * @author Ben Culkin
 */
@Deprecated
public interface TokenSplitter {
	/**
	 * Split a provided string using configured delimiters, and keeping the
	 * delimiters.
	 *
	 * <p>
	 * The splitter must be compiled first.
	 * </p>
	 *
	 * @param inp
	 *                The string to split.
	 *
	 * @return The split string, including delimiters.
	 *
	 * @throws IllegalStateException
	 *                 If the splitter isn't compiled.
	 */
	String[] split(String inp);
}