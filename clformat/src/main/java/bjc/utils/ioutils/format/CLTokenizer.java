package bjc.utils.ioutils.format;

import java.util.*;
import java.util.regex.*;

/**
 * Tokenizer for creating @{link Decree}s from strings.
 *
 * @author bjculkin
 *
 */
public class CLTokenizer implements Iterator<SimpleDecree> {
	/**
	 * Whether or not the tokenizer is in debug mode or not.
	 */
	public static boolean DEBUG = false;

	/*
	 * Internal class for a tokenizer that returns a specific set of tokens.
	 */
	private static class SetCLTokenizer extends CLTokenizer {
		private Iterator<SimpleDecree> body;

		public SetCLTokenizer(Iterator<SimpleDecree> bod) {
			body = bod;
		}

		public SetCLTokenizer(Iterable<SimpleDecree> bod) {
			body = bod.iterator();
		}

		@Override
		public boolean hasNext() {
			return body.hasNext();
		}

		@Override
		public SimpleDecree next() {
			return body.next();
		}
	}

	private Matcher mat;

	private SimpleDecree dir;

	/**
	 * Empty constructor that should only be invoked if you are a subclass who
	 * overrides hasNext()/next().
	 */
	protected CLTokenizer() {

	}

	/**
	 * Create a new tokenizer, tokenizing from a given string.
	 *
	 * @param strang
	 *               The string to tokenize from.
	 */
	public CLTokenizer(String strang) {
		this.mat = CLPattern.getDirectiveMatcher(strang);
	}

	/**
	 * Create a CLTokenizer yielding a given set of decrees.
	 *
	 * @param bod
	 *            The decrees to yield.
	 *
	 * @return A tokenizer yielding the given set of decrees.
	 */
	public static CLTokenizer fromTokens(Iterator<SimpleDecree> bod) {
		return new SetCLTokenizer(bod);
	}

	/**
	 * Create a CLTokenizer yielding a given set of decrees.
	 *
	 * @param bod
	 *            The decrees to yield.
	 *
	 * @return A tokenizer yielding the given set of decrees.
	 */
	public static CLTokenizer fromTokens(Iterable<SimpleDecree> bod) {
		return new SetCLTokenizer(bod);
	}

	@Override
	public boolean hasNext() {
		return !mat.hitEnd();
	}

	@Override
	public SimpleDecree next() {
		return getNext();
	}

	private SimpleDecree getNext() {
		if (!hasNext()) throw new NoSuchElementException("No possible decrees remaining");

		if (dir != null) {
			SimpleDecree tmp = dir;

			dir = null;

			return tmp;
		}

		StringBuffer sb = new StringBuffer();

		while (mat.find()) {
			mat.appendReplacement(sb, "");

			String tmp = sb.toString();

			{
				String directiveName = mat.group("name");
				String directiveFunction = mat.group("funcname");
				String directiveModifierString = mat.group("modifiers");
				String directiveParameterString = mat.group("params");

				if (directiveModifierString  == null)  directiveModifierString  = "";
				if (directiveParameterString == null)  directiveParameterString = "";

				boolean isUser = directiveName == null && directiveFunction != null;

				dir = new SimpleDecree(directiveName, isUser,
						CLParameters.fromDirective(directiveParameterString),
						CLModifiers.fromString(directiveModifierString));
				
				dir.setPosition(mat.start(), mat.end());
			}

			if (tmp.equals("")) {
				SimpleDecree dcr = dir;

				dir = null;

				return dcr;
			}

			return new SimpleDecree(sb.toString(), mat.start(), mat.end());
		}

		mat.appendTail(sb);

		return new SimpleDecree(sb.toString());
	}

	/**
	 * Read in a group decree.
	 *
	 * @param openedWith
	 *                       The decree that started the group.
	 *
	 * @param desiredClosing
	 *                       The name of the decree that will close the group.
	 *
	 * @return A group decree with the given properties.
	 */
	public GroupDecree nextGroup(SimpleDecree openedWith, String desiredClosing) {
		return nextGroup(openedWith, desiredClosing, null);
	}

	/**
	 * Read in a group decree.
	 *
	 * @param openedWith
	 *                       The decree that started the group.
	 *
	 * @param desiredClosing
	 *                       The name of the decree that will close the group.
	 *
	 * @param clauseSep
	 *                       The name of the decree that will separate clauses in
	 *                       the group. Pass 'null' if this group doesn't have
	 *                       separate clauses.
	 *
	 * @return A group decree with the given properties.
	 */
	public GroupDecree nextGroup(SimpleDecree openedWith, String desiredClosing,
			String clauseSep) {
		GroupDecree newGroup = new GroupDecree();
		newGroup.opening = openedWith;

		if (!hasNext()) {
			String fmt = "No decrees available for group starting with %s at %#s";
			throw new NoSuchElementException(String.format(fmt, openedWith.name, openedWith.position));
		}

		ClauseDecree curClause = new ClauseDecree();

		int nestingLevel = 1;

		SimpleDecree curDecree;

		do {
			curDecree = next();

			if (curDecree.isLiteral) {
				curClause.addChild(curDecree);
			} else if (nestingLevel == 1) {
				// Clauses can only be ended at neutral nesting
				if (curDecree.isNamed(desiredClosing)) {
					newGroup.addChild(curClause);
					newGroup.closing = curDecree;

					if (DEBUG) {
						System.err.printf("[TRACE] Closing with %s\n", curDecree);
					}

					break;
				} else if (clauseSep != null && curDecree.isNamed(clauseSep)) {
					if (DEBUG)
						System.err.printf("[TRACE] Clause separator %s\n", curDecree);
					curClause.terminator = curDecree;
					newGroup.addChild(curClause);

					curClause = new ClauseDecree();
				} else {
					curClause.addChild(curDecree);
				}
			} else if (curDecree.isNamed(openedWith.name)) {
				// Nest one level deeper
				nestingLevel += 1;

				curClause.addChild(curDecree);
			} else if (curDecree.isNamed(desiredClosing)) {
				// Unnest
				nestingLevel -= 1;

				curClause.addChild(curDecree);
			} else {
				curClause.addChild(curDecree);
			}
		} while (hasNext());

		if (newGroup.closing == null) {
			String msg = String.format(
					"Did not find closing directive for group (wanted \"%s\", last decree was \"%s\" at %#s)",
					desiredClosing, curDecree.name, curDecree.position);

			throw new NoSuchElementException(msg);
		}

		return newGroup;
	}
}
