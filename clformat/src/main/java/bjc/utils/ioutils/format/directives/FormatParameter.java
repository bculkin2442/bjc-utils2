package bjc.utils.ioutils.format.directives;

import java.util.regex.Matcher;

import bjc.utils.esodata.Tape;
import bjc.utils.ioutils.ReportWriter;
import bjc.utils.ioutils.format.CLFormatter;
import bjc.utils.ioutils.format.CLModifiers;
import bjc.utils.ioutils.format.CLParameters;

public class FormatParameter {
	public ReportWriter	rw;
	public Object		item;
	public CLModifiers	mods;
	public CLParameters	arrParams;
	public Tape<Object>	tParams;
	public Matcher		dirMatcher;
	public CLFormatter	fmt;

	public FormatParameter(ReportWriter rw, Object item, CLModifiers mods, CLParameters arrParams,
			Tape<Object> tParams, Matcher dirMatcher, CLFormatter fmt) {
		this.rw = rw;
		this.item = item;
		this.mods = mods;
		this.arrParams = arrParams;
		this.tParams = tParams;
		this.dirMatcher = dirMatcher;
		this.fmt = fmt;
	}
}