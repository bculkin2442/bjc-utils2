package bjc.utils.ioutils.format;

import java.util.*;
import java.util.regex.*;

import bjc.utils.ioutils.*;
import bjc.utils.ioutils.format.directives.*;

public class CLTokenizer implements Iterator<Decree> {
	private Matcher mat;

	private Decree dir;

	public CLTokenizer(String strang) {
		this.mat = CLPattern.getDirectiveMatcher(strang);
	}

	@Override
	public boolean hasNext() {
		return !mat.hitEnd();
	}

	@Override
	public Decree next() {
		Decree tk = getNext();

		return tk;
	}

	private Decree getNext() {
		if (!hasNext()) throw new NoSuchElementException("No possible decrees remaining");

		if (dir != null) {
			Decree tmp = dir;

			dir = null;

			return tmp;
		}

		StringBuffer sb = new StringBuffer();

		while (mat.find()) {
			mat.appendReplacement(sb, "");
			
			String tmp = sb.toString();

			{
				String dirName   = mat.group("name");
				String dirFunc   = mat.group("funcname");
				String dirMods   = mat.group("modifiers");
				String dirParams = mat.group("params");

				if(dirMods == null) {
					dirMods = "";
				}

				if(dirParams == null) {
					dirParams = "";
				}

				boolean isUser = dirName == null && dirFunc != null;

				dir = new Decree(dirName, isUser,
						CLParameters.fromDirective(dirParams),
						CLModifiers.fromString(dirMods));
			}

			if (tmp.equals("")) {
				Decree dcr = dir;

				dir = null;

				return dcr;
			}

			return new Decree(sb.toString());
		}

		mat.appendTail(sb);

		return new Decree(sb.toString());
	}

	/**
	 * Read in a group decree.
	 *
	 * @param openedWith
	 * 	The decree that started the group.
	 *
	 * @param desiredClosing
	 * 	The name of the decree that will close the group.
	 *
	 * @param clauseSep
	 * 	The name of the decree that will separate clauses in the group. Pass 'null' if this group
	 * 	doesn't have separate clauses.
	 *
	 * @return A group decree with the given properties.
	 */
	public GroupDecree nextGroup(Decree openedWith, String desiredClosing, String clauseSep) {
		GroupDecree newGroup = new GroupDecree();
		
		if (!hasNext()) throw new NoSuchElementException("No decrees available");
		
		ClauseDecree curClause = new ClauseDecree();

		int nestingLevel = 1;

		do {
			Decree curDecree = next();

			// @TODO handle nesting & such
			if (curDecree.isLiteral) {
				curClause.addChild(curDecree);
			} else if (nestingLevel == 1) {
				// Clauses can only be ended at neutral nesting
				if (curDecree.isNamed(desiredClosing)) {
					newGroup.addChild(curClause);
					newGroup.closing = curDecree;

					break;
				} else if (clauseSep != null && curDecree.isNamed(clauseSep)) {
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
			} else {
				curClause.addChild(curDecree);
			}
		} while (hasNext());

		if (newGroup.closing == null) {
			String msg = String.format("Did not find closing directive for group (wanted %s)", desiredClosing);

			throw new NoSuchElementException(msg);
		}

		return newGroup;
	}
}
