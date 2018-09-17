package bjc.utils.ioutils;

import java.io.IOException;
import java.io.Writer;

import bjc.utils.esodata.DefaultList;

public class ReportWriter extends Writer {
	private static class IndentVal {
		public int indentStrPos;

		public String indentStr;
		public String indentStrSpacedTabs;

		public IndentVal() {
		}
	}

	private Writer contained;

	// # of character positions indentStr occupies
	private int    indentLevel;

	private DefaultList<IndentVal> iVals;
	private IndentVal              defIVal;

	// # of char. positions to the tab
	private int tabEqv       = 8;
	private int linesWritten = 0;
	private int linePos      = 0;

	private boolean printTabsAsSpaces;

	private boolean lastCharWasNL;
	private char    lastChar;

	// Really wish java had public `readonly` properties. I wouldn't even
	// care if that was a restriction that was only enforced by the compiler
	public int getLevel() {
		return indentLevel;
	}

	public String getString() {
		return defIVal.indentStr;
	}

	public String getString(int lvl) {
		return iVals.get(lvl).indentStr;
	}

	public int getTabEqv() {
		return tabEqv;
	}

	public int getLinesWritter() {
		return linesWritten;
	}

	public int getLinePos() {
		return linePos;
	}

	public char getLastChar() {
		return lastChar;
	}

	public Writer getWriter() {
		return contained;
	}

	public boolean isLastCharNL() {
		return lastCharWasNL;
	}

	public boolean isPrintingTabsAsSpaces() {
		return printTabsAsSpaces;
	}

	public void setPrintTabsAsSpaces(boolean tabsAsSpaces) {
		printTabsAsSpaces = tabsAsSpaces;

		// Recalculate indentStrSpacedTabs
		refreshIndents(-1);
	}

	public void setLevel(int level) {
		indentLevel = level;
	}

	public void setTabEqv(int eqv) {
		tabEqv = eqv;

		// Recalculate position count of indentStr
		refreshIndents(-1);
	}

	// @NOTE 9/5/18
	//
	// Weirdness may occur if the indent string has a
	// newline in it, since that newline won't be considered
	// to exist by the IndentWriter
	public void setString(String str) {
		defIVal.indentStr = str;

		refreshIndents(-2);
	}

	public void setString(int lvl, String str) {
		iVals.get(lvl).indentStr = str;

		refreshIndents(lvl);
	}

	// Parameter is the level of indents to refresh.
	//
	// Pass a index to refresh that level
	// Pass -1 to refresh all indexes
	// Pass -2 to refresh the default index
	private void refreshIndents(int lvl) {
		if (lvl == -2) {
			refreshIndent(defIVal);
		} else if (lvl == -1) {
			for (IndentVal ival : iVals) {
				refreshIndent(ival);
			}
		} else {
			refreshIndent(iVals.get(lvl));
		}
	}

	private void refreshIndent(IndentVal vl) {
		vl.indentStrPos = 0;

		int indentLength = vl.indentStr.length();

		StringBuilder conv = new StringBuilder();
		for(int i = 0; i < indentLength; i++) {
			char c = vl.indentStr.charAt(i);
			if(c == '\t') {
				for(int j = 0; j < tabEqv; j++) {
					conv.append(' ');
				}

				vl.indentStrPos += tabEqv;
			} else {
				conv.append(c);
				vl.indentStrPos += 1;
			}
		}

		vl.indentStrSpacedTabs = conv.toString();
	}

	public ReportWriter duplicate(Writer contents) {
		ReportWriter rw = new ReportWriter(contents);

		rw.iVals       = iVals;
		rw.defIVal     = defIVal;

		rw.indentLevel = indentLevel;

		rw.tabEqv = tabEqv;

		rw.linesWritten = linesWritten;
		rw.linePos      = linePos;

		rw.printTabsAsSpaces = printTabsAsSpaces;

		// @NOTE 9/5/18
		//
		// Not sure if the lastChar* properties are things we should
		// copy.

		return rw;
	}

	public ReportWriter(Writer write) {
		this(write, 0, "\t");
	}

	public ReportWriter(Writer write, int level, String str) {
		super();

		contained = write;

		indentLevel = level;

		defIVal = new IndentVal();
		iVals   = new DefaultList<>(defIVal);

		setString(str);
	}

	public void indent(int lvl) {
		indentLevel += lvl;
	}

	public void indent() {
		indent(1);
	}

	public void dedent(int lvl) {
		// @NOTE 9/5/18
		//
		// Perhaps there should be an exception if we try to dedent too
		// many times?
		indentLevel = Math.max(0, indentLevel - lvl);
	}

	public void dedent() {
		dedent(1);
	}

	public void writeBuffer(StringBuffer sb) throws IOException {
		write(sb.toString());

		// Clear the buffer
		sb.delete(0, sb.length());
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		// Skip empty writes
		if(len == 0) return;

		// Last character was a new line, print the indent string
		if(lastCharWasNL) {
			lastCharWasNL = false;

			printIndents();
		}

		for(int i = 0; i < len; i++) {
			int idx = i + off;

			char c = cbuf[idx];

			if(c == '\n' || c == '\r') {
				// Count lines written
				if(c == '\r')                     linesWritten++;
				if(c == '\n' && lastChar != '\r') linesWritten++;

				lastCharWasNL = true;

				contained.write(c);

				linePos = 0;
			} else {
				// @CopyPaste from above.
				//
				// No real point in pulling it out to a method
				// yet, but if :IndentStr happens, it might
				// warrant it.
				if(lastCharWasNL) {
					lastCharWasNL = false;

					printIndents();
				}

				if(c == '\t') {
					linePos += tabEqv;

					for(int j = 0; j < tabEqv; j++) {
						contained.write(' ');
					}
				} else {
					linePos += 1;
					contained.write(c);
				}
			}

			lastChar = c;
		}
	}

	private void printIndents() throws IOException {
		for (int j = 0; j < indentLevel; j++) {
			IndentVal ival = iVals.get(j);

			if (printTabsAsSpaces) contained.write(ival.indentStrSpacedTabs);
			else                   contained.write(ival.indentStr);

			linePos += ival.indentStrPos;
		}
	}

	@Override
	public void flush() throws IOException {
		contained.flush();
	}

	@Override
	public void close() throws IOException {
		contained.close();
	}

	// @NOTE 9/5/18
	//
	// There are some cases where I can imagine that you might want our
	// extra info, but those are (mostly) minor, and this is more convenient
	// than writing getWriter().toString()
	@Override
	public String toString() {
		return contained.toString();
	}
}
