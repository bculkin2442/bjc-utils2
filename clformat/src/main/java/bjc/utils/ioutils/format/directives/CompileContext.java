package bjc.utils.ioutils.format.directives;

import java.util.*;

import bjc.utils.esodata.*;
import bjc.utils.ioutils.format.*;

/**
 * Encapsulates the state necessary for compiling Directives into Edicts.
 *
 * @author Ben Culkin
 */
public class CompileContext {
	/**
	 * The stream of parsed directives.
	 */
	public Iterator<Decree> directives;

	/**
	 * The configured formatter instance we are using.
	 */
	public CLFormatter formatter;

	/**
	 * The decree that is currently being parsed.
	 */
	public Decree decr;

	public CompileContext(Iterator<Decree> dirs, CLFormatter fmt, Decree dcr) {
		directives = dirs;
		formatter = fmt;
		
		decr = dcr;
	}
}
