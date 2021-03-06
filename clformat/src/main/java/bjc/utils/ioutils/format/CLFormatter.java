package bjc.utils.ioutils.format;

import java.io.*;
import java.util.*;

import bjc.esodata.*;
import bjc.utils.ioutils.ReportWriter;
import bjc.utils.ioutils.format.directives.*;

// Grab our easy converters/constructors
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
	/**
	 * Set this to true to enable additional debug output.
	 */
	public boolean DEBUG;

	// Built-in formatting directives
	private static Map<String, Directive> builtinDirectives;

	// Extra directives specific to this formatter
	private Map<String, Directive> extraDirectives;

	static {
		// Set up the built-in directives
		builtinDirectives = new HashMap<>();

		builtinDirectives.put("A", new AestheticDirective());
		// @NOTE 9/6/18
		//
		// This is just an alias, not the actual S directive
		builtinDirectives.put("S", new AestheticDirective());

		builtinDirectives.put("C", new CharacterDirective());

		builtinDirectives.put("B", new NumberDirective(-1, 2, 'B'));
		builtinDirectives.put("O", new NumberDirective(-1, 8, 'O'));
		builtinDirectives.put("D", new NumberDirective(-1, 10, 'D'));
		builtinDirectives.put("X", new NumberDirective(-1, 16, 'X'));

		builtinDirectives.put("R", new RadixDirective());

		builtinDirectives.put("&", new FreshlineDirective());

		builtinDirectives.put("%", new LiteralDirective("\n", '%'));
		builtinDirectives.put("|", new LiteralDirective("\f", '|'));
		builtinDirectives.put("~", new LiteralDirective("~", '~'));
		builtinDirectives.put("?", new RecursiveDirective());

		builtinDirectives.put("*", new GotoDirective());

		builtinDirectives.put("^", new EscapeDirective());
		builtinDirectives.put("[", new ConditionalDirective());
		builtinDirectives.put("{", new IterationDirective());

		builtinDirectives.put("(", new CaseDirective());

		builtinDirectives.put("`[", new InflectDirective());

		builtinDirectives.put("T", new TabulateDirective());

		builtinDirectives.put("`D", new DecimalDirective());
	}

	/**
	 * Create a new CL formatter.
	 */
	public CLFormatter() {
		extraDirectives = new HashMap<>();
	}

	/*
	 * @TODO Ben Culkin 9/24/2019 :checkItem Convert this to return a boolean, not
	 * throw an exception.
	 *
	 * In general, I want to cut down on exceptions, except for where it would be
	 * very inconvenient to do so (namely, the EscapeException we use for the ~^
	 * directive; that would be a pain to implement by hand)
	 */
	/**
	 * Check that an item is valid for a directive.
	 *
	 * @param itm
	 *                  The item to check.
	 *
	 * @param directive
	 *                  The directive to check for.
	 *
	 * @throws IllegalArgumentException
	 *                                  if itm is null.
	 */
	public static void checkItem(Object itm, char directive) {
		if (itm == null) {
			String msg = String.format("No argument provided for %c directive", directive);

			throw new IllegalArgumentException(msg);
		}
	}

	/**
	 * Check that an item is valid for a directive.
	 *
	 * @param itm
	 *                  The item to check.
	 *
	 * @param directive
	 *                  The directive to check for.
	 *
	 * @throws IllegalArgumentException
	 *                                  if itm is null.
	 */
	public static void checkItem(Object itm, String directive) {
		if (itm == null) {
			String msg = String.format("No argument provided for %s directive", directive);

			throw new IllegalArgumentException(msg);
		}
	}
	/**
	 * Format a string in the style of CL's FORMAT.
	 *
	 * @param format
	 *               The format string to use.
	 *
	 * @param params
	 *               The parameters for the string.
	 *
	 * @return The formatted string.
	 *
	 * @throws IOException
	 *                     if something goes wrong during formatting the string.
	 */
	public String formatString(String format, Object... params) throws IOException {
		return formatString(format, I(I(params)));
	}

	/**
	 * Format a string in the style of CL's FORMAT.
	 *
	 * @param format
	 *               The format string to use.
	 *
	 * @param params
	 *               The parameters for the string.
	 *
	 * @return The formatted string.
	 *
	 * @throws IOException
	 *                     if something goes wrong during formatting the string.
	 */
	public String formatString(String format, Iterable<Object> params)
			throws IOException {
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
	 *               The writer to send output to.
	 *
	 * @param format
	 *               The format string to use.
	 *
	 * @param params
	 *               The parameters for the string.
	 *
	 * @throws IOException
	 *                     If something I/O related goes wrong.
	 */
	public void formatString(Writer target, String format, Object... params)
			throws IOException {
		try (ReportWriter rw = new ReportWriter(target)) {
			/*
			 * Put the parameters where we can easily handle them.
			 */
			Tape<Object> tParams = new SingleTape<>(params);

			doFormatString(format, rw, tParams, true);
		}
	}

	/**
	 * Format a string in the style of CL's FORMAT.
	 *
	 * @param target
	 *               The writer with configured format options to use.
	 *
	 * @param format
	 *               The format string to use.
	 *
	 * @param params
	 *               The parameters for the string.
	 *
	 * @throws IOException
	 *                     If something I/O related goes wrong.
	 */
	public void formatString(ReportWriter target, String format, Object... params)
			throws IOException {
		/* Put the parameters where we can easily handle them. */
		Tape<Object> tParams = new SingleTape<>(params);

		doFormatString(format, target, tParams, true);
	}

	/**
	 * Format a string in the style of CL's FORMAT.
	 *
	 * @param target
	 *               The writer to send output to.
	 *
	 * @param format
	 *               The format string to use.
	 *
	 * @param params
	 *               The parameters for the string.
	 * 
	 * @throws IOException
	 *                     If something I/O related goes wrong.
	 */
	public void formatString(Writer target, String format, Iterable<Object> params)
			throws IOException {
		ReportWriter rw = new ReportWriter(target);

		/* Put the parameters where we can easily handle them. */
		Tape<Object> tParams = new SingleTape<>(params);

		doFormatString(format, rw, tParams, true);
	}

	/**
	 * Fill in a partially started format string.
	 *
	 * Used mostly for directives that require formatting again with a different
	 * string.
	 *
	 * @param format
	 *                   The format to use.
	 *
	 * @param rw
	 *                   The buffer to file output into.
	 *
	 * @param tParams
	 *                   The parameters to use.
	 *
	 * @param isToplevel
	 *                   Whether or not this is a top-level format
	 *
	 * @throws IOException
	 *                     If something goes wrong
	 */
	public void doFormatString(String format, ReportWriter rw, Tape<Object> tParams,
			boolean isToplevel) throws IOException {
		CLTokenizer cltok = new CLTokenizer(format);

		doFormatString(cltok, rw, tParams, isToplevel);
	}

	/**
	 * Fill in a partially started format string.
	 *
	 * Used mostly for directives that require formatting again with a different
	 * string.
	 *
	 * @param cltok
	 *                   The place to get tokens from.
	 *
	 * @param rw
	 *                   The buffer to file output into.
	 *
	 * @param tParams
	 *                   The parameters to use.
	 *
	 * @param isToplevel
	 *                   Whether or not this is a top-level format
	 *
	 * @throws IOException
	 *                     If something goes wrong
	 */
	public void doFormatString(CLTokenizer cltok, ReportWriter rw, Tape<Object> tParams,
			boolean isToplevel) throws IOException {
		try {
			while (cltok.hasNext()) {
				SimpleDecree decr = cltok.next();

				if (decr.isLiteral) {
					rw.write(decr.name);
				} else if (decr.isUserCall) {
					/*
					 * @TODO implement user-called functions.
					 */
				} else if (extraDirectives.containsKey(decr.name)) {
					FormatParameters params
							= new FormatParameters(rw, tParams.item(), decr,
									tParams, cltok, this);

					extraDirectives.get(decr.name).format(params);
				} else if (builtinDirectives.containsKey(decr.name)) {
					FormatParameters params
							= new FormatParameters(rw, tParams.item(), decr,
									tParams, cltok, this);

					builtinDirectives.get(decr.name).format(params);
				} else {
					// All of these conditions are an error in some way
					if (decr.name == null) decr.name = "<null>";
	
					switch (decr.name) {
					case "]":
						throw new IllegalArgumentException(
								"Found conditional-end outside of conditional.");
					case ";":
						throw new IllegalArgumentException(
								"Found seperator outside of block.");
					case "}":
						throw new IllegalArgumentException(
								"Found iteration-end outside of iteration");
					case ")":
						throw new IllegalArgumentException(
								"Case-conversion end outside of case conversion");
					case "`]":
						throw new IllegalArgumentException(
								"Inflection-end outside of inflection");
					case "<":
					case ">":
						throw new IllegalArgumentException(
								"Inflection marker outside of inflection");
					case "`<":
					case "`>":
						throw new IllegalArgumentException(
								"Layout-control directives aren't implemented yet.");
					case "F":
					case "E":
					case "G":
					case "$":
						/*
						 * @TODO
						 *
						 * implement floating point directives.
						 */
						throw new IllegalArgumentException(
								"For now, floating point directives are implemented via the `D directive. Use that instead");
					case "W":
						/*
						 * @TODO
						 *
						 * figure out if we want to implement someting for these directives
						 * instead of punting.
						 */
						throw new IllegalArgumentException(
								"S and W aren't implemented. Use A instead");
					case "P":
						throw new IllegalArgumentException(
								"These directives aren't implemented yet");
					case "\n":
						/*
						 * Ignored newline.
						 */
						break;
					default:
						String msg
								= String.format("Unknown format directive '%s'", decr.name);
						throw new IllegalArgumentException(msg);
					}
				}
			}
		} catch (DirectiveEscape eex) {
			if (!isToplevel) throw eex;
		}
	}

	/**
	 * Compile a CLString from a string.
	 *
	 * @param inp
	 *            The string to compile.
	 *
	 * @return A CLString compiled from the input.
	 */
	public CLString compile(String inp) {
		CLTokenizer tokenzer = new CLTokenizer(inp);

		List<Edict> edts = compile(tokenzer);

		return new CLString(edts);
	}

	/**
	 * Compile a set of edicts from a list of decrees.
	 *
	 * @param decrees
	 *                The decrees to compile.
	 *
	 * @return A set of edicts compiled from the decrees.
	 */
	public List<Edict> compile(Iterable<SimpleDecree> decrees) {
		// If we have no decrees, there are no edicts.
		if (decrees == null) return new ArrayList<>();

		CLTokenizer it = CLTokenizer.fromTokens(decrees);
		return compile(it);
	}

	/**
	 * Compile a set of edicts from a clause.
	 *
	 * @param clause
	 *               The clause to compile.
	 *
	 * @return The set of edicts compiled from the clause.
	 */
	public List<Edict> compile(ClauseDecree clause) {
		if (clause == null) return new ArrayList<>();
		else                return compile(clause.body);
	}

	/**
	 * Compile a set of edicts from a set of tokens.
	 *
	 * @param cltok
	 *              The tokenizer providing us with our tokens.
	 *
	 * @return The edicts compiled from those tokens.
	 */
	public List<Edict> compile(CLTokenizer cltok) {
		List<Edict> result = new ArrayList<>();

		while (cltok.hasNext()) {
			SimpleDecree decr = cltok.next();
			String nam = decr.name;

			CompileContext compCTX = new CompileContext(cltok, this, decr);

			if (decr.isLiteral) {
				result.add(new StringEdict(decr.name));
			} else if (decr.isUserCall) {
				/*
				 * @TODO implement user-called functions.
				 */
				throw new IllegalArgumentException(
						"User-called functions have not yet been implemented");
			} else if (extraDirectives.containsKey(nam)) {
				Edict edt = extraDirectives.get(nam).compile(compCTX);

				result.add(edt);
			} else if (builtinDirectives.containsKey(nam)) {
				Edict edt = builtinDirectives.get(nam).compile(compCTX);

				result.add(edt);
			} else {
				// All of these conditions are an error in some way
				if (nam == null) nam = "<null>";
	
				switch (nam) {
				case "]":
					throw new IllegalArgumentException(
							"Found conditional-end outside of conditional.");
				case ";":
					throw new IllegalArgumentException("Found seperator outside of block.");
				case "}":
					throw new IllegalArgumentException(
							"Found iteration-end outside of iteration");
				case ")":
					throw new IllegalArgumentException(
							"Case-conversion end outside of case conversion");
				case "`]":
					throw new IllegalArgumentException(
							"Inflection-end outside of inflection");
				case "<":
				case ">":
					throw new IllegalArgumentException(
							"Inflection marker outside of inflection");
				case "`<":
				case "`>":
					throw new IllegalArgumentException(
							"Layout-control directives aren't implemented yet.");
				case "F":
				case "E":
				case "G":
				case "$":
					/*
					 * @TODO
					 *
					 * implement floating point directives.
					 */
					throw new IllegalArgumentException(
							"Floating-point directives aren't implemented yet.");
				case "W":
					/*
					 * @TODO
					 *
					 * figure out if we want to implement someting for these directives
					 * instead of punting.
					 */
					throw new IllegalArgumentException(
							"S and W aren't implemented. Use A instead");
				case "P":
					throw new IllegalArgumentException(
							"These directives aren't implemented yet");
				case "\n":
					/*
					 * Ignored newline.
					 */
					break;
				default:
					String msg = String.format("Unknown format directive '%s'", nam);
					throw new IllegalArgumentException(msg);
				}
			}
		}

		return result;
	}
}
