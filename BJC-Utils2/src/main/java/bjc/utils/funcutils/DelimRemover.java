package bjc.utils.funcutils;

import bjc.utils.esodata.Stack;
import bjc.utils.esodata.SimpleStack;

import java.util.List;
import java.util.ArrayList;

/**
 * Group delimited sequences of any kind together properly.
 *
 * @author Ben Culkin
 */
public class DelimRemover {
	/**
	 * Represents a string or possibly a delimited group of strings.
	 *
	 * @author Ben Culkin
	 */
	public static class Result {
		/**
		 * Whether this result is a delimited group, or just a string.
		 */
		public final boolean isGroup;
		
		/**
		 * The string value of this result, or null if it's a group.
		 */
		public final String stringVal;

		/*
		 * The info necessary for handling a delimited group.
		 *
		 * Only used when it is a group.
		 */
		/**
		 * The delimiter that opened this group.
		 */
		public final String opener;

		/**
		 * The tokens inside this group.
		 */
		public final Result[] contents;

		/**
		 * The delimiter that closed this group.
		 */
		public final String closer;
	}

	/**
	 * Represents a failure of some sort during delimiter grouping.
	 *
	 * @author Ben Culkin
	 */
	public static class DelimException extends RuntimeException {
	}

	/**
	 * Create a new delimiter remover.
	 */
	public DelimRemover() {

	}

	/**
	 * Groups delimited sequences in an array of string tokens together.
	 *
	 * @param tokens
	 * 		The tokens to group sequences in.
	 *
	 * @return The tokens with sequences grouped together.
	 *
	 * @throws DelimException Thrown if something went wrong during delimiter grouping.
	 */
	public Result[] delimitTokens(String[] tokens) {
		/*
		 * @TODO implement me.
		 */
		List<Result> res = new ArrayList<>(tokens.length);

		return res.toArray(new Result[0]);
	}

	/**
	 * Check if a string is properly delimited.
	 *
	 * It is guaranteed that delimitTokens will not throw a DelimException for tokens this
	 * method returns true for.
	 *
	 * @param tokens
	 * 		The sequence of tokens to check.
	 *
	 * @return Whether or not the tokens are properly delimited.
	 */
	public boolean isProperlyDelimited(String[] tokens) {
		/*
		 * @TODO implement me.
		 */
		return false;
	}
}
