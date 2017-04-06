package bjc.utils.parserutils;

import java.util.regex.Pattern;

import static bjc.utils.PropertyDB.*;

/*
 * Checks if a string would pass Double.parseDouble.
 *
 * Uses a regex from the javadoc for Double.valueOf()
 */
class DoubleMatcher {
	/*
	 * Unit pieces.
	 */
	private static final String	DecDigits	= getRegex("fpDigits");
	private static final String	HexDigits	= getRegex("fpHexDigits");
	private static final String	Exponent	= applyFormat("fpExponent", getRegex("fpExponent"), DecDigits);

	/*
	 * Decimal floating point numbers.
	 */
	private static final String	SIMPLE_DEC	= applyFormat("fpDecimalDecimal", DecDigits, Exponent);
	private static final String	SIMPLE_INTDEC	= applyFormat("fpDecimalInteger", DecDigits, Exponent);

	/*
	 * Hex floating point numbers.
	 */
	private static final String	HEX_INT		= applyFormat("fpHexInteger", HexDigits);
	private static final String	HEX_DEC		= applyFormat("fpHexDecimal", HexDigits);
	private static final String	HEX_LEAD	= applyFormat("fpHexLeader", HEX_INT, HEX_DEC);
	private static final String	HEX_STRING	= applyFormat("fpHexString", HEX_LEAD, DecDigits);

	/*
	 * Floating point components.
	 */
	private static final String	FP_LEADER	= getRegex("fpLeader");
	private static final String	FP_NUM		= applyFormat("fpNumber", SIMPLE_INTDEC, SIMPLE_DEC,
			HEX_STRING);

	private static final String	fpRegex		= applyFormat("fpDouble", FP_LEADER, FP_NUM);
	public static final Pattern	floatingLiteral	= Pattern.compile("\\A" + fpRegex + "\\Z");
}