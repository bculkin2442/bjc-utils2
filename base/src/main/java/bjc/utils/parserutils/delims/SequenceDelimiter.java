package bjc.utils.parserutils.delims;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import bjc.data.IPair;
import bjc.data.ITree;
import bjc.data.Tree;
import bjc.esodata.PushdownMap;
import bjc.esodata.SimpleStack;
import bjc.esodata.Stack;
import bjc.funcdata.IMap;
import bjc.utils.funcutils.StringUtils;

/**
 * Convert linear sequences into trees that represent group structure.
 *
 * @author EVE
 *
 * @param <T>
 *            The type of items in the sequence.
 */
public class SequenceDelimiter<T> {
	/* Mapping from group names to actual groups. */
	private final Map<T, DelimiterGroup<T>> groups;

	/* The initial group to start with. */
	private DelimiterGroup<T> initialGroup;

	/** Create a new sequence delimiter. */
	public SequenceDelimiter() {
		groups = new HashMap<>();
	}

	/**
	 * Convert a linear sequence into a tree that matches the delimiter structure.
	 *
	 * Essentially, creates a parse tree of the expression against the following
	 * grammar while obeying the defined grouping rules.
	 *
	 * <pre>
	 *         <tree>     → (<data> | <subgroup> | <group>)*
	 *         <subgroup> → <tree> <marker>
	 *         <group>    → <open> <tree> <close>
	 *
	 *         <data>     → STRING
	 *         <open>     → STRING
	 *         <close>    → STRING
	 *         <marker>   → STRING
	 * </pre>
	 *
	 * @param chars
	 *              The parameters on how to mark certain portions of the tree.
	 * @param seq
	 *              The sequence to delimit.
	 *
	 * @return The sequence as a tree that matches its group structure. Each node in
	 *         the tree is either a data node, a subgroup node, or a group node.
	 *
	 *         A data node is a leaf node whose data is the string it represents.
	 *
	 *         A subgroup node is a node with two children, and the name of the
	 *         sub-group as its label. The first child is the contents of the
	 *         sub-group, and the second is the marker that started the subgroup.
	 *         The marker is a leaf node labeled with its contents, and the contents
	 *         contains a recursive tree.
	 *
	 *         A group node is a node with three children, and the name of the group
	 *         as its label. The first child is the opening delimiter, the second is
	 *         the group contents, and the third is the closing delimiter. The
	 *         delimiters are leaf nodes labeled with their contents, while the
	 *         group node contains a recursive tree.
	 *
	 * @throws DelimiterException
	 *                            Thrown if something went wrong during sequence
	 *                            delimitation.
	 *
	 */
	public ITree<T> delimitSequence(final SequenceCharacteristics<T> chars,
			@SuppressWarnings("unchecked") final T... seq) throws DelimiterException {
		if (initialGroup == null) {
			throw new NullPointerException("Initial group must be specified.");
		} else if (chars == null) {
			throw new NullPointerException("Sequence characteristics must not be null");
		}

		/* The stack of opened and not yet closed groups. */
		final Stack<DelimiterGroup<T>.OpenGroup> groupStack = new SimpleStack<>();

		/* Open initial group. */
		groupStack.push(initialGroup.open(chars.root, null));

		/* Groups that aren't allowed to be opened at the moment. */
		final Stack<Multiset<T>> forbiddenDelimiters = new SimpleStack<>();
		forbiddenDelimiters.push(HashMultiset.create());

		/* Groups that are allowed to be opened at the moment. */
		final Stack<Multimap<T, T>> allowedDelimiters = new SimpleStack<>();
		allowedDelimiters.push(HashMultimap.create());

		/* Map of who forbid what for debugging purposes. */
		final IMap<T, T> whoForbid = new PushdownMap<>();

		/*
		 * Process each member of the sequence.
		 */
		for (int i = 0; i < seq.length; i++) {
			final T tok = seq[i];

			/* Check if this token could open a group. */
			final IPair<T, T[]> possibleOpenPar = groupStack.top().doesOpen(tok);
			T possibleOpen = possibleOpenPar.getLeft();

			if (possibleOpen == null) {
				/*
				 * Handle nested openers.
				 *
				 * Local openers take priority over nested ones if they overlap.
				 */
				if (allowedDelimiters.top().containsKey(tok)) {
					possibleOpen = allowedDelimiters.top().get(tok).iterator().next();
				}
			}

			/*
			 * If we have an opening delimiter, handle it.
			 */
			if (possibleOpen != null) {
				final DelimiterGroup<T> group = groups.get(possibleOpen);

				/*
				 * Error on groups that can't open in this context.
				 *
				 * This means groups that can't occur at the top-level of this group, as
				 * well as nested exclusions from all enclosing groups.
				 */
				if (isForbidden(groupStack, forbiddenDelimiters, possibleOpen)) {
					T forbiddenBy;

					forbiddenBy = whoForbid.get(tok).orElse(groupStack.top().getName());

					final String ctxList
							= StringUtils.toEnglishList(groupStack.toArray(), "then");

					final String fmt
							= "Group '%s' can't be opened in this context. (forbidden by '%s')\nContext Stack: %s";

					throw new DelimiterException(
							String.format(fmt, group, forbiddenBy, ctxList));
				}

				/* Add an open group. */
				final DelimiterGroup<T>.OpenGroup open
						= group.open(tok, possibleOpenPar.getRight());
				groupStack.push(open);

				/*
				 * Handle 'forgetful' groups that reset nesting
				 */
				if (open.isForgetful()) {
					allowedDelimiters.push(HashMultimap.create());
					forbiddenDelimiters.push(HashMultiset.create());
				}

				/* Add the nested opens from this group. */
				final Multimap<T, T> currentAllowed = allowedDelimiters.top();
				for (final Entry<T, T> opener : open.getNestingOpeners().entrySet()) {
					currentAllowed.put(opener.getKey(), opener.getValue());
				}

				/* Add the nested exclusions from this group */
				final Multiset<T> currentForbidden = forbiddenDelimiters.top();
				for (final T exclusion : open.getNestingExclusions()) {
					currentForbidden.add(exclusion);

					whoForbid.put(exclusion, possibleOpen);
				}
			} else if (!groupStack.isEmpty() && groupStack.top().isClosing(tok)) {
				/*
				 * Close the group.
				 */
				final DelimiterGroup<T>.OpenGroup closed = groupStack.pop();

				groupStack.top().addItem(closed.toTree(tok, chars));

				/* Remove nested exclusions from this group. */
				final Multiset<T> currentForbidden = forbiddenDelimiters.top();
				for (final T excludedGroup : closed.getNestingExclusions()) {
					currentForbidden.remove(excludedGroup);

					whoForbid.remove(excludedGroup);
				}

				/* Remove the nested opens from this group. */
				final Multimap<T, T> currentAllowed = allowedDelimiters.top();
				for (final Entry<T, T> closer : closed.getNestingOpeners().entrySet()) {
					currentAllowed.remove(closer.getKey(), closer.getValue());
				}

				/*
				 * Handle 'forgetful' groups that reset nesting.
				 */
				if (closed.isForgetful()) {
					allowedDelimiters.drop();
					forbiddenDelimiters.drop();
				}
			} else if (!groupStack.isEmpty() && groupStack.top().marksSubgroup(tok)) {
				/*
				 * Mark a subgroup.
				 */
				groupStack.top().markSubgroup(tok, chars);
			} else {
				/* Add an item to the group. */
				groupStack.top().addItem(new Tree<>(tok));
			}
		}

		/*
		 * Error if not all groups were closed.
		 */
		if (groupStack.size() > 1) {
			final DelimiterGroup<T>.OpenGroup group = groupStack.top();

			final String closingDelims = StringUtils
					.toEnglishList(group.getNestingExclusions().toArray(), false);

			final String ctxList
					= StringUtils.toEnglishList(groupStack.toArray(), "then");

			final String fmt
					= "Unclosed group '%s'. Expected one of %s to close it.\nOpen groups: %s";

			throw new DelimiterException(
					String.format(fmt, group.getName(), closingDelims, ctxList));
		}

		return groupStack.pop().toTree(chars.root, chars);
	}

	/* Check if a group is forbidden to open in a context. */
	private boolean isForbidden(final Stack<DelimiterGroup<T>.OpenGroup> groupStack,
			final Stack<Multiset<T>> forbiddenDelimiters, final T groupName) {
		boolean localForbid;

		/*
		 * Check if a delimiter is locally forbidden.
		 */
		if (groupStack.isEmpty()) {
			localForbid = false;
		} else {
			localForbid = groupStack.top().excludes(groupName);
		}

		return localForbid || forbiddenDelimiters.top().contains(groupName);
	}

	/**
	 * Add a delimiter group.
	 *
	 * @param group
	 *              The delimiter group.
	 */
	public void addGroup(final DelimiterGroup<T> group) {
		if (group == null) {
			throw new NullPointerException("Group must not be null");
		}

		groups.put(group.groupName, group);
	}

	/**
	 * Creates and adds a delimiter group using the provided settings.
	 *
	 * @param openers
	 *                  The tokens that open this group
	 * @param groupName
	 *                  The name of the group
	 * @param closers
	 *                  The tokens that close this group
	 */
	public void addGroup(final T[] openers, final T groupName,
			@SuppressWarnings("unchecked") final T... closers) {
		final DelimiterGroup<T> group = new DelimiterGroup<>(groupName);

		group.addClosing(closers);

		addGroup(group);

		for (final T open : openers) {
			group.addOpener(open, groupName);
		}
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		builder.append("SequenceDelimiter [");

		if (groups != null) {
			builder.append("groups=");
			builder.append(groups);
			builder.append(",");
		}

		if (initialGroup != null) {
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
	 *                     The initial group of this delimiter.
	 */
	public void setInitialGroup(final DelimiterGroup<T> initialGroup) {
		this.initialGroup = initialGroup;
	}
}
