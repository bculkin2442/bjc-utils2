package bjc.utils.ioutils;

import bjc.utils.PropertyDB;

import static bjc.utils.PropertyDB.applyFormat;
import static bjc.utils.PropertyDB.getCompiledRegex;
import static bjc.utils.PropertyDB.getRegex;

import java.util.regex.Pattern;

public class CLFormatter {
	private static final String  prefixParam  = getRegex("clFormatPrefix");
	private static final Pattern pPrefixParam = Pattern.compile(prefixParam);
}
