package bjc.utils.parserutils.delims;

import bjc.utils.data.IPair;
import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.esodata.PushdownMap;
import bjc.utils.esodata.SimpleStack;
import bjc.utils.esodata.Stack;
import bjc.utils.funcdata.IMap;
import bjc.utils.funcutils.StringUtils;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.HashMap;
import java.util.Map;

/**
 * Convert linear sequences into trees that represent group structure.
 * 
 * @author EVE
 *
 * @param <T>
 *                The type of items in the sequence.
 */
public class SequenceDelimiter<T> {
	/*
	 * Mapping from opening delimiters to the names of the groups they open
	 */

	/*
	 * Mapping from group names to actual groups.
	 */
	private Map<T, DelimiterGroup<T>> groups;

	private DelimiterGroup<T> initialGroup;

	/**
	 * Create a new sequence delimiter.
	 */
	public SequenceDelimiter() {
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
	 *         <tree>     -> (<data> | <subgroup> | <group>)*
	 *         <subgroup> -> <tree> <marker>
	 *         <group>    -> <open> <tree> <close>
	 *         
	 *         <data>     -> STRING
	 *         <open>     -> STRING
	 *         <close>    -> STRING
	 *         <marker>   -> STRING
	 * </pre>
	 * 
	 * @param chars
	 *                The parameters on how to mark certain portions of the
	 *                tree.
	 * @param seq
	 *                The sequence to delimit.
	 * 
	 * @return The sequence as a tree that matches its group structure. Each
	 *         node in the tree is either a data node, a subgroup node, or a
	 *         group node.
	 * 
	 *         A data node is a leaf node whose data is the string it
	 *         represents.
	 * 
	 *         A subgroup node is a node with two children, and the name of
	 *         the sub-group as its label. The first child is the contents
	 *         of the sub-group, and the second is the marker that started
	 *         the subgroup. The marker is a leaf node labeled with its
	 *         contents, and the contents contains a recursive tree.
	 * 
	 *         A group node is a node with three children, and the name of
	 *         the group as its label. The first child is the opening
	 *         delimiter, the second is the group contents, and the third is
	 *         the closing delimiter. The delimiters are leaf nodes labeled
	 *         with their contents, while the group node contains a
	 *         recursive tree.
	 * 
	 * @throws DelimiterException
	 *                 Thrown if something went wrong during sequence
	 *                 delimitation.
	 * 
	 */
	public ITree<T> delimitSequence(SequenceCharacteristics<T> chars, @SuppressWarnings("unchecked") T... seq)
			throws DelimiterException {
		if(initialGroup == null) {
			throw new NullPointerException("Initial group must be specified.");
		}

		/*
		 * The stack of opened and not yet closed groups.
		 */
		Stack<DelimiterGroup<T>.OpenGroup> groupStack = new SimpleStack<>();

		/*
		 * Open initial group.
		 */
		groupStack.push(initialGroup.open(chars.root, null));
		
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

			IPair<T, T[]> possibleOpenPar = groupStack.top().doesOpen(tok);
			T possibleOpen = possibleOpenPar.getLeft();
			
			/*
			 * If we have an opening delimiter, handle it.
			 */
			if(possibleOpen != null) {
				DelimiterGroup<T> group = groups.get(possibleOpen);

				/*
				 * Error on groups that can't open in this
				 * context.
				 * 
				 * This means groups that can't occur at the
				 * top-level of this group, as well as nested
				 * exclusions from all enclosing groups.
				 */
				if(isForbidden(groupStack, forbiddenDelimiters, possibleOpen)) {
					StringBuilder msgBuilder = new StringBuilder();

					T forbiddenBy;

					if(whoForbid.containsKey(tok)) {
						forbiddenBy = whoForbid.get(tok);
					} else {
						forbiddenBy = groupStack.top().getName();
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
				DelimiterGroup<T>.OpenGroup open = group.open(tok, possibleOpenPar.getRight());
				groupStack.push(open);

				/*
				 * Add the nested exclusions from this group
				 */
				for(T exclusion : open.getNestingExclusions()) {
					forbiddenDelimiters.add(exclusion);

					whoForbid.put(exclusion, possibleOpen);
				}
			} else if(!groupStack.empty() && groupStack.top().isClosing(tok)) {
				/*
				 * Close the group.
				 */
				DelimiterGroup<T>.OpenGroup closed = groupStack.pop();

				groupStack.top().addItem(closed.toTree(tok, chars));

				/*
				 * Remove nested exclusions from this group.
				 */
				for(T excludedGroup : closed.getNestingExclusions()) {
					forbiddenDelimiters.remove(excludedGroup);

					whoForbid.remove(excludedGroup);
				}
			} else if(!groupStack.empty() && groupStack.top().marksSubgroup(tok)) {
				groupStack.top().markSubgroup(tok, chars);
			} else {
				groupStack.top().addItem(new Tree<>(tok));
			}
		}

		/*
		 * Error if not all groups were closed.
		 */
		if(groupStack.size() > 1) {
			DelimiterGroup<T>.OpenGroup group = groupStack.top();

			StringBuilder msgBuilder = new StringBuilder();

			String closingDelims = StringUtils.toEnglishList(group.getNestingExclusions().toArray(), false);

			String ctxList = StringUtils.toEnglishList(groupStack.toArray(), "then");

			msgBuilder.append("Unclosed group '");
			msgBuilder.append(group.getName());
			msgBuilder.append("'. Expected one of ");
			msgBuilder.append(closingDelims);
			msgBuilder.append(" to close it\nOpen groups: ");
			msgBuilder.append(ctxList);

			throw new DelimiterException(msgBuilder.toString());
		}

		return groupStack.pop().toTree(chars.root, chars);
	}

	private boolean isForbidden(Stack<DelimiterGroup<T>.OpenGroup> groupStack, Multiset<T> forbiddenDelimiters,
			T groupName) {
		boolean localForbid;
		if(groupStack.empty())
			localForbid = false;
		else
			localForbid = groupStack.top().excludes(groupName);

		return localForbid || forbiddenDelimiters.contains(groupName);
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
			group.addOpener(open, groupName);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("SequenceDelimiter [");

		if(groups != null) {
			builder.append("groups=");
			builder.append(groups);
			builder.append(",");
		}

		if(initialGroup != null) {
			builder.append("initialGroup=");
			builder.append(initialGroup);
		}

		builder.append("]");

		return builder.toString();
	}

	/**
	 * Set the initial group of this delimiter.
	 * 
	 * @param initialGroup
	 *                The initial group of this delimiter.
	 */
	public void setInitialGroup(DelimiterGroup<T> initialGroup) {
		this.initialGroup = initialGroup;
	}
}
