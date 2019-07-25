package bjc.utils.ioutils.format.directives;

/**
 *
 * The compiled equivalent of {@link Directive}.
 *
 * @author Ben Culkin.
 */
@FunctionalInterface
public interface Edict {
	/**
	 * Invoke this format directive.
	 *
	 * @param formCTX
	 * 	The state needed for this invocation.
	 */
	public void format(FormatContext formCTX);
}
