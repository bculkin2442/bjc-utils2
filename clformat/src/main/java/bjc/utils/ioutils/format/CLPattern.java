package bjc.utils.ioutils.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bjc.utils.ioutils.SimpleProperties;

/**
 * Utility class for reading in the pattern for parsing format directives.
 *
 * @author bjculkin
 *
 */
public class CLPattern {
	private static String prefixParam;
	private static String formatMod;
	private static String directiveName;

	private static String prefixList;
	private static String formatDirective;

	private static Pattern pFormatDirective;

	static {
		SimpleProperties props = new SimpleProperties();

		try (InputStream is = CLFormatter.class.getResourceAsStream("/clformat.sprop")) {
			props.loadFrom(is, false);
		} catch (IOException ioex) {
			// WELP, we failed. Bail
			throw new RuntimeException("Couldn't load formats for formatter");
		}

		String seqPrefixParam = props.get("clFormatPrefixParam");

		prefixParam = String.format(props.get("clFormatPrefix"), seqPrefixParam);
		formatMod = props.get("clFormatModifier");
		directiveName = props.get("clFormatName");

		prefixList = String.format(props.get("delimSeparatedList"), prefixParam, ",");
		formatDirective = String.format(props.get("clFormatDirective"), prefixList,
				formatMod, directiveName);

		pFormatDirective = Pattern.compile(formatDirective);
	}

	/**
	 * Get a matcher for FORMAT directives.
	 *
	 * @param inp
	 *            The string to parse directives from.
	 */
	public static Matcher getDirectiveMatcher(String inp) {
		return pFormatDirective.matcher(inp);
	}
}
