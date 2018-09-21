package bjc.utils.ioutils;

import java.io.InputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// @NOTE 9/19/18
//
// What to do about one-to-many mirrored mappings?
//
// Currently, we pick the one with the latest codepoint
public class MirrorDB {
	private Map<String, String> mirrored;

	public MirrorDB() {
		mirrored = new HashMap<>();

		final InputStream stream = getClass().getResourceAsStream("/BidiMirrorDB.txt");

		try (Scanner scn = new Scanner(stream)) {
			String ln = "";

			while (scn.hasNextLine()) {
				ln = scn.nextLine();

				if (ln.equals("")) {
					continue;
				}
				if (ln.startsWith("#")) {
					continue;
				}

				final int cp1 = Integer.parseInt(ln.substring(0, 4), 16);
				final int cp2 = Integer.parseInt(ln.substring(6, 10), 16);

				final char[] cpa1 = Character.toChars(cp1);
				final char[] cpa2 = Character.toChars(cp2);

				final String cps1 = new String(cpa1);
				final String cps2 = new String(cpa2);

				mirrored.put(cps1, cps2);
			}
		}
	}

	public boolean canMirror(String mir) {
		return mirrored.containsKey(mir);
	}

	public String mirror(String mir) {
		return mirrored.get(mir);
	}
}
