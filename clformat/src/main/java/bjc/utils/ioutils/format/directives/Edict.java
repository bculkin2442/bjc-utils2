package bjc.utils.ioutils.format.directives;

import java.io.*;

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
	 *                The state needed for this invocation.
	 * @throws IOException Thrown if an I/O error happens.
	 */
	public void format(FormatContext formCTX) throws IOException;
}
