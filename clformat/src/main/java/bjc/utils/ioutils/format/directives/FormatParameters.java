package bjc.utils.ioutils.format.directives;

import java.util.*;

import bjc.utils.esodata.*;
import bjc.utils.ioutils.*;
import bjc.utils.ioutils.format.*;

public class FormatParameters {
	public ReportWriter rw;

	public Object item;

	public Decree decr;

	public Tape<Object> tParams;

	public Iterator<Decree> dirIter;

	public CLFormatter fmt;

	public FormatParameters(ReportWriter rw, Object item, Decree decr, Tape<Object> tParams,
			Iterator<Decree> dirIter, CLFormatter fmt) {
		this.rw = rw;

		this.item = item;

		this.decr = decr;

		this.tParams = tParams;

		this.dirIter = dirIter;

		this.fmt = fmt;
	}

	public CLParameters getParams() {
		return decr.parameters;
	}

	public CLModifiers getMods() {
		return decr.modifiers;
	}

	public CompileContext toCompileCTX() {
		return new CompileContext(dirIter, fmt, decr);
	}

	public FormatContext toFormatCTX() {
		return new FormatContext(rw, tParams);
	}
}
