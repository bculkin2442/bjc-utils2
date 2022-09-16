package bjc.utils.ioutils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Simple file based properties.
 *
 * @author EVE
 *
 */
public class SimpleProperties implements Map<String, String> {
	private final Map<String, String> props;

	/**
	 * Create a new set of simple properties.
	 */
	public SimpleProperties() {
		props = new HashMap<>();
	}

	/**
	 * Load properties from an input stream.
	 *
	 * Delegates to {@link SimpleProperties#loadFrom(Reader, boolean)} to allow you
	 * to pass a input stream instead of a reader.
	 *
	 * @param is
	 *                        The stream to read from.
	 * @param allowDuplicates
	 *                        Whether or not duplicate keys should be allowed.
	 */
	public void loadFrom(final InputStream is, final boolean allowDuplicates) {
		loadFrom(new InputStreamReader(is), allowDuplicates);
	}

	/**
	 * Load properties from the provided input reader.
	 *
	 * The format is the name, a space, then the body.
	 *
	 * All leading/trailing spaces from the name &amp; body are removed.
	 *
	 * @param rdr
	 *                        The reader to read from.
	 *
	 * @param allowDuplicates
	 *                        Whether or not duplicate keys should be allowed. If
	 *                        they are, the end-value of the property will be what
	 *                        the last one was.
	 *
	 * @throws DuplicateKeys
	 *                       If duplicate keys have been found when they are
	 *                       prohibited.
	 */
	public void loadFrom(final Reader rdr, final boolean allowDuplicates) {
		try (Scanner scn = new Scanner(rdr)) {
			while (scn.hasNextLine()) {
				final String ln = scn.nextLine().trim();

				/*
				 * Skip blank lines/comments
				 */
				if (ln.equals("") || ln.startsWith("#")) {
					continue;
				}

				final int sepIdx = ln.indexOf(' ');

				/*
				 * Complain about improperly formatted lines.
				 */
				if (sepIdx == -1) {
					throw new InvalidLineFormat(ln);
				}

				final String name = ln.substring(0, sepIdx).trim();
				final String body = ln.substring(sepIdx).trim();

				/*
				 * Complain about duplicates, if that is wanted.
				 */
				if (!allowDuplicates && containsKey(name)) {
					throw new DuplicateKeys(name);
				}

				put(name, body);
			}
		}
	}

	/**
	 * Output the set of read properties.
	 *
	 * Uses System.out, since one isn't specified.
	 */
	public void outputProperties() {
		outputProperties(System.out);
	}

	/**
	 * Output the set of read properties.
	 *
	 * @param strim
	 *              The stream to output properties to.
	 */
	public void outputProperties(PrintStream strim) {
		strim.println("Read properties:");

		for (final Entry<String, String> entry : entrySet()) {
			strim.printf("\t'%s'\t'%s'\n", entry.getKey(), entry.getValue());
		}

		strim.println();
	}

	@Override
	public int size() {
		return props.size();
	}

	@Override
	public boolean isEmpty() {
		return props.isEmpty();
	}

	@Override
	public boolean containsKey(final Object key) {
		return props.containsKey(key);
	}

	@Override
	public boolean containsValue(final Object value) {
		return props.containsValue(value);
	}

	@Override
	public String get(final Object key) {
		return props.get(key);
	}

	@Override
	public String put(final String key, final String value) {
		return props.put(key, value);
	}

	@Override
	public String remove(final Object key) {
		return props.remove(key);
	}

	@Override
	public void putAll(final Map<? extends String, ? extends String> m) {
		props.putAll(m);
	}

	@Override
	public void clear() {
		props.clear();
	}

	@Override
	public Set<String> keySet() {
		return props.keySet();
	}

	@Override
	public Collection<String> values() {
		return props.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return props.entrySet();
	}

	@Override
	public boolean equals(final Object o) {
		return props.equals(o);
	}

	@Override
	public int hashCode() {
		return props.hashCode();
	}
}
