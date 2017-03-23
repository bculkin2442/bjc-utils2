package bjc.utils.parserutils.delims;

import bjc.utils.data.IPair;
import bjc.utils.data.ITree;
import bjc.utils.data.Pair;
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
import java.util.function.BiPredicate;
import java.util.function.Function;

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

		private T[] params;

		/**
		 * Create a new instance of a delimiter group.
		 * 
		 * @param open
		 *                The item that opened this group.
		 * 
		 * @param parms
		 *                Any parameters from the opener.
		 */
		public OpenGroup(T open, T[] parms) {
			opener = open;
			params = parms;

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
			if(impliedSubgroups.containsKey(closer)) {
				markSubgroup(impliedSubgroups.get(closer), chars);
			}

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
			if(closingDelimiters.contains(del)) {
				return true;
			}

			for(BiPredicate<T, T[]> pred : predClosers) {
				if(pred.test(del, params)) {
					return true;
				}
			}

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
		 * Get the groups that are allowed to open anywhere inside this
		 * group.
		 * 
		 * @return The groups allowed to open anywhere inside this
		 *         group.
		 */
		public Map<T, T> getNestingOpeners() {
			return nestedOpenDelimiters;
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

		/**
		 * Checks if a given token opens a group.
		 * 
		 * @param marker
		 *                The token to check.
		 * 
		 * @return The name of the group T opens, or null if it doesn't
		 *         open one.
		 */
		public IPair<T, T[]> doesOpen(T marker) {
			if(openDelimiters.containsKey(marker)) {
				return new Pair<>(openDelimiters.get(marker), null);
			}

			for(Function<T, IPair<T, T[]>> pred : predOpeners) {
				IPair<T, T[]> par = pred.apply(marker);

				if(par.getLeft() != null) {
					return par;
				}
			}

			return new Pair<>(null, null);
		}

		/**
		 * Check if this group starts a new nesting scope.
		 * 
		 * @return Whether this group starts a new nesting scope.
		 */
		public boolean isForgetful() {
			return forgetful;
		}
	}

	/**
	 * The name of this delimiter group.
	 */
	public final T groupName;

	/*
	 * The delimiters that open groups at the top level of this group.
	 */
	private Map<T, T> openDelimiters;

	/*
	 * The delimiters that open groups inside of this group.
	 */
	private Map<T, T> nestedOpenDelimiters;

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

	/*
	 * Subgroups implied by a particular closing delimiter
	 */
	private Map<T, T> impliedSubgroups;

	/*
	 * Allows more complex openings
	 */
	private List<Function<T, IPair<T, T[]>>> predOpeners;

	/*
	 * Allow more complex closings
	 */
	private List<BiPredicate<T, T[]>> predClosers;

	/*
	 * Whether or not this group starts a new nesting set.
	 */
	private boolean forgetful;

	/**
	 * Create a new empty delimiter group.
	 * 
	 * @param name
	 *                The name of the delimiter group
	 */
	public DelimiterGroup(T name) {
		if(name == null) throw new NullPointerException("Group name must not be null");

		groupName = name;

		openDelimiters = new HashMap<>();
		nestedOpenDelimiters = new HashMap<>();

		closingDelimiters = new HashSet<>();

		topLevelExclusions = new HashSet<>();
		groupExclusions = new HashSet<>();

		subgroups = new HashMap<>();
		impliedSubgroups = new HashMap<>();

		predOpeners = new LinkedList<>();
		predClosers = new LinkedList<>();
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

	/**
	 * Adds a marker that opens a group at the top level of this group.
	 * 
	 * @param opener
	 *                The marker that opens the group.
	 * 
	 * @param group
	 *                The group opened by the marker.
	 */
	public void addOpener(T opener, T group) {
		if(opener == null) {
			throw new NullPointerException("Opener must not be null");
		} else if(group == null) {
			throw new NullPointerException("Group to open must not be null");
		}

		openDelimiters.put(opener, group);
	}

	/**
	 * Adds a marker that opens a group inside of this group.
	 * 
	 * @param opener
	 *                The marker that opens the group.
	 * 
	 * @param group
	 *                The group opened by the marker.
	 */
	public void addNestedOpener(T opener, T group) {
		if(opener == null) {
			throw new NullPointerException("Opener must not be null");
		} else if(group == null) {
			throw new NullPointerException("Group to open must not be null");
		}

		nestedOpenDelimiters.put(opener, group);
	}

	/**
	 * Mark a closing delimiter as implying a subgroup.
	 * 
	 * @param closer
	 *                The closing delimiter.
	 * 
	 * @param subgroup
	 *                The subgroup to imply.
	 */
	public void implySubgroup(T closer, T subgroup) {
		if(closer == null) {
			throw new NullPointerException("Closer must not be null");
		} else if(subgroup == null) {
			throw new NullPointerException("Subgroup must not be null");
		} else if(!closingDelimiters.contains(closer)) {
			throw new IllegalArgumentException(String.format("No closing delimiter '%s' defined", closer));
		} else if(!subgroups.containsKey(subgroup)) {
			throw new IllegalArgumentException(String.format("No subgroup '%s' defined", subgroup));
		}

		impliedSubgroups.put(closer, subgroup);
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
	 * @param parms
	 *                The parameters that opened this group
	 * 
	 * @return An opened instance of this group.
	 */
	public OpenGroup open(T opener, T[] parms) {
		return new OpenGroup(opener, parms);
	}

	/**
	 * Adds a predicated opener to the top level of this group.
	 * 
	 * @param pred
	 *                The predicate that defines the opener and its
	 *                parameters.
	 */
	public void addPredOpener(Function<T, IPair<T, T[]>> pred) {
		predOpeners.add(pred);
	}

	/**
	 * Adds a predicated closer to the top level of this group.
	 * 
	 * @param pred
	 *                The predicate that defines the closer.
	 */
	public void addPredCloser(BiPredicate<T, T[]> pred) {
		predClosers.add(pred);
	}

	/**
	 * Set whether or not this group starts a new nesting set.
	 * 
	 * @param forgetful
	 *                Whether this group starts a new nesting set.
	 */
	public void setForgetful(boolean forgetful) {
		this.forgetful = forgetful;
	}
}