package bjc.utils.ioutils;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
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
	 * Load properties from the provided input stream.
	 *
	 * The format is the name, a space, then the body.
	 *
	 * All leading/trailing spaces from the name &amp; body are removed.
	 *
	 * @param is
	 *        The stream to read from.
	 *
	 * @param allowDuplicates
	 *        Whether or not duplicate keys should be allowed.
	 */
	public void loadFrom(final InputStream is, final boolean allowDuplicates) {
		try(Scanner scn = new Scanner(is)) {
			while(scn.hasNextLine()) {
				final String ln = scn.nextLine().trim();

				/*
				 * Skip blank lines/comments
				 */
				if(ln.equals("") || ln.startsWith("#")) {
					continue;
				}

				final int sepIdx = ln.indexOf(' ');

				/*
				 * Complain about improperly formatted lines.
				 */
				if(sepIdx == -1) {
					final String fmt = "Properties must be a name, a space, then the body.\n\tOffending line is '%s'";
					final String msg = String.format(fmt, ln);

					throw new NoSuchElementException(msg);
				}

				final String name = ln.substring(0, sepIdx).trim();
				final String body = ln.substring(sepIdx).trim();

				/*
				 * Complain about duplicates, if that is wanted.
				 */
				if(!allowDuplicates && containsKey(name)) {
					final String msg = String.format("Duplicate key '%s'", name);

					throw new IllegalStateException(msg);
				}

				put(name, body);
			}
		}
	}

	/**
	 * Output the set of read properties.
	 */
	public void outputProperties() {
		System.out.println("Read properties:");

		for(final Entry<String, String> entry : entrySet()) {
			System.out.printf("\t'%s'\t'%s'\n", entry.getKey(), entry.getValue());
		}

		System.out.println();
	}

	@Override
	public int size() {
		return props.size();
	}

	@Override
	public boolean isEmpty() {
		return props.isEmpty();
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public boolean containsKey(final Object key) {
		return props.containsKey(key);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public boolean containsValue(final Object value) {
		return props.containsValue(value);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public String get(final Object key) {
		return props.get(key);
	}

	@Override
	public String put(final String key, final String value) {
		return props.put(key, value);
	}

	@SuppressWarnings("unlikely-arg-type")
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
