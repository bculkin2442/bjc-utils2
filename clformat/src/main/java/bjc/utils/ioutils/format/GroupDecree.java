package bjc.utils.ioutils.format;

import java.util.*;

/**
 * Represents an enclosed group of decrees.
 *
 * This is used for all of the decrees that take another format string as part of their body.
 *
 * @author Ben Culkin
 */
public class GroupDecree {
	/**
	 * The decree that opened this group.
	 */
	public Decree opening;
	
	/**
	 * The decree that closed this group.
	 */
	public Decree closing;

	/**
	 * The clauses that make up the body of this group.
	 */
	public List<ClauseDecree> body;

	/**
	 * Create a new group decree with no values.
	 */
	public GroupDecree() {
		body = new ArrayList<>();
	}

	/**
	 * Create a new group decree with a given body.
	 *
	 * @param children
	 * 	The decrees that form the body of the group.
	 */
	public GroupDecree(ClauseDecree... children) {
		this();

		for (ClauseDecree child : children) {
			body.add(child);
		}
	}

	/**
	 * Create a new group decree with all of the fields filled out.
	 *
	 * @param opening
	 * 	The decree opening the group.
	 *
	 * @param closing
	 * 	The decree closing the group.
	 *
	 * @param children
	 * 	The decree making up the body of the group.
	 */
	public GroupDecree(Decree opening, Decree closing, ClauseDecree... children) {
		this(children);

		this.opening = opening;
		this.closing = closing;
	}

	/**
	 * Add a decree to this group.
	 *
	 * @param child
	 * 	The decree to add to the group.
	 */
	public void addChild(ClauseDecree child) {
		body.add(child);
	}

	/**
	 * Get the first clause from the group.
	 *
	 * @return The first clause in the group
	 */
	public ClauseDecree clause() {
		return body.get(0);
	}

	/**
	 * Get a specific clause from the group.
	 *
	 * @param idx
	 * 	The index of the clause to get.
	 * 
	 * @return The clause at that index.
	 */
	public ClauseDecree clause(int idx) {
		return body.get(idx);
	}

	/**
	 * Get the body of the first clause. 
	 * 
	 * @return The decrees that make up the body of the first clause.
	 */
	public List<Decree> unwrap() {
		return body.get(0).body;
	}
}