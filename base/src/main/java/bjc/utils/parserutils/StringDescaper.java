package bjc.utils.parserutils;

import static bjc.utils.misc.PropertyDB.getRegex;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <<<<<<< Updated upstream Customizable string escapes.
 *
 * @author Benjamin Culkin
 */
public class StringDescaper {
	/* The logger. */
	private Logger LOGGER = Logger.getLogger(StringDescaper.class.getName());

	/*
	 * Patterns and pattern parts.
	 */
	private static String rPossibleEscapeString = getRegex("possibleStringEscape");
	private static Pattern possibleEscapePatt = Pattern.compile(rPossibleEscapeString);

	private static String rShortEscape = getRegex("shortFormStringEscape");
	private static String rOctalEscape = getRegex("octalStringEscape");
	private static String rUnicodeEscape = getRegex("unicodeStringEscape");

	private String rEscapeString;
	private Pattern escapePatt;

	private Map<String, String> literalEscapes;
	private Map<Pattern, UnaryOperator<String>> specialEscapes;

	/**
	 * Create a new customizable string escape remover.
	 */
	public StringDescaper() {
		/* Setup the escape maps. */
		literalEscapes = new HashMap<>();
		specialEscapes = new HashMap<>();

		/* Set up the hard-coded escapes. */
		rEscapeString = String.format("\\\\(%1$s|%2$s|%3$s)", rShortEscape, rOctalEscape,
				rUnicodeEscape);
		escapePatt = Pattern.compile(rEscapeString);
	}

	/**
	 * Add a new literal escape.
	 *
	 * @param escape
	 *               The custom escape to add.
	 * @param val
	 *               The value for the escape.
	 */
	public void addLiteralEscape(String escape, String val) {
		if (literalEscapes.containsKey(escape)) {
			LOGGER.warning(String.format("Shadowing literal escape '%s'\n", escape));
		}

		literalEscapes.put(escape, val);
	}

	/**
	 * Create a new custom escape.
	 *
	 * @param escape
	 *               The escape to add.
	 * @param val
	 *               The implementation of the escape.
	 */
	public void addSpecialEscape(String escape, UnaryOperator<String> val) {
		/*
		 * Make sure this special escape is a valid regex.
		 */

		Pattern patt = null;
		try {
			patt = Pattern.compile(escape);
		} catch (PatternSyntaxException psex) {
			String msg = String.format("Invalid special escape '%s'", escape);

			IllegalArgumentException iaex = new IllegalArgumentException(msg);
			iaex.initCause(psex);

			throw psex;
		}

		if (specialEscapes.containsKey(patt)) {
			LOGGER.warning(String.format("Shadowing special escape '%s'\n", escape));
		}

		specialEscapes.put(patt, val);
	}

	/**
	 * Compile the escapes.
	 */
	public void compileEscapes() {
		StringBuilder work = new StringBuilder();

		for (String litEscape : literalEscapes.keySet()) {
			work.append("|(?:");
			work.append(Pattern.quote(litEscape));
			work.append(")");
		}

		for (Pattern specEscape : specialEscapes.keySet()) {
			work.append("|(?:");
			work.append(specEscape.toString());
			work.append(")");
		}

		/*
		 * Convert user-defined escapes to a regex for matching. We don't need a bar
		 * before %4 because the string has it.
		 */
		rEscapeString = String.format("\\(%1$s|%2$s|%3$s%4$s)", rShortEscape,
				rOctalEscape, rUnicodeEscape, work.toString());
		escapePatt = Pattern.compile(rEscapeString);
	}

	/**
	 * Replace escape characters with their actual equivalents.
	 *
	 * @param inp
	 *            The string to replace escape sequences in.
	 *
	 * @return The string with escape sequences replaced by their equivalent
	 *         characters.
	 */
	public String descapeString(final String inp) {
		if (inp == null) {
			throw new NullPointerException("Input to descapeString must not be null");
		}

		/*
		 * Prepare the buffer and escape finder.
		 */
		final StringBuffer work = new StringBuffer();
		final Matcher possibleEscapeFinder = possibleEscapePatt.matcher(inp);
		final Matcher escapeFinder = escapePatt.matcher(inp);

		/* Go through each escape. */
		while (possibleEscapeFinder.find()) {
			if (!escapeFinder.find()) {
				/*
				 * Found a possible escape that isn't actually an escape.
				 */
				final String msg = String.format(
						"Illegal escape sequence '%s' at position %d of string '%s'",
						possibleEscapeFinder.group(), possibleEscapeFinder.start(), inp);
				throw new IllegalArgumentException(msg);
			}

			final String escapeSeq = escapeFinder.group();

			/*
			 * Convert the escape to a string.
			 */
			String escapeRep = "";
			switch (escapeSeq) {
			case "\\b":
				escapeRep = "\b";
				break;
			case "\\t":
				escapeRep = "\t";
				break;
			case "\\n":
				escapeRep = "\n";
				break;
			case "\\f":
				escapeRep = "\f";
				break;
			case "\\r":
				escapeRep = "\r";
				break;
			case "\\\"":
				escapeRep = "\"";
				break;
			case "\\'":
				escapeRep = "'";
				break;
			case "\\\\":
				/*
				 * Skip past the second slash.
				 */
				possibleEscapeFinder.find();
				escapeRep = "\\";
				break;
			default:
				if (escapeSeq.startsWith("u")) {
					/* Handle a unicode escape. */
					escapeRep = handleUnicodeEscape(escapeSeq.substring(1));
				} else if (escapeSeq.startsWith("O")) {
					/* Handle an octal escape. */
					escapeRep = handleOctalEscape(escapeSeq.substring(1));
				} else if (literalEscapes.containsKey(escapeSeq)) {
					/* Handle a custom literal escape. */
					escapeRep = literalEscapes.get(escapeSeq);
				} else {
					/* Handle a custom special escape. */
					for (Entry<Pattern, UnaryOperator<String>> ent : specialEscapes
							.entrySet()) {
						Pattern pat = ent.getKey();

						Matcher mat = pat.matcher(escapeSeq);
						if (mat.matches()) {
							escapeRep = ent.getValue().apply(escapeSeq);
							break;
						}
					}
				}
			}

			escapeFinder.appendReplacement(work, escapeRep);
		}

		escapeFinder.appendTail(work);

		return work.toString();
	}

	/*
	 * Handle a unicode codepoint.
	 */
	private static String handleUnicodeEscape(final String seq) {
		try {
			final int codepoint = Integer.parseInt(seq, 16);

			return new String(Character.toChars(codepoint));
		} catch (final IllegalArgumentException iaex) {
			final String msg
					= String.format("'%s' is not a valid Unicode escape sequence'", seq);

			final IllegalArgumentException reiaex = new IllegalArgumentException(msg);

			reiaex.initCause(iaex);

			throw reiaex;
		}
	}

	/*
	 * Handle a octal codepoint.
	 */
	private static String handleOctalEscape(final String seq) {
		try {
			final int codepoint = Integer.parseInt(seq, 8);

			if (codepoint > 255) {
				final String msg = String
						.format("'%d' is outside the range of octal escapes'", codepoint);

				throw new IllegalArgumentException(msg);
			}

			return new String(Character.toChars(codepoint));
		} catch (final IllegalArgumentException iaex) {
			final String msg
					= String.format("'%s' is not a valid octal escape sequence'", seq);

			final IllegalArgumentException reiaex = new IllegalArgumentException(msg);

			reiaex.initCause(iaex);

			throw reiaex;
		}
	}
}
