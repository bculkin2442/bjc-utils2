package bjc.utils.esodata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.IMap;

/**
 * Represents a mapping from a set of strings to a mapping of all unambiguous
 * prefixes of their respective strings.
 *
 * This works the same as Ruby's Abbrev module.
 *
 * @author EVE
 */
public class AbbrevMap {
	/* All of the words we have abbreviations for. */
	private final Set<String> wrds;

	/* Maps abbreviations to their strings. */
	private IMap<String, String> abbrevMap;

	/* Counts how many times we've seen a substring. */
	private Set<String> seen;

	/* Maps ambiguous abbreviations to the strings they could be. */
	private SetMultimap<String, String> ambMap;

	/**
	 * Create a new abbreviation map.
	 *
	 * @param words
	 *        The initial set of words to put in the map.
	 */
	public AbbrevMap(final String... words) {
		wrds = new HashSet<>(Arrays.asList(words));

		recalculate();
	}

	/**
	 * Recalculate all the abbreviations in this map.
	 *
	 * This may be needed after certain operations to ensure that all of the
	 * results are correct.
	 */
	public void recalculate() {
		abbrevMap = new FunctionalMap<>();

		ambMap = HashMultimap.create();

		seen = new HashSet<>();

		for(final String word : wrds) {
			intAddWord(word);
		}
	}

	/**
	 * Adds words to the abbreviation map.
	 *
	 * @param words
	 *        The words to add to the abbreviation map.
	 */
	public void addWords(final String... words) {
		wrds.addAll(Arrays.asList(words));

		for(final String word : words) {
			intAddWord(word);
		}
	}

	/* Actually add abbreviations of a word. */
	private void intAddWord(final String word) {
		/* A word always abbreviates to itself. */
		abbrevMap.put(word, word);

		/* Skip blank words. */
		if(word.equals("")) return;

		/* Handle each possible abbreviation. */
		for(int i = word.length(); i > 0; i--) {
			final String subword = word.substring(0, i);

			if(seen.contains(subword)) {
				/*
				 * Remove a mapping if its ambiguous and not a
				 * whole word.
				 */
				if(abbrevMap.containsKey(subword) && !wrds.contains(subword)) {
					final String oldword = abbrevMap.remove(subword);

					ambMap.put(subword, oldword);
					ambMap.put(subword, word);
				} else if(!wrds.contains(subword)) {
					ambMap.put(subword, word);
				}
			} else {
				seen.add(subword);

				abbrevMap.put(subword, word);
			}
		}
	}

	/**
	 * Removes words from the abbreviation map.
	 *
	 * NOTE: There may be inconsistent behavior after removing a word from
	 * the map. Use {@link AbbrevMap#recalculate()} to fix it if it occurs.
	 *
	 * @param words
	 *        The words to remove.
	 */
	public void removeWords(final String... words) {
		wrds.removeAll(Arrays.asList(words));

		for(final String word : words) {
			intRemoveWord(word);
		}
	}

	/* Actually remove a word. */
	private void intRemoveWord(final String word) {
		/* Skip blank words. */
		if(word.equals("")) return;

		/* Handle each possible abbreviation. */
		for(int i = word.length(); i > 0; i--) {
			final String subword = word.substring(0, i);

			if(abbrevMap.containsKey(subword)) {
				abbrevMap.remove(subword);
			} else {
				ambMap.remove(subword, word);

				final Set<String> possWords = ambMap.get(subword);

				if(possWords.size() == 0) {
					seen.remove(subword);
				} else if(possWords.size() == 1) {
					/*
					 * An abbreviation went from ambiguous
					 * to non-ambiguous.
					 */
					final String newWord = possWords.iterator().next();

					abbrevMap.put(subword, newWord);
					ambMap.remove(subword, newWord);
				}
			}
		}
	}

	/**
	 * Convert an abbreviation into all the strings it could abbreviate
	 * into.
	 *
	 * @param abbrev
	 *        The abbreviation to convert.
	 *
	 * @return All the expansions for the provided abbreviation.
	 */
	public String[] deabbrev(final String abbrev) {
		if(abbrevMap.containsKey(abbrev)) {
			return new String[] {
					abbrevMap.get(abbrev)
			};
		}

		return ambMap.get(abbrev).toArray(new String[0]);
	}

	@Override
	public int hashCode() {
		final int prime = 31;

		int result = 1;
		result = prime * result + (wrds == null ? 0 : wrds.hashCode());

		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof AbbrevMap)) return false;

		final AbbrevMap other = (AbbrevMap) obj;

		if(wrds == null) {
			if(other.wrds != null) return false;
		} else if(!wrds.equals(other.wrds)) return false;

		return true;
	}

	@Override
	public String toString() {
		final String fmt = "AbbrevMap [wrds=%s, abbrevMap=%s, seen=%s, ambMap=%s]";

		return String.format(fmt, wrds, abbrevMap, seen, ambMap);
	}
}
