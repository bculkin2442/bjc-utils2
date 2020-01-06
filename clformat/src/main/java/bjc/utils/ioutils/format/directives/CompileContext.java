package bjc.utils.ioutils.format.directives;

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
	public CLTokenizer directives;

	/**
	 * The configured formatter instance we are using.
	 */
	public CLFormatter formatter;

	/**
	 * The decree that is currently being parsed.
	 */
	public Decree decr;

	/**
	 * Create a new compilation context.
	 * 
	 * @param dirs
	 * 	The directives to compile from.
	 * 
	 * @param fmt
	 * 	The formatter being used to compile.
	 * 
	 * @param dcr
	 * 	The decree currently being compiled.
	 */
	public CompileContext(CLTokenizer dirs, CLFormatter fmt, Decree dcr) {
		directives = dirs;
		formatter = fmt;
		
		decr = dcr;
	}
}
