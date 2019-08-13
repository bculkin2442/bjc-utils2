package bjc.utils.ioutils.format;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import bjc.utils.data.*;
import bjc.utils.esodata.*;
import bjc.utils.funcutils.*;
import bjc.utils.ioutils.*;
import bjc.utils.ioutils.format.directives.*;

// Grab our easy converters/constructors
import static bjc.utils.funcutils.IteratorUtils.AI;
import static bjc.utils.funcutils.IteratorUtils.I;

/**
 * An implementation of a string formatter strongly inspired by FORMAT from
 * Common Lisp.
 *
 * I say 'strongly inspired' instead of 'an implementation' because there are
 * differences and extensions between this version of FORMAT, and the one
 * defined by the CLHS.
 * 
 * @author Ben Culkin
 */
public class CLFormatter {
	// Built-in formatting directives
	private static Map<String, Directive> builtinDirectives;

	// Extra directives specific to this formatter
	private        Map<String, Directive> extraDirectives;

	static {
		// Set up the built-in directives
		builtinDirectives = new HashMap<>();

		builtinDirectives.put("A", new AestheticDirective());
		// @NOTE 9/6/18
		//
		// This is just an alias, not the actual S directive
		builtinDirectives.put("S", new AestheticDirective());

		builtinDirectives.put("C", new CharacterDirective());

		builtinDirectives.put("B", new NumberDirective(-1, 2,  'B'));
		builtinDirectives.put("O", new NumberDirective(-1, 8,  'O'));
		builtinDirectives.put("D", new NumberDirective(-1, 10, 'D'));
		builtinDirectives.put("X", new NumberDirective(-1, 16, 'X'));

		builtinDirectives.put("R", new RadixDirective());

		builtinDirectives.put("&", new FreshlineDirective());

		builtinDirectives.put("%", new LiteralDirective("\n", '%'));
		builtinDirectives.put("|", new LiteralDirective("\f", '|'));
		builtinDirectives.put("~", new LiteralDirective("~",  '~'));
		builtinDirectives.put("?", new RecursiveDirective());

		builtinDirectives.put("*", new GotoDirective());

		builtinDirectives.put("^", new EscapeDirective());
		builtinDirectives.put("[", new ConditionalDirective());
		builtinDirectives.put("{", new IterationDirective());

		builtinDirectives.put("(", new CaseDirective());

		builtinDirectives.put("`[", new InflectDirective());

		builtinDirectives.put("T", new TabulateDirective());
	}

	/**
	 * Create a new CL formatter.
	 */
	public CLFormatter() {
		extraDirectives = new HashMap<>();
	}

	/*
	 * @TODO Ben Culkin 9/24/2019 :checkItem
	 * Convert this to return a boolean, not throw an exception.
	 *
	 * In general, I want to cut down on exceptions, except for where it
	 * would be very inconvenient to do so (namely, the EscapeException we
	 * use for the ~^ directive; that would be a pain to implement by hand)
	 */
	/**
	 * Check that an item is valid for a directive.
	 * 
	 * @param itm
	 * 	The item to check.
	 *
	 * @param directive
	 * 	The directive to check for.
	 *
	 * @throws IlegalArgumentException if itm is null.
	 */
	public static void checkItem(Object itm, char directive) {
		if(itm == null) {
			String msg =  String.format("No argument provided for %c directive", directive);

			throw new IllegalArgumentException(msg);
		}
	}

	/**
	 * Format a string in the style of CL's FORMAT.
	 * 
	 * @param format
	 * 	The format string to use.
	 *
	 * @param params
	 * 	The parameters for the string.
	 *
	 * @return The formatted string.
	 *
	 * @throws IOException if something goes wrong during formatting the
	 * string.
	 */
	public String formatString(String format, Object... params) throws IOException {
		return formatString(format, I(AI(params)));
	}
	
	/**
	 * Format a string in the style of CL's FORMAT.
	 * 
	 * @param format
	 * 	The format string to use.
	 *
	 * @param params
	 * 	The parameters for the string.
	 *
	 * @return The formatted string.
	 *
	 * @throws IOException if something goes wrong during formatting the
	 * string.
	 */
	public String formatString(String format, Iterable<Object> params) throws IOException {
		ReportWriter rw = new ReportWriter(new StringWriter());

		/* Put the parameters where we can easily handle them. */
		Tape<Object> tParams = new SingleTape<>(params);

		doFormatString(format, rw, tParams, true);

		return rw.toString();
	}

	/**
	 * Format a string in the style of CL's FORMAT.
	 * 
	 * @param target
	 * 	The writer to send output to.
	 *
	 * @param format
	 * 	The format string to use.
	 *
	 * @param params
	 * 	The parameters for the string.
	 */
	public void formatString(Writer target, String format, Object... params) throws IOException {
		ReportWriter rw = new ReportWriter(target);
		/* Put the parameters where we can easily handle them. */
		Tape<Object> tParams = new SingleTape<>(params);

		doFormatString(format, rw, tParams, true);
	}
	
	/**
	 * Format a string in the style of CL's FORMAT.
	 * 
	 * @param target
	 * 	The writer with configured format options to use.
	 *
	 * @param format
	 * 	The format string to use.
	 *
	 * @param params
	 * 	The parameters for the string.
	 */
	public void formatString(ReportWriter target, String format, Object... params) throws IOException {
		/* Put the parameters where we can easily handle them. */
		Tape<Object> tParams = new SingleTape<>(params);

		doFormatString(format, target, tParams, true);
	}

	/**
	 * Format a string in the style of CL's FORMAT.
	 * 
	 * @param target
	 * 	The writer to send output to.
	 *
	 * @param format
	 * 	The format string to use.
	 *
	 * @param params
	 * 	The parameters for the string.
	 */
	public void formatString(Writer target, String format, Iterable<Object> params) throws IOException {
		ReportWriter rw = new ReportWriter(target);

		/* Put the parameters where we can easily handle them. */
		Tape<Object> tParams = new SingleTape<>(params);

		doFormatString(format, rw, tParams, true);
	}

	/**
	 * Fill in a partially started format string.
	 * 
	 * Used mostly for directives that require formatting again with a
	 * different string.
	 * 
	 * @param format
	 * 	The format to use.
	 *
	 * @param rw
	 * 	The buffer to file output into.
	 *
	 * @param tParams
	 * 	The parameters to use.
	 *
	 * @param isToplevel 
	 * 	Whether or not this is a top-level format
	 *
	 * @throws IOException If something goes wrong
	 */
	public void doFormatString(String format, ReportWriter rw, Tape<Object> tParams, boolean isToplevel) throws IOException {
		CLTokenizer cltok = new CLTokenizer(format);

		doFormatString(cltok, rw, tParams, isToplevel);
	}

	/**
	 * Fill in a partially started format string.
	 * 
	 * Used mostly for directives that require formatting again with a
	 * different string.
	 * 
	 * @param cltok
	 * 	The place to get tokens from.
	 *
	 * @param rw
	 * 	The buffer to file output into.
	 *
	 * @param tParams
	 * 	The parameters to use.
	 *
	 * @param isToplevel 
	 * 	Whether or not this is a top-level format
	 *
	 * @throws IOException If something goes wrong
	 */
	public void doFormatString(Iterable<Decree> cltok, ReportWriter rw, Tape<Object> tParams, boolean isToplevel) throws IOException {
		doFormatString(cltok.iterator(), rw, tParams, isToplevel);
	}

	/**
	 * Fill in a partially started format string.
	 * 
	 * Used mostly for directives that require formatting again with a
	 * different string.
	 * 
	 * @param cltok
	 * 	The place to get tokens from.
	 *
	 * @param rw
	 * 	The buffer to file output into.
	 *
	 * @param tParams
	 * 	The parameters to use.
	 *
	 * @param isToplevel 
	 * 	Whether or not this is a top-level format
	 *
	 * @throws IOException If something goes wrong
	 */
	public void doFormatString(Iterator<Decree> cltok, ReportWriter rw, Tape<Object> tParams, boolean isToplevel) throws IOException {
		boolean doTail = true;

		try {
			while (cltok.hasNext()) {
				Decree decr = cltok.next();

				if (decr.isLiteral) {
					rw.write(decr.name);
					continue;
				}

				Object item = tParams.item();

				if(decr.isUserCall) {
					/*
					 * @TODO implement user-called functions.
					 */
					continue;
				}

				if(extraDirectives.containsKey(decr.name)) {
					FormatParameters params = new FormatParameters(rw, item, decr, tParams, cltok, this);

					extraDirectives.get(decr.name).format(params);

					continue;
				}

				if(builtinDirectives.containsKey(decr.name)) {
					FormatParameters params = new FormatParameters(rw, item, decr, tParams, cltok, this);

					builtinDirectives.get(decr.name).format(params); 
					continue;
				}

				if(decr.name == null) decr.name = "<null>";

				switch(decr.name) {
					case "]":
						throw new IllegalArgumentException("Found conditional-end outside of conditional.");
					case ";":
						throw new IllegalArgumentException(
								"Found seperator outside of block.");
					case "}":
						throw new IllegalArgumentException("Found iteration-end outside of iteration");
					case ")":
						throw new IllegalArgumentException("Case-conversion end outside of case conversion");
					case "`]":
						throw new IllegalArgumentException("Inflection-end outside of inflection");
					case "<":
					case ">":
						throw new IllegalArgumentException("Inflection marker outside of inflection");
					case "`<":
					case "`>":
						throw new IllegalArgumentException("Layout-control directives aren't implemented yet.");
					case "F":
					case "E":
					case "G":
					case "$":
						/* @TODO 
						 *
						 * implement floating point directives.
						 */
						throw new IllegalArgumentException("Floating-point directives aren't implemented yet.");
					case "W":
						/*
						 * @TODO 
						 *
						 * figure out if we want to
						 * implement someting for these
						 * directives instead of
						 * punting.
						 */
						throw new IllegalArgumentException("S and W aren't implemented. Use A instead");
					case "P":
						throw new IllegalArgumentException("These directives aren't implemented yet");
					case "\n":
						/*
						 * Ignored newline.
						 */
						break;
					default:
						String msg = String.format("Unknown format directive '%s'", decr.name);
						throw new IllegalArgumentException(msg);
				}
			}
		} catch (EscapeException eex) {
			if (!isToplevel) throw eex;

			doTail = false;
		}
	}
}
