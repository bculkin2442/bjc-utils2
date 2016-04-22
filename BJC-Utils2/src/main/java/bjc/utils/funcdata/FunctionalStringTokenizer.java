package bjc.utils.funcdata;

import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A string tokenizer that exposes a functional interface
 * 
 * @author ben
 *
 */
public class FunctionalStringTokenizer {
	/**
	 * Create a new tokenizer from the specified string.
	 * 
	 * @param strang
	 *            The string to create a tokenizer from.
	 * @return A new tokenizer that splits the provided string on spaces.
	 */
	public static FunctionalStringTokenizer fromString(String strang) {
		if (strang == null) {
			throw new NullPointerException(
					"String to tokenize must be non-null");
		}

		return new FunctionalStringTokenizer(
				new StringTokenizer(strang, " "));
	}

	/**
	 * The string tokenizer being driven
	 */
	private StringTokenizer input;

	/**
	 * Create a functional string tokenizer from a given string
	 * 
	 * @param inp
	 *            The string to tokenize
	 */
	public FunctionalStringTokenizer(String inp) {
		if (inp == null) {
			throw new NullPointerException(
					"String to tokenize must be non-null");
		}

		this.input = new StringTokenizer(inp);
	}

	/**
	 * Create a functional string tokenizer from a given string and set of
	 * seperators
	 * 
	 * @param inputString
	 *            The string to tokenize
	 * @param seperators
	 *            The set of separating tokens to use for splitting
	 */
	public FunctionalStringTokenizer(String inputString,
			String seperators) {
		if (inputString == null) {
			throw new NullPointerException(
					"String to tokenize must not be null");
		} else if (seperators == null) {
			throw new NullPointerException(
					"Tokens to split on must not be null");
		}

		this.input = new StringTokenizer(inputString, seperators);
	}

	/**
	 * Create a functional string tokenizer from a non-functional one
	 * 
	 * @param toWrap
	 *            The non-functional string tokenizer to wrap
	 */
	public FunctionalStringTokenizer(StringTokenizer toWrap) {
		if (toWrap == null) {
			throw new NullPointerException(
					"Wrapped tokenizer must not be null");
		}

		this.input = toWrap;
	}

	/**
	 * Execute a provided action for each of the remaining tokens
	 * 
	 * @param action
	 *            The action to execute for each token
	 */
	public void forEachToken(Consumer<String> action) {
		if (action == null) {
			throw new NullPointerException("Action must not be null");
		}

		while (input.hasMoreTokens()) {
			action.accept(input.nextToken());
		}
	}

	/**
	 * Get the string tokenizer encapsuled by this tokenizer
	 * 
	 * @return The encapsulated tokenizer
	 */
	public StringTokenizer getInternal() {
		return input;
	}

	/**
	 * Check if this tokenizer has more tokens
	 * 
	 * @return Whether or not this tokenizer has more tokens
	 */
	public boolean hasMoreTokens() {
		return input.hasMoreTokens();
	}

	/**
	 * Return the next token from the tokenizer. Returns null if no more
	 * tokens are available
	 * 
	 * @return The next token from the tokenizer
	 */
	public String nextToken() {
		if (input.hasMoreTokens()) {
			// Return the next availible token
			return input.nextToken();
		}

		// Return no token
		return null;
	}

	/**
	 * Convert this tokenizer into a list of strings
	 * 
	 * @return This tokenizer, converted into a list of strings
	 */
	public IFunctionalList<String> toList() {
		return toList((String element) -> element);
	}

	/**
	 * Convert the contents of this tokenizer into a list. Consumes all of
	 * the input from this tokenizer.
	 * 
	 * @param <E>
	 *            The type of the converted tokens
	 * 
	 * @param tokenTransformer
	 *            The function to use to convert tokens.
	 * @return A list containing all of the converted tokens.
	 */
	public <E> IFunctionalList<E> toList(
			Function<String, E> tokenTransformer) {
		if (tokenTransformer == null) {
			throw new NullPointerException("Transformer must not be null");
		}

		IFunctionalList<E> returnList = new FunctionalList<>();

		// Add each token to the list after transforming it
		forEachToken(token -> {
			E transformedToken = tokenTransformer.apply(token);

			returnList.add(transformedToken);
		});

		return returnList;
	}
}
