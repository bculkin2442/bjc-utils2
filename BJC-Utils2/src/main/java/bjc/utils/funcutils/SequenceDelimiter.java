package bjc.utils.funcutils;

import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.esodata.PushdownMap;
import bjc.utils.esodata.SimpleStack;
import bjc.utils.esodata.Stack;
import bjc.utils.funcdata.IMap;

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
 */
public class SequenceDelimiter {
	/**
	 * Represents a possible delimiter group to match.
	 * 
	 * @author EVE
	 *
	 */
	public static class DelimiterGroup {
		/**
		 * The name of this delimiter group.
		 */
		public final String groupName;

		/*
		 * The delimiters that close this group.
		 */
		private Set<String> closingDelimiters;

		/*
		 * The groups that can't occur in the top level of this group.
		 */
		private Set<String> topLevelExclusions;

		/*
		 * The groups that can't occur anywhere inside this group.
		 */
		private Set<String> groupExclusions;

		/**
		 * Create a new empty delimiter group.
		 * 
		 * @param name
		 *                The name of the delimiter group
		 */
		public DelimiterGroup(String name) {
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
		public boolean isClosing(String del) {
			return closingDelimiters.contains(del);
		}

		/**
		 * Adds one or more delimiters that close this group.
		 * 
		 * @param closers
		 *                Delimiters that close this group.
		 */
		public void addClosing(String... closers) {
			List<String> closerList = Arrays.asList(closers);

			for(String closer : closerList) {
				if(closer == null) {
					throw new NullPointerException("Closing delimiter must not be null");
				} else if(closer.equals("")) {
					throw new IllegalArgumentException(
							"Empty string is not a valid closing delimiter");
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
		public void addTopLevelForbid(String... exclusions) {
			for(String exclusion : exclusions) {
				if(exclusion == null) {
					throw new NullPointerException("Exclusion must not be null");
				} else if(exclusion.equals("")) {
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
		public void addGroupForbid(String... exclusions) {
			for(String exclusion : exclusions) {
				if(exclusion == null) {
					throw new NullPointerException("Exclusion must not be null");
				} else if(exclusion.equals("")) {
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
			for(String closer : closingDelimiters) {
				builder.append(closer + ",");
			}
			builder.deleteCharAt(builder.length() - 1);
			builder.append("]");

			if(topLevelExclusions != null && !topLevelExclusions.isEmpty()) {
				builder.append(", ");
				builder.append("topLevelExclusions=[");
				for(String exclusion : topLevelExclusions) {
					builder.append(exclusion + ",");
				}
				builder.deleteCharAt(builder.length() - 1);
				builder.append("]");
			}

			if(groupExclusions != null && !groupExclusions.isEmpty()) {
				builder.append(", ");
				builder.append("groupExclusions=[");
				for(String exclusion : topLevelExclusions) {
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
	private Map<String, String> openDelimiters;

	/*
	 * Mapping from group names to actual groups.
	 */
	private Map<String, DelimiterGroup> groups;

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
	public ITree<String> delimitSequence(String[] seq) throws DelimiterException {
		/*
		 * The root node of the tree to give back.
		 */
		ITree<String> res = new Tree<>("");

		/*
		 * Handle the trivial case where there are no groups.
		 */
		if(openDelimiters.isEmpty()) {
			for(String tok : seq) {
				res.addChild(new Tree<>(tok));
			}

			return res;
		}

		/*
		 * The stack of trees that represent the sequence.
		 */
		Stack<ITree<String>> trees = new SimpleStack<>();
		trees.push(res);

		/*
		 * The stack of opened and not yet closed groups.
		 */
		Stack<DelimiterGroup> groupStack = new SimpleStack<>();

		/*
		 * Groups that aren't allowed to be opened at the moment.
		 */
		Multiset<String> forbiddenDelimiters = HashMultiset.create();

		/*
		 * Map of who forbid what for debugging purposes.
		 */
		IMap<String, String> whoForbid = new PushdownMap<>();

		for(int i = 0; i < seq.length; i++) {
			String tok = seq[i];

			/*
			 * If we have an opening delimiter, handle it.
			 */
			if(openDelimiters.containsKey(tok)) {
				String groupName = openDelimiters.get(tok);
				DelimiterGroup group = groups.get(groupName);

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

					String forbiddenBy;

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
				ITree<String> groupTree = new Tree<>(groupName);
				groupTree.addChild(new Tree<>(tok));

				/*
				 * The tree that represents the contents of the
				 * opened group.
				 */
				ITree<String> groupContents = new Tree<>("contents");

				/*
				 * Add the trees to the open trees.
				 */
				trees.push(groupTree);
				trees.push(groupContents);

				/*
				 * Add the nested exclusions from this group
				 */
				for(String exclusion : group.groupExclusions) {
					forbiddenDelimiters.add(exclusion);
					
					whoForbid.put(exclusion, groupName);
				}
			} else if(!groupStack.empty() && groupStack.top().isClosing(tok)) {
				/*
				 * Close the group.
				 */
				DelimiterGroup closed = groupStack.pop();

				/*
				 * Remove the contents of the group and the
				 * group itself from the stack.
				 */
				ITree<String> contents = trees.pop();
				ITree<String> groupTree = trees.pop();

				/*
				 * Fill in the group node.
				 */
				groupTree.addChild(contents);
				groupTree.addChild(new Tree<>(tok));

				/*
				 * Add the group node to the group that
				 * contained it.
				 */
				trees.top().addChild(groupTree);

				/*
				 * Remove nested exclusions from this group.
				 */
				for(String excludedGroup : closed.groupExclusions) {
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
			DelimiterGroup group = groupStack.top();
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

	private boolean isForbidden(Stack<DelimiterGroup> groupStack, Multiset<String> forbiddenDelimiters,
			String groupName) {
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
	public void addOpener(String open, String groupName) {
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
	public void addGroup(DelimiterGroup group) {
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
	public void addGroup(String[] openers, String groupName, String... closers) {
		DelimiterGroup group = new DelimiterGroup(groupName);

		group.addClosing(closers);

		addGroup(group);

		for(String open : openers) {
			addOpener(open, groupName);
		}
	}
}
