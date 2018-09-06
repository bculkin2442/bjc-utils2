package bjc.utils.ioutils;

import java.io.IOException;
import java.io.Writer;

public class ReportWriter extends Writer {
	private Writer contained;

	// @TODO Ben Culkin :IndentStr 9/5/18
	//
	// I can think of several ways I might want to extend/change this, but
	// no good ways to implement them come to mind.
	//
	// The main one is for things like bulleted lists, where at each level
	// you want to use a different indent string.

	// # of character positions indentStr occupies
	private int    indentStrPos;
	private int    indentLevel;
	private String indentStr;
	// Indent string w/ tabs converted to spaces
	private String indentStrSpacedTabs;

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
		return indentStr;
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

	public boolean isLastCharNL() {
		return lastCharWasNL;
	}

	public boolean isPrintingTabsAsSpaces() {
		return printTabsAsSpaces;
	}

	public void setPrintTabsAsSpaces(boolean tabsAsSpaces) {
		printTabsAsSpaces = tabsAsSpaces;

		// Recalculate indentStrSpacedTabs
		setString(indentStr);
	}

	public void setLevel(int level) {
		indentLevel = level;
	}

	public void setTabEqv(int eqv) {
		tabEqv = eqv;

		// Recalculate position count of indentStr
		setString(indentStr);
	}

	// @NOTE 9/5/18
	//
	// Weirdness may occur if the indent string has a
	// newline in it, since that newline won't be considered
	// to exist by the indentWriter
	public void setString(String str) {
		indentStr = str;

		indentStrPos = 0;
		StringBuilder conv = new StringBuilder();

		for(int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if(c == '\t') {
				for(int j = 0; j < tabEqv; j++) {
					conv.append(' ');
				}

				indentStrPos += tabEqv;
			} else {
				conv.append(c);
			}

			indentStrPos += 1;
		}
	}

	public ReportWriter(Writer write) {
		this(write, 0, "\t");
	}

	public ReportWriter(Writer write, int level, String str) {
		super();

		contained = write;

		indentLevel = level;

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
		// Perhaps there should be an exception if we try to dedent to
		// many times?
		indentLevel = Math.max(0, indentLevel - lvl);
	}

	public void dedent() {
		dedent(1);
	}

	public void write(char[] cbuf, int off, int len) throws IOException {
		String actIndentStr = printTabsAsSpaces ? indentStrSpacedTabs : indentStr;

		// Skip empty writes
		if(len == 0) return;

		// Last character was a new line, print the indent string
		if(lastCharWasNL) {
			lastCharWasNL = false;

			for(int i = 0; i < indentLevel; i++) {
				contained.write(actIndentStr);
			}

			linePos += indentStrPos;
		}

		for(int i = 0; i < len; i++) {
			int idx = i + off;

			char c = cbuf[off];

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

					for(int j = 0; j < indentLevel; j++) {
						contained.write(actIndentStr);
						linePos += indentStrPos;
					}
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

	public void flush() throws IOException {
		contained.flush();
	}

	public void close() throws IOException {
		contained.close();
	}
}
