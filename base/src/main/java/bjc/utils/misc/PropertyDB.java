package bjc.utils.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import bjc.utils.funcutils.LambdaLock;
import bjc.utils.ioutils.SimpleProperties;

/**
 * Database for storage of properties from external files.
 *
 * @author EVE
 *
 */
public class PropertyDB {
	/* Regex storage. */
	private static SimpleProperties regexes;
	private static Map<String, Pattern> compiledRegexes;

	/* Format string storage. */
	private static SimpleProperties formats;

	/*
	 * Whether or not to log during the loading.
	 */
	private static final boolean LOGLOAD = false;

	/*
	 * The lock to use to ensure a read can't happen during a reload
	 */
	private static LambdaLock loadLock = new LambdaLock();

	static {
		/* Reload properties on class load. */
		reloadProperties();
	}

	/**
	 * Reload all the properties from their files.
	 *
	 * NOTE: Any attempts to read from the property DB while properties are being
	 * loaded will block, to prevent reads from partial states.
	 */
	public static void reloadProperties() {
		/* * Do the load with the write lock taken. */
		loadLock.write(() -> {
			if (LOGLOAD) {
				System.out.println("Reading regex properties:");
			}

			/* * Load regexes. */
			regexes = new SimpleProperties();
			regexes.loadFrom(PropertyDB.class.getResourceAsStream("/regexes.sprop"),
					false);
			if (LOGLOAD) {
				regexes.outputProperties(System.out);
				System.out.println();
			}
			compiledRegexes = new HashMap<>();

			if (LOGLOAD) {
				System.out.println("Reading format properties:");
			}

			/* * Load formats. */
			formats = new SimpleProperties();
			formats.loadFrom(PropertyDB.class.getResourceAsStream("/formats.sprop"),
					false);
			if (LOGLOAD) {
				formats.outputProperties(System.out);
				System.out.println();
			}
		});
	}

	/**
	 * Retrieve a persisted regular expression.
	 *
	 * @param key
	 *            The name of the regular expression.
	 *
	 * @return The regular expression with that name.
	 */
	public static String getRegex(final String key) {
		return loadLock.read(() -> {
			if (!regexes.containsKey(key)) {
				final String msg
						= String.format("No regular expression named '%s' found", key);

				throw new NoSuchElementException(msg);
			}

			return regexes.get(key);
		});
	}

	/**
	 * Retrieve a persisted regular expression, compiled into a regular expression.
	 *
	 * @param key
	 *            The name of the regular expression.
	 *
	 * @return The regular expression with that name.
	 */
	public static Pattern getCompiledRegex(final String key) {
		return loadLock.read(() -> {
			if (!regexes.containsKey(key)) {
				final String msg
						= String.format("No regular expression named '%s' found", key);

				throw new NoSuchElementException(msg);
			}

			/* * Get the regex, and cache a compiled version. */
			return compiledRegexes.computeIfAbsent(key, strang -> Pattern.compile(regexes.get(strang)));
		});
	}

	/**
	 * Retrieve a persisted format string.
	 *
	 * @param key
	 *            The name of the format string.
	 *
	 * @return The format string with that name.
	 */
	public static String getFormat(final String key) {
		return loadLock.read(() -> {
			if (!formats.containsKey(key)) {
				final String msg
						= String.format("No format string named '%s' found", key);

				throw new NoSuchElementException(msg);
			}

			return formats.get(key);
		});
	}

	/**
	 * Retrieve a persisted format string, and apply it to a set of arguments.
	 *
	 * @param key
	 *                The name of the format string.
	 *
	 * @param objects
	 *                The parameters to the format string.
	 *
	 * @return The format string with that name.
	 */
	public static String applyFormat(final String key, final Object... objects) {
		return String.format(getFormat(key), objects);
	}
}
