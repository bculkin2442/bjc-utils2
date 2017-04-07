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
	private Map<String, String> props;

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
	 * All leading/trailing spaces from the name & body are removed.
	 * 
	 * @param is
	 *            The stream to read from.
	 * 
	 * @param allowDuplicates
	 *            Whether or not duplicate keys should be allowed.
	 */
	public void loadFrom(InputStream is, boolean allowDuplicates) {
		try (Scanner scn = new Scanner(is)) {
			while (scn.hasNextLine()) {
				String ln = scn.nextLine().trim();

				/*
				 * Skip blank lines/comments
				 */
				if (ln.equals(""))
					continue;
				if (ln.startsWith("#"))
					continue;

				int sepIdx = ln.indexOf(' ');

				if (sepIdx == -1) {
					String fmt = "Properties must be a name, a space, then the body.\n\tOffending line is '%s'";
					String msg = String.format(fmt, ln);

					throw new NoSuchElementException(msg);
				}

				String name = ln.substring(0, sepIdx).trim();
				String body = ln.substring(sepIdx).trim();

				if (!allowDuplicates && containsKey(name)) {
					String msg = String.format("Duplicate key '%s'", name);

					throw new IllegalStateException(msg);
				}

				put(name, body);
			}
		}

		System.out.println("Read properties:");
		for (Entry<String, String> entry : entrySet()) {
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

	@Override
	public boolean containsKey(Object key) {
		return props.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return props.containsValue(value);
	}

	@Override
	public String get(Object key) {
		return props.get(key);
	}

	@Override
	public String put(String key, String value) {
		return props.put(key, value);
	}

	@Override
	public String remove(Object key) {
		return props.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
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
	public boolean equals(Object o) {
		return props.equals(o);
	}

	@Override
	public int hashCode() {
		return props.hashCode();
	}
}
