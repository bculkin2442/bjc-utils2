package bjc.utils.ioutils.format.directives;

import java.io.*;

import bjc.utils.ioutils.format.*;

/**
 * Implements directives that create a literal string.
 * 
 * @author student
 *
 */
public class LiteralDirective implements Directive {
	private String lit;

	/**
	 * Create a new literal directive.
	 * 
	 * @param lit
	 *            The string for the directive.
	 * @param directive
	 *            The character for the directive.
	 */
	public LiteralDirective(String lit, char directive) {
		Character.toString(directive);
		this.lit = lit;
	}

	@Override
	public Edict compile(CompileContext compCTX) {
		CLValue nTimes = null;

		CLParameters params = compCTX.decr.parameters;

		if (params.length() >= 1) {
			params.mapIndices("count");

			nTimes = params.resolveKey("count");
		}

		return new LiteralEdict(lit, nTimes);
	}
}

/*
 * Compiled version of the directive.
 */
class LiteralEdict implements Edict {
	private String lit;
	private CLValue nTimes;

	public LiteralEdict(String lit, CLValue nTimes) {
		this.lit = lit;

		this.nTimes = nTimes;
	}

	@Override
	public void format(FormatContext formCTX) throws IOException {
		int num = 1;

		if (nTimes != null) {
			num = nTimes.asInt(formCTX.items, "occurance count", "literal", 1);
		}

		for (int i = 0; i < num; i++) {
			formCTX.writer.write(lit);
		}
	}
}
