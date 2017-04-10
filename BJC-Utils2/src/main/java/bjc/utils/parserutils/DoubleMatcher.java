package bjc.utils.parserutils;

import static bjc.utils.PropertyDB.applyFormat;
import static bjc.utils.PropertyDB.getRegex;

import java.util.regex.Pattern;

/*
 * Checks if a string would pass Double.parseDouble.
 *
 * Uses a regex from the javadoc for Double.valueOf()
 */
class DoubleMatcher {
	/*
	 * Unit pieces.
	 */
	private static final String	rDecDigits	= getRegex("fpDigits");
	private static final String	rHexDigits	= getRegex("fpHexDigits");
	private static final String	rExponent	= applyFormat("fpExponent", getRegex("fpExponent"), rDecDigits);

	/*
	 * Decimal floating point numbers.
	 */
	private static final String	rSimpleDec	= applyFormat("fpDecimalDecimal", rDecDigits, rExponent);
	private static final String	rSimpleIntDec	= applyFormat("fpDecimalInteger", rDecDigits, rExponent);

	/*
	 * Hex floating point numbers.
	 */
	private static final String	rHexInt		= applyFormat("fpHexInteger", rHexDigits);
	private static final String	rHexDec		= applyFormat("fpHexDecimal", rHexDigits);
	private static final String	rHexLead	= applyFormat("fpHexLeader", rHexInt, rHexDec);
	private static final String	rHexString	= applyFormat("fpHexString", rHexLead, rDecDigits);

	/*
	 * Floating point components.
	 */
	private static final String	rFPLeader	= getRegex("fpLeader");
	private static final String	rFPNum		= applyFormat("fpNumber", rSimpleIntDec, rSimpleDec,
			rHexString);

	private static final String	rDouble		= applyFormat("fpDouble", rFPLeader, rFPNum);
	public static final Pattern	doubleLiteral	= Pattern.compile("\\A" + rDouble + "\\Z");
}