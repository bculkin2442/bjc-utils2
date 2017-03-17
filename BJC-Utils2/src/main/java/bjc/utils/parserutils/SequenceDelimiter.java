package bjc.utils.parserutils;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.esodata.PushdownMap;
import bjc.utils.esodata.SimpleStack;
import bjc.utils.esodata.Stack;
import bjc.utils.funcdata.IMap;
import bjc.utils.funcutils.StringUtils;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Convert linear sequences into trees that represent group structure.
 * 
 * @author EVE
 *
 * @param <T>
 *                The type of items in the sequence.
 */
public class SequenceDelimiter<T> {
	/**
	 * Represents a possible delimiter group to match.
	 * 
	 * @author EVE
	 *
	 * @param <T2>
	 *                The type of items in the sequence.
	 */
	public static class DelimiterGroup<T2> {
		/**
		 * The name of this delimiter group.
		 */
		public final T2 groupName;

		/*
		 * The delimiters that close this group.
		 */
		private Set<T2> closingDelimiters;

		/*
		 * The groups that can't occur in the top level of this group.
		 */
		private Set<T2> topLevelExclusions;

		/*
		 * The groups that can't occur anywhere inside this group.
		 */
		private Set<T2> groupExclusions;

		/**
		 * Create a new empty delimiter group.
		 * 
		 * @param name
		 *                The name of the delimiter group
		 */
		public DelimiterGroup(T2 name) {
			if(name == null) throw new NullPointerException("Group name must not be null");

			groupName = name;

			closingDelimiters = new HashSet<>();
			topLevelExclusions = new HashSet<>();
			groupExclusions = new HashSet<>();
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
		public boolean isClosing(T2 del) {
			return closingDelimiters.contains(del);
		}

		/**
		 * Adds one or more delimiters that close this group.
		 * 
		 * @param closers
		 *                Delimiters that close this group.
		 */
		@SafeVarargs
		public final void addClosing(T2... closers) {
			List<T2> closerList = Arrays.asList(closers);

			for(T2 closer : closerList) {
				if(closer == null) {
					throw new NullPointerException("Closing delimiter must not be null");
				} else if(closer.equals("")) {
					/*
					 * We can do this because equals works
					 * on arbitrary objects, not just those
					 * of the same type.
					 */
					throw new IllegalArgumentException("Empty string is not a valid exclusion");
				} else {
					closingDelimiters.add(closer);
				}
			}
		}

		/**
		 * Adds one or more groups that cannot occur in the top level of
		 * this group.
		 * 
		 * @param exclusions
		 *                The groups forbidden in the top level of this
		 *                group.
		 */
		@SafeVarargs
		public final void addTopLevelForbid(T2... exclusions) {
			for(T2 exclusion : exclusions) {
				if(exclusion == null) {
					throw new NullPointerException("Exclusion must not be null");
				} else if(exclusion.equals("")) {
					/*
					 * We can do this because equals works
					 * on arbitrary objects, not just those
					 * of the same type.
					 */
					throw new IllegalArgumentException("Empty string is not a valid exclusion");
				} else {
					topLevelExclusions.add(exclusion);
				}
			}
		}

		/**
		 * Adds one or more groups that cannot occur at all in this
		 * group.
		 * 
		 * @param exclusions
		 *                The groups forbidden inside this group.
		 */
		@SafeVarargs
		public final void addGroupForbid(T2... exclusions) {
			for(T2 exclusion : exclusions) {
				if(exclusion == null) {
					throw new NullPointerException("Exclusion must not be null");
				} else if(exclusion.equals("")) {
					/*
					 * We can do this because equals works
					 * on arbitrary objects, not just those
					 * of the same type.
					 */
					throw new IllegalArgumentException("Empty string is not a valid exclusion");
				} else {
					groupExclusions.add(exclusion);
				}
			}
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();

			builder.append("(");

			builder.append("groupName=[");
			builder.append(groupName);
			builder.append("], ");

			builder.append("closingDelimiters=[");
			for(T2 closer : closingDelimiters) {
				builder.append(closer + ",");
			}
			builder.deleteCharAt(builder.length() - 1);
			builder.append("]");

			if(topLevelExclusions != null && !topLevelExclusions.isEmpty()) {
				builder.append(", ");
				builder.append("topLevelExclusions=[");
				for(T2 exclusion : topLevelExclusions) {
					builder.append(exclusion + ",");
				}
				builder.deleteCharAt(builder.length() - 1);
				builder.append("]");
			}

			if(groupExclusions != null && !groupExclusions.isEmpty()) {
				builder.append(", ");
				builder.append("groupExclusions=[");
				for(T2 exclusion : topLevelExclusions) {
					builder.append(exclusion + ",");
				}
				builder.deleteCharAt(builder.length() - 1);
				builder.append("]");
			}

			builder.append(" )");

			return builder.toString();
		}

	}

	/**
	 * The superclass for exceptions thrown during sequence delimitation.
	 */
	public static class DelimiterException extends RuntimeException {
		/**
		 * Create a new generic delimiter exception.
		 * 
		 * @param res
		 *                The reason for this exception.
		 */
		public DelimiterException(String res) {
			super(res);
		}
	}

	/*
	 * Mapping from opening delimiters to the names of the groups they open
	 */
	private Map<T, T> openDelimiters;

	/*
	 * Mapping from group names to actual groups.
	 */
	private Map<T, DelimiterGroup<T>> groups;

	/**
	 * Create a new sequence delimiter.
	 */
	public SequenceDelimiter() {
		openDelimiters = new HashMap<>();

		groups = new HashMap<>();
	}

	/**
	 * Convert a linear sequence into a tree that matches the delimiter
	 * structure.
	 * 
	 * Essentially, creates a parse tree of the expression against the
	 * following grammar while obeying the defined group rules.
	 * 
	 * <pre>
	 *         <tree>  -> (<data> | <group>)*
	 *         <group> -> <open> <tree> <close>
	 *         <data>  -> STRING
	 *         <open>  -> STRING
	 *         <close> -> STRING
	 * </pre>
	 * 
	 * @param seq
	 *                The sequence to delimit.
	 * 
	 * @param root
	 *                The root of the returned tree.
	 * 
	 * @param contents
	 *                The item to use to mark the contents of a group
	 * 
	 * @return The sequence as a tree that matches its group structure. Each
	 *         node in the tree is either a data node or a group node.
	 * 
	 *         A data node is a leaf node whose data is the string it
	 *         represents.
	 * 
	 *         A group node is a node with three children, and the name of
	 *         the group as its label. The first child is the opening
	 *         delimiter, the second is the group contents, and the third is
	 *         the closing delimiter. The delimiters are leaf nodes labeled
	 *         with their contents, while the group node contains a list of
	 *         data and group nodes.
	 * 
	 * @throws DelimiterException
	 *                 Thrown if something went wrong during sequence
	 *                 delimitation.
	 * 
	 */
	public ITree<T> delimitSequence(T root, T contents, @SuppressWarnings("unchecked") T... seq) throws DelimiterException {
		/*
		 * The root node of the tree to give back.
		 */
		ITree<T> res = new Tree<>(root);

		/*
		 * Handle the trivial case where there are no groups.
		 */
		if(openDelimiters.isEmpty()) {
			for(T tok : seq) {
				res.addChild(new Tree<>(tok));
			}

			return res;
		}

		/*
		 * The stack of trees that represent the sequence.
		 */
		Stack<ITree<T>> trees = new SimpleStack<>();
		trees.push(res);

		/*
		 * The stack of opened and not yet closed groups.
		 */
		Stack<DelimiterGroup<T>> groupStack = new SimpleStack<>();

		/*
		 * Groups that aren't allowed to be opened at the moment.
		 */
		Multiset<T> forbiddenDelimiters = HashMultiset.create();

		/*
		 * Map of who forbid what for debugging purposes.
		 */
		IMap<T, T> whoForbid = new PushdownMap<>();

		for(int i = 0; i < seq.length; i++) {
			T tok = seq[i];

			/*
			 * If we have an opening delimiter, handle it.
			 */
			if(openDelimiters.containsKey(tok)) {
				T groupName = openDelimiters.get(tok);
				DelimiterGroup<T> group = groups.get(groupName);

				/*
				 * Error on groups that can't open in this
				 * context.
				 * 
				 * This means groups that can't occur at the
				 * top-level of this group, as well as nested
				 * exclusions from all enclosing groups.
				 */
				if(isForbidden(groupStack, forbiddenDelimiters, groupName)) {
					StringBuilder msgBuilder = new StringBuilder();

					T forbiddenBy;

					if(whoForbid.containsKey(tok)) {
						forbiddenBy = whoForbid.get(tok);
					} else {
						forbiddenBy = groupStack.top().groupName;
					}

					String ctxList = StringUtils.toEnglishList(groupStack.toArray(), "then");

					msgBuilder.append("Group '");
					msgBuilder.append(group);
					msgBuilder.append("' can't be opened in this context.");
					msgBuilder.append(" (forbidden by '");
					msgBuilder.append(forbiddenBy);
					msgBuilder.append("')\nContext stack: ");
					msgBuilder.append(ctxList);

					throw new DelimiterException(msgBuilder.toString());
				}

				/*
				 * Add an open group.
				 */
				groupStack.push(group);

				/*
				 * The tree that represents the opened group.
				 */
				ITree<T> groupTree = new Tree<>(groupName);
				groupTree.addChild(new Tree<>(tok));

				/*
				 * The tree that represents the contents of the
				 * opened group.
				 */
				ITree<T> groupContents = new Tree<>(contents);

				/*
				 * Add the trees to the open trees.
				 */
				trees.push(groupTree);
				trees.push(groupContents);

				/*
				 * Add the nested exclusions from this group
				 */
				for(T exclusion : group.groupExclusions) {
					forbiddenDelimiters.add(exclusion);

					whoForbid.put(exclusion, groupName);
				}
			} else if(!groupStack.empty() && groupStack.top().isClosing(tok)) {
				/*
				 * Close the group.
				 */
				DelimiterGroup<T> closed = groupStack.pop();

				/*
				 * Remove the contents of the group and the
				 * group itself from the stack.
				 */
				ITree<T> contentTree = trees.pop();
				ITree<T> groupTree = trees.pop();

				/*
				 * Fill in the group node.
				 */
				groupTree.addChild(contentTree);
				groupTree.addChild(new Tree<>(tok));

				/*
				 * Add the group node to the group that
				 * contained it.
				 */
				trees.top().addChild(groupTree);

				/*
				 * Remove nested exclusions from this group.
				 */
				for(T excludedGroup : closed.groupExclusions) {
					forbiddenDelimiters.remove(excludedGroup);

					whoForbid.remove(excludedGroup);
				}
			} else {
				trees.top().addChild(new Tree<>(tok));
			}
		}

		/*
		 * Error if not all groups were closed.
		 */
		if(!groupStack.empty()) {
			DelimiterGroup<T> group = groupStack.top();
			StringBuilder msgBuilder = new StringBuilder();

			String closingDelims = StringUtils.toEnglishList(group.closingDelimiters.toArray(), false);

			String ctxList = StringUtils.toEnglishList(groupStack.toArray(), "then");

			msgBuilder.append("Unclosed group '");
			msgBuilder.append(group.groupName);
			msgBuilder.append("'. Expected one of ");
			msgBuilder.append(closingDelims);
			msgBuilder.append(" to close it\nOpen groups: ");
			msgBuilder.append(ctxList);

			throw new DelimiterException(msgBuilder.toString());
		}

		return res;
	}

	private boolean isForbidden(Stack<DelimiterGroup<T>> groupStack, Multiset<T> forbiddenDelimiters, T groupName) {
		boolean localForbid;
		if(groupStack.empty())
			localForbid = false;
		else
			localForbid = groupStack.top().topLevelExclusions.contains(groupName);

		return localForbid || forbiddenDelimiters.contains(groupName);
	}

	/**
	 * Add a open delimiter for the specified group.
	 * 
	 * @param open
	 *                The open delimiter.
	 * @param groupName
	 *                The name of the group it opens.
	 */
	public void addOpener(T open, T groupName) {
		if(open == null) {
			throw new NullPointerException("Opener must not be null");
		} else if(open.equals("")) {
			throw new IllegalArgumentException("Empty string is not a valid opening delimiter");
		} else if(groupName == null) {
			throw new NullPointerException("Group name must not be null");
		} else if(!groups.containsKey(groupName)) {
			throw new IllegalArgumentException("Group " + groupName + " doesn't exist.");
		}

		openDelimiters.put(open, groupName);
	}

	/**
	 * Add a delimiter group.
	 * 
	 * @param group
	 *                The delimiter group.
	 */
	public void addGroup(DelimiterGroup<T> group) {
		if(group == null) {
			throw new NullPointerException("Group must not be null");
		}

		groups.put(group.groupName, group);
	}

	/**
	 * Creates and adds a delimiter group using the provided settings.
	 * 
	 * @param openers
	 *                The tokens that open this group
	 * @param groupName
	 *                The name of the group
	 * @param closers
	 *                The tokens that close this group
	 */
	public void addGroup(T[] openers, T groupName, @SuppressWarnings("unchecked") T... closers) {
		DelimiterGroup<T> group = new DelimiterGroup<>(groupName);

		group.addClosing(closers);

		addGroup(group);

		for(T open : openers) {
			addOpener(open, groupName);
		}
	}
}
