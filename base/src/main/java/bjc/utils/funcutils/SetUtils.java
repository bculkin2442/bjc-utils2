package bjc.utils.funcutils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Various utility functions dealing with sets.
 * 
 * @author bjculkin
 *
 */
public class SetUtils {
	/**
	 * Create a power-set (set of all subsets) of a given set.
	 * 
	 * @param originalSet
	 *                    The set to create a power-set of.
	 * @return The power-set of the set.
	 */
	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
		Set<Set<T>> sets = new HashSet<>();

		// Special-case empty input
		if (originalSet.isEmpty()) {
			sets.add(new HashSet<T>());
			return sets;
		}

		List<T> list = new ArrayList<>(originalSet);

		// Add original set to list.
		T head = list.get(0);

		// Trim leading element from set.
		Set<T> rest = new HashSet<>(list.subList(1, list.size()));

		Set<Set<T>> remSets = powerSet(rest);

		for (Set<T> set : remSets) {
			Set<T> newSet = new HashSet<>();

			// Create a new set with the removed element.
			newSet.add(head);
			newSet.addAll(set);

			sets.add(newSet);
			sets.add(set);
		}

		return sets;
	}

	/**
	 * Utility method for set construction.
	 * 
	 * @param elms
	 *             The elements to stick in the set.
	 * @return A set containing the specified elements.
	 */
	@SafeVarargs
	public static <T> Set<T> toSet(T... elms) {
		Set<T> set = new HashSet<>();

		for (T elm : elms) set.add(elm);

		return set;
	}
}
