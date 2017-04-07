package bjc.utils;

import bjc.utils.funcutils.LambdaLock;
import bjc.utils.ioutils.SimpleProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

/**
 * Database for storage of properties from external files.
 * 
 * @author EVE
 *
 */
public class PropertyDB {
	private static SimpleProperties		regexes;
	private static Map<String, Pattern>	compiledRegexes;

	private static SimpleProperties formats;

	private static final boolean LOGLOAD = false;

	/*
	 * The lock to use to ensure a read can't happen during a reload
	 */
	private static LambdaLock loadLock = new LambdaLock();

	static {
		reloadProperties();
	}

	/**
	 * Reload all the properties from their files.
	 * 
	 * NOTE: Any attempts to read from the property DB while properties are
	 * being loaded will block, to prevent reads from partial states.
	 */
	public static void reloadProperties() {
		loadLock.write(() -> {
			if(LOGLOAD) {
				System.out.println("Reading regex properties:");
			}
			regexes = new SimpleProperties();
			regexes.loadFrom(PropertyDB.class.getResourceAsStream("/regexes.sprop"), false);
			if(LOGLOAD) {
				regexes.outputProperties();
				System.out.println();
			}

			compiledRegexes = new HashMap<>();

			if(LOGLOAD) {
				System.out.println("Reading format properties:");
			}
			formats = new SimpleProperties();
			formats.loadFrom(PropertyDB.class.getResourceAsStream("/formats.sprop"), false);
			if(LOGLOAD) {
				formats.outputProperties();
				System.out.println();
			}
		});
	}

	/**
	 * Retrieve a persisted regular expression.
	 * 
	 * @param key
	 *                The name of the regular expression.
	 * 
	 * @return The regular expression with that name.
	 */
	public static String getRegex(String key) {
		return loadLock.read(() -> {
			if(!regexes.containsKey(key)) {
				String msg = String.format("No regular expression named '%s' found", key);

				throw new NoSuchElementException(msg);
			}

			return regexes.get(key);
		});
	}

	/**
	 * Retrieve a persisted regular expression, compiled into a regular
	 * expression.
	 * 
	 * @param key
	 *                The name of the regular expression.
	 * 
	 * @return The regular expression with that name.
	 */
	public static Pattern getCompiledRegex(String key) {
		return loadLock.read(() -> {
			if(!regexes.containsKey(key)) {
				String msg = String.format("No regular expression named '%s' found", key);

				throw new NoSuchElementException(msg);
			}

			return compiledRegexes.computeIfAbsent(key, strang -> {
				return Pattern.compile(regexes.get(strang));
			});
		});
	}

	/**
	 * Retrieve a persisted format string.
	 * 
	 * @param key
	 *                The name of the format string.
	 * 
	 * @return The format string with that name.
	 */
	public static String getFormat(String key) {
		return loadLock.read(() -> {
			if(!formats.containsKey(key)) {
				String msg = String.format("No format string named '%s' found", key);

				throw new NoSuchElementException(msg);
			}

			return formats.get(key);
		});
	}

	/**
	 * Retrieve a persisted format string, and apply it to a set of
	 * arguments.
	 * 
	 * @param key
	 *                The name of the format string.
	 * 
	 * @param objects
	 *                The parameters to the format string.
	 * 
	 * @return The format string with that name.
	 */
	public static String applyFormat(String key, Object... objects) {
		return String.format(getFormat(key), objects);
	}
}