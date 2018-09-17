package bjc.utils.funcutils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetUtils {
	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
		Set<Set<T>> sets = new HashSet<Set<T>>();

		if (originalSet.isEmpty()) {
			sets.add(new HashSet<T>());
			return sets;
		}

		List<T> list = new ArrayList<T>(originalSet);

		T head = list.get(0);

		Set<T> rest = new HashSet<T>(list.subList(1, list.size()));

		for (Set<T> set : powerSet(rest)) {
			Set<T> newSet = new HashSet<T>();

			newSet.add(head);
			newSet.addAll(set);

			sets.add(newSet);
			sets.add(set);
		}

		return sets;
	}

	public static <T> Set<T> toSet(T... elms) {
		Set<T> set = new HashSet<>();

		for(T elm : elms) {
			set.add(elm);
		}

		return set;
	}
}