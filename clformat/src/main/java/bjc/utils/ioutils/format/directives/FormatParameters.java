package bjc.utils.ioutils.format.directives;

import java.util.Iterator;

import bjc.utils.esodata.Tape;
import bjc.utils.ioutils.ReportWriter;
import bjc.utils.ioutils.format.CLFormatter;
import bjc.utils.ioutils.format.CLModifiers;
import bjc.utils.ioutils.format.CLParameters;

public class FormatParameters {
	public ReportWriter	rw;
	public Object		item;
	public CLModifiers	mods;
	public CLParameters	arrParams;
	public Tape<Object>	tParams;
	public Iterator<String>	dirIter;
	public CLFormatter	fmt;

	public FormatParameters(ReportWriter rw, Object item, CLModifiers mods, CLParameters arrParams,
			Tape<Object> tParams, Iterator<String> dirIter, CLFormatter fmt) {
		this.rw        = rw;
		this.item      = item;
		this.mods      = mods;
		this.arrParams = arrParams;
		this.tParams   = tParams;
		this.dirIter   = dirIter;
		this.fmt       = fmt;
	}
}
