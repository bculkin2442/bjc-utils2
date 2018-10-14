package bjc.utils.ioutils;

import java.io.IOException;
import java.io.Writer;

import bjc.utils.esodata.DefaultList;

/**
 * A writer with support for some formatting operations, such as autoindentation
 * and pagination.
 * 
 * @author bjculkin
 *
 */
public class ReportWriter extends Writer {
	/*
	 * Storage of indentation values.
	 */
	private static class IndentVal {
		// # of character positions indentStr occupies
		public int indentStrPos;

		// String that is printed for the indent.
		public String indentStr;
		// Indent string w/ tabs replaced with spaces
		public String indentStrSpacedTabs;

		public IndentVal() {
		}
	}

	// Writer to print to
	private Writer contained;

	// Current indentation level
	private int indentLevel;
	// Current # of columns the indentation occupies
	private int indentPos = 0;

	// The indentation values to use
	private DefaultList<IndentVal> iVals;
	// The default indentation value.
	private IndentVal defIVal;

	// # of char. positions to the tab
	private int tabEqv = 8;
	// @NOTE 9/17/18
	//
	// Consider adding support for both the vertical tab, and variable tab
	// stops.
	//
	// For variable tab stops, decide between a set of numbers saying 'This
	// level tab is counted as X spaces' and a set of numbers saying 'A tab
	// advances to the next tab stop that has not been passed'

	// The total count of lines printed
	private int linesWritten = 0;
	// The current position in the line
	private int linePos = 0;

	// The number of newlines to print for every newline encountered.
	private int lineSpacing = 1;

	// The current line on the page
	private int pageLine = 0;
	// The current page number
	private int pageNum = 0;
	// The number of lines per page
	private int linesPerPage = 20;

	// Whether or not to print tabs as spaces.
	private boolean printTabsAsSpaces;

	// Whether or not the last character was a newline
	private boolean lastCharWasNL;
	// The last character encountered
	private char lastChar;

	// Really wish java had public `readonly` properties. I wouldn't even
	// care if that was a restriction that was only enforced by the compiler

	/**
	 * Get the current indent level.
	 * 
	 * @return The current indent level.
	 */
	public int getLevel() {
		return indentLevel;
	}

	/**
	 * Get the current line-spacing.
	 * 
	 * This is the number of blank lines to print out every time a blank
	 * line is encountered in the input, and is set to 1 by default.
	 * 
	 * @return The current line spacing.
	 */
	public int getLineSpacing() {
		return lineSpacing;
	}

	/**
	 * Get the current line on the page.
	 * 
	 * @return The current line on the page.
	 */
	public int getPageLine() {
		return pageLine;
	}

	/**
	 * Get the current page number.
	 * 
	 * @return The current page number.
	 */
	public int getPageNum() {
		return pageNum;
	}

	/**
	 * Get the number of lines per page.
	 * 
	 * @return The number of lines per page.
	 */
	public int getLinesPerPage() {
		return linesPerPage;
	}

	/**
	 * Get the current indent position.
	 * 
	 * This is the count of columns the indentation occupies.
	 * 
	 * @return The current indent position.
	 */
	public int getIndentPos() {
		return indentPos;
	}

	/**
	 * Get the string of the default indentation value.
	 * 
	 * @return The string for the default indentation value.
	 */
	public String getString() {
		return defIVal.indentStr;
	}

	/**
	 * Get the string of a specific indentation value.
	 * 
	 * @param lvl
	 *                The level to get the value for.
	 * @return The string for the specified indentation value.
	 */
	public String getString(int lvl) {
		return iVals.get(lvl).indentStr;
	}

	/**
	 * Get the number of spaces to a tab.
	 * 
	 * @return The number of spaces to a tab.
	 */
	public int getTabEqv() {
		return tabEqv;
	}

	/**
	 * Get the total number of lines written.
	 * 
	 * @return The total number of lines written.
	 */
	public int getLinesWritter() {
		return linesWritten;
	}

	/**
	 * Get the current position in the line.
	 * 
	 * @return The current position in the line.
	 */
	public int getLinePos() {
		return linePos;
	}

	/**
	 * Get the last character printed.
	 * 
	 * @return The last character printed.
	 */
	public char getLastChar() {
		return lastChar;
	}

	/**
	 * Get the contained writer.
	 * 
	 * @return The contained writer.
	 */
	public Writer getWriter() {
		return contained;
	}

	/**
	 * Check if the last character was a newline.
	 * 
	 * @return Was the last character a new line?
	 */
	public boolean isLastCharNL() {
		return lastCharWasNL;
	}

	/**
	 * Check if tabs are being printed as spaces.
	 * 
	 * @return Are tabs being printed as spaces?
	 */
	public boolean isPrintingTabsAsSpaces() {
		return printTabsAsSpaces;
	}

	/**
	 * Set the line spacing.
	 * 
	 * @param spacing
	 *                The number of lines to print for every blank line
	 *                encountered.
	 */
	public void setLineSpacing(int spacing) {
		lineSpacing = spacing;
	}

	/**
	 * Set whether tabs are being printed as spaces.
	 * 
	 * @param tabsAsSpaces
	 *                Whether to print tabs as spaces.
	 */
	public void setPrintTabsAsSpaces(boolean tabsAsSpaces) {
		printTabsAsSpaces = tabsAsSpaces;

		// Recalculate indentStrSpacedTabs
		refreshIndents(-1);
	}

	/**
	 * Set the number of lines per page.
	 * 
	 * @param lines
	 *                The number of lines per page.
	 */
	public void setLinesPerPage(int lines) {
		linesPerPage = lines;

		// @NOTE 9/17/18
		//
		// This is somewhat questionable as to what should happen, as
		// the lines have already been printed.
		//
		// Right now, we just call writePage, which resets the line
		// counts. If writePage gets changed to add additional
		// functionality, it is questionable as to what we should do in
		// that case; whether we should continue calling the function,
		// or just adjust the counts and pretend that nothing
		// significant happened
		while (pageLine > linesPerPage) {
			writePage();
		}
	}

	/**
	 * Set the current indentation level.
	 * 
	 * @param level
	 *                The indentation level.
	 */
	public void setLevel(int level) {
		indentLevel = level;
	}

	/**
	 * Set the current amount of spaces per tab.
	 * 
	 * @param eqv
	 *                The amount of spaces per tab.
	 */
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

	/**
	 * Set the default indentation string to use.
	 * 
	 * NOTE: Using a string that contains a newline may cause weirdness of
	 * various sorts to happen.
	 * 
	 * @param str
	 *                The string to use for default indentation.
	 */
	public void setString(String str) {
		defIVal.indentStr = str;

		refreshIndents(-2);
	}

	/**
	 * Set the indentation string for a specific indentation level.
	 * 
	 * @param lvl
	 *                The level to set the indentation string for.
	 * @param str
	 *                The indentation string to use.
	 */
	public void setString(int lvl, String str) {
		iVals.get(lvl).indentStr = str;

		refreshIndents(lvl);
	}

	// Parameter is the level of indents to refresh.
	//
	// Pass a index to refresh that level
	// Pass -1 to refresh all indexes
	// Pass -2 to refresh the default index
	/**
	 * Refresh the indentation settings.
	 * 
	 * @param lvl
	 *                The level of indents to refresh. Passing a number >= 0
	 *                refreshes that level, -1 refreshes every level, and -2
	 *                refreshes just the default indentation.
	 */
	private void refreshIndents(int lvl) {
		if (lvl == -2) {
			refreshIndent(defIVal);
		} else if (lvl == -1) {
			for (IndentVal ival : iVals) {
				refreshIndent(ival);
			}
			
			refreshIndent(defIVal);
		} else {
			refreshIndent(iVals.get(lvl));
		}
	}

	// Refresh an indent val to respect current settings
	private void refreshIndent(IndentVal vl) {
		vl.indentStrPos = 0;

		int indentLength = vl.indentStr.length();

		StringBuilder conv = new StringBuilder();
		for (int i = 0; i < indentLength; i++) {
			char c = vl.indentStr.charAt(i);
			if (c == '\t') {
				for (int j = 0; j < tabEqv; j++) {
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

	/**
	 * Duplicate this writers settings.
	 * @param contents The internal writer to use.
	 * @return A new writer, sharing this ones settings, but writing to the provided Writer instead.
	 */
	public ReportWriter duplicate(Writer contents) {
		ReportWriter rw = new ReportWriter(contents);

		rw.iVals = iVals;
		rw.defIVal = defIVal;

		rw.indentLevel = indentLevel;
		rw.indentPos = indentPos;

		rw.tabEqv = tabEqv;

		rw.linesWritten = linesWritten;
		rw.linePos = linePos;
		rw.lineSpacing = lineSpacing;

		rw.printTabsAsSpaces = printTabsAsSpaces;

		rw.pageLine = pageLine;
		rw.pageNum = pageNum;
		rw.linesPerPage = linesPerPage;

		// @NOTE 9/5/18
		//
		// Not sure if the lastChar* properties are things we should
		// copy.

		return rw;
	}

	/**
	 * Create a new ReportWriter.
	 * @param write The place to write to.
	 */
	public ReportWriter(Writer write) {
		this(write, 0, "\t");
	}

	/**
	 * Create a new ReportWriter with the specified default indentation values.
	 * @param write The place to write to.
	 * @param level The current indentation level.
	 * @param indentStr The indentation string.
	 */
	public ReportWriter(Writer write, int level, String indentStr) {
		super();

		contained = write;

		indentLevel = level;

		defIVal = new IndentVal();
		iVals = new DefaultList<>(defIVal);

		setString(indentStr);
	}

	/**
	 * Indent a specific number of levels.
	 * @param lvl The number of levels to indent.
	 */
	public void indent(int lvl) {
		indentLevel += lvl;
	}

	/**
	 * Indent one level.
	 */
	public void indent() {
		indent(1);
	}

	/**
	 * Dedent a specific number of levels.
	 * @param lvl The number of levels to dedent.
	 */
	public void dedent(int lvl) {
		// @NOTE 9/5/18
		//
		// Perhaps there should be an exception if we try to dedent too
		// many times?
		indentLevel = Math.max(0, indentLevel - lvl);
	}

	/**
	 * Dedent 1 level.
	 */
	public void dedent() {
		dedent(1);
	}

	/**
	 * Writes a buffer to the output, then clears it.
	 * @param sb The buffer to write and clear.
	 * @throws IOException If something goes wrong writing the buffer.
	 */
	public void writeBuffer(StringBuffer sb) throws IOException {
		write(sb.toString());

		// Clear the buffer
		sb.delete(0, sb.length());
	}

	// @NOTE 9/17/18
	//
	// Need to make some decision about how to handle doing a new page, and
	// whether we want to simulate it with simply newlines until we hit the
	// next page, or just printing the actual form-feed and hoping whatever
	// reading software handles it properly
	private void writePage() {
		pageNum += 1;
		pageLine -= linesPerPage;
	}

	private void writeNL(char c) throws IOException {
		// Count lines written
		linesWritten += lineSpacing;
		pageLine += lineSpacing;

		lastCharWasNL = true;

		for (int i = 0; i < lineSpacing; i++) {
			contained.write(c);

			// If we're printing CRLF pairs, make sure that we don't
			// print incomplete pairs.
			if (i < lineSpacing - 1) {
				if (c == '\n' && lastChar == '\r') contained.write('\r');
			}
		}

		// @NOTE 9/17/18
		//
		// Not sure if this should be here, or before the above loop
		if (pageLine > linesPerPage || c == '\f') {
			writePage();
		}

		linePos = 0;
		indentPos = 0;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		// Skip empty writes
		if (len == 0) return;

		// Last character was a new line, print the indent string
		if (lastCharWasNL) {
			lastCharWasNL = false;

			printIndents();
		}

		for (int i = 0; i < len; i++) {
			int idx = i + off;

			char c = cbuf[idx];

			if ((c == '\n' && lastChar != '\r') || c == '\n' || c == '\r' ||  c == '\f') {
				writeNL(c);
			} else {
				if (lastCharWasNL) {
					lastCharWasNL = false;

					printIndents();
				}

				if (c == '\t') {
					linePos += tabEqv;

					for (int j = 0; j < tabEqv; j++) {
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

			if (printTabsAsSpaces)
				contained.write(ival.indentStrSpacedTabs);
			else contained.write(ival.indentStr);

			linePos += ival.indentStrPos;
			indentPos += ival.indentStrPos;
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
