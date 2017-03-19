package bjc.utils.parserutils;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a possible delimiter group to match.
 * 
 * @author EVE
 *
 * @param <T>
 *                The type of items in the sequence.
 */
public class DelimiterGroup<T> {
	/**
	 * Represents an instance of a delimiter group.
	 * 
	 * @author EVE
	 *
	 */
	public class OpenGroup {
		private Deque<ITree<T>> contents;

		private IList<ITree<T>> currentGroup;

		private T opener;

		/**
		 * Create a new instance of a delimiter group.
		 * 
		 * @param open
		 *                The item that opened this group.
		 */
		public OpenGroup(T open) {
			opener = open;

			contents = new LinkedList<>();

			currentGroup = new FunctionalList<>();
		}

		/**
		 * Add an item to this group instance.
		 * 
		 * @param itm
		 *                The item to add to this group instance.
		 */
		public void addItem(ITree<T> itm) {
			currentGroup.add(itm);
		}

		/**
		 * Mark a subgroup.
		 * 
		 * @param marker
		 *                The item that indicated this subgroup.
		 * 
		 * @param chars
		 *                The characteristics for building the tree.
		 */
		public void markSubgroup(T marker, SequenceCharacteristics<T> chars) {
			ITree<T> subgroupContents = new Tree<>(chars.contents);
			for(ITree<T> itm : currentGroup) {
				subgroupContents.addChild(itm);
			}

			while(!contents.isEmpty()) {
				ITree<T> possibleSubordinate = contents.peek();

				if(possibleSubordinate.getHead().equals(chars.subgroup)) {
					T otherMarker = possibleSubordinate.getChild(1).getHead();

					if(subgroups.get(marker) > subgroups.get(otherMarker)) {
						subgroupContents.prependChild(contents.pop());
					} else {
						break;
					}
				} else {
					subgroupContents.prependChild(contents.pop());
				}
			}

			Tree<T> subgroup = new Tree<>(chars.subgroup, subgroupContents, new Tree<>(marker));

			//System.out.println("\tTRACE: generated subgroup\n" + subgroup + "\n\n");
			contents.push(subgroup);

			currentGroup = new FunctionalList<>();
		}

		/**
		 * Convert this group into a tree.
		 * 
		 * @param closer
		 *                The item that closed this group.
		 * 
		 * @param chars
		 *                The characteristics for building the tree.
		 * 
		 * @return This group as a tree.
		 */
		public ITree<T> toTree(T closer, SequenceCharacteristics<T> chars) {
			ITree<T> res = new Tree<>(chars.contents);

			if(contents.isEmpty()) {
				currentGroup.forEach(res::addChild);
			} else {
				while(!contents.isEmpty()) {
					res.prependChild(contents.poll());
				}

				currentGroup.forEach(res::addChild);
			}

			return new Tree<>(groupName, new Tree<>(opener), res, new Tree<>(closer));
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();

			builder.append("OpenGroup [contents=");
			builder.append(contents);
			builder.append(", currentGroup=");
			builder.append(currentGroup);
			builder.append(", opener=");
			builder.append(opener);
			builder.append("]");

			return builder.toString();
		}

		/**
		 * Check if a group is excluded at the top level of this group.
		 * 
		 * @param groupName
		 *                The group to check.
		 * 
		 * @return Whether or not the provided group is excluded.
		 */
		public boolean excludes(T groupName) {
			return topLevelExclusions.contains(groupName);
		}

		/**
		 * Check if the provided delimiter would close this group.
		 * 
		 * @param del
		 *                The string to check as a closing delimiter.
		 * 
		 * @return Whether or not the provided delimiter closes this
		 *         group.
		 */
		public boolean isClosing(T del) {
			return closingDelimiters.contains(del);
		}

		/**
		 * Get the name of the group this is an instance of.
		 * 
		 * @return The name of the group this is an instance of.
		 */
		public T getName() {
			return groupName;
		}

		/**
		 * Get the groups that aren't allowed at all in this group.
		 * 
		 * @return The groups that aren't allowed at all in this group.
		 */
		public Set<T> getNestingExclusions() {
			return groupExclusions;
		}

		/**
		 * Checks if a given token marks a subgroup.
		 * 
		 * @param tok
		 *                The token to check.
		 * 
		 * @return Whether or not the token marks a subgroup.
		 */
		public boolean marksSubgroup(T tok) {
			return subgroups.containsKey(tok);
		}
	}

	/**
	 * The name of this delimiter group.
	 */
	public final T groupName;

	/*
	 * The delimiters that close this group.
	 */
	private Set<T> closingDelimiters;

	/*
	 * The groups that can't occur in the top level of this group.
	 */
	private Set<T> topLevelExclusions;

	/*
	 * The groups that can't occur anywhere inside this group.
	 */
	private Set<T> groupExclusions;

	/*
	 * Mapping from sub-group delimiters, to any sub-groups enclosed in
	 * them.
	 */
	private Map<T, Integer> subgroups;

	/**
	 * Create a new empty delimiter group.
	 * 
	 * @param name
	 *                The name of the delimiter group
	 */
	public DelimiterGroup(T name) {
		if(name == null) throw new NullPointerException("Group name must not be null");

		groupName = name;

		closingDelimiters = new HashSet<>();
		topLevelExclusions = new HashSet<>();
		groupExclusions = new HashSet<>();
		subgroups = new HashMap<>();
	}

	/**
	 * Adds one or more delimiters that close this group.
	 * 
	 * @param closers
	 *                Delimiters that close this group.
	 */
	@SafeVarargs
	public final void addClosing(T... closers) {
		List<T> closerList = Arrays.asList(closers);

		for(T closer : closerList) {
			if(closer == null) {
				throw new NullPointerException("Closing delimiter must not be null");
			} else if(closer.equals("")) {
				/*
				 * We can do this because equals works on
				 * arbitrary objects, not just those of the same
				 * type.
				 */
				throw new IllegalArgumentException("Empty string is not a valid exclusion");
			} else {
				closingDelimiters.add(closer);
			}
		}
	}

	/**
	 * Adds one or more groups that cannot occur in the top level of this
	 * group.
	 * 
	 * @param exclusions
	 *                The groups forbidden in the top level of this group.
	 */
	@SafeVarargs
	public final void addTopLevelForbid(T... exclusions) {
		for(T exclusion : exclusions) {
			if(exclusion == null) {
				throw new NullPointerException("Exclusion must not be null");
			} else if(exclusion.equals("")) {
				/*
				 * We can do this because equals works on
				 * arbitrary objects, not just those of the same
				 * type.
				 */
				throw new IllegalArgumentException("Empty string is not a valid exclusion");
			} else {
				topLevelExclusions.add(exclusion);
			}
		}
	}

	/**
	 * Adds one or more groups that cannot occur at all in this group.
	 * 
	 * @param exclusions
	 *                The groups forbidden inside this group.
	 */
	@SafeVarargs
	public final void addGroupForbid(T... exclusions) {
		for(T exclusion : exclusions) {
			if(exclusion == null) {
				throw new NullPointerException("Exclusion must not be null");
			} else if(exclusion.equals("")) {
				/*
				 * We can do this because equals works on
				 * arbitrary objects, not just those of the same
				 * type.
				 */
				throw new IllegalArgumentException("Empty string is not a valid exclusion");
			} else {
				groupExclusions.add(exclusion);
			}
		}
	}

	/**
	 * Adds sub-group markers to this group.
	 * 
	 * @param subgroup
	 *                The token to mark a sub-group.
	 * 
	 * @param priority
	 *                The priority of this sub-group.
	 * 
	 * @param contained
	 *                Any sub-groups to enclose in this group.
	 */
	public void addSubgroup(T subgroup, int priority) {
		if(subgroup == null) {
			throw new NullPointerException("Subgroup marker must not be null");
		}

		subgroups.put(subgroup, priority);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("(");

		builder.append("groupName=[");
		builder.append(groupName);
		builder.append("], ");

		builder.append("closingDelimiters=[");
		for(T closer : closingDelimiters) {
			builder.append(closer + ",");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append("]");

		if(topLevelExclusions != null && !topLevelExclusions.isEmpty()) {
			builder.append(", ");
			builder.append("topLevelExclusions=[");
			for(T exclusion : topLevelExclusions) {
				builder.append(exclusion + ",");
			}
			builder.deleteCharAt(builder.length() - 1);
			builder.append("]");
		}

		if(groupExclusions != null && !groupExclusions.isEmpty()) {
			builder.append(", ");
			builder.append("groupExclusions=[");
			for(T exclusion : groupExclusions) {
				builder.append(exclusion + ",");
			}
			builder.deleteCharAt(builder.length() - 1);
			builder.append("]");
		}

		builder.append(" )");

		return builder.toString();
	}

	/**
	 * Open an instance of this group.
	 * 
	 * @param opener
	 *                The item that opened this group.
	 * 
	 * @return An opened instance of this group.
	 */
	public OpenGroup open(T opener) {
		return new OpenGroup(opener);
	}

}