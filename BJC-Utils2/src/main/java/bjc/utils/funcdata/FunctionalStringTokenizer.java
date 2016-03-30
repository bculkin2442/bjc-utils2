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
	 * @param s
	 *            The string to create a tokenizer from.
	 * @return A new tokenizer that splits the provided string on spaces.
	 */
	public static FunctionalStringTokenizer fromString(String s) {
		return new FunctionalStringTokenizer(new StringTokenizer(s, " "));
	}

	/**
	 * The string tokenizer being driven
	 */
	private StringTokenizer inp;

	/**
	 * Create a functional string tokenizer from a given string
	 * 
	 * @param inp
	 *            The string to tokenize
	 */
	public FunctionalStringTokenizer(String inp) {
		this.inp = new StringTokenizer(inp);
	}

	/**
	 * Create a functional string tokenizer from a given string and set of
	 * seperators
	 * 
	 * @param inp
	 *            The string to tokenize
	 * @param seps
	 *            The string to use for splitting
	 */
	public FunctionalStringTokenizer(String inp, String seps) {
		this.inp = new StringTokenizer(inp, seps);
	}

	/**
	 * Create a functional string tokenizer from a non-functional one
	 * 
	 * @param inp
	 *            The non-functional string tokenizer to wrap
	 */
	public FunctionalStringTokenizer(StringTokenizer inp) {
		this.inp = inp;
	}

	/**
	 * Execute a provided action for each of the remaining tokens
	 * 
	 * @param f
	 *            The action to execute for each token
	 */
	public void forEachToken(Consumer<String> f) {
		while (inp.hasMoreTokens()) {
			f.accept(inp.nextToken());
		}
	}

	/**
	 * Get the string tokenizer encapsuled by this
	 * 
	 * @return The encapsulated tokenizer
	 */
	public StringTokenizer getInternal() {
		return inp;
	}

	/**
	 * Return the next token from the tokenizer Returns null if no more
	 * tokens are available
	 * 
	 * @return The next token from the tokenizer
	 */
	public String nextToken() {
		return inp.hasMoreTokens() ? inp.nextToken() : null;
	}

	/**
	 * Convert the contents of this tokenizer into a list. Consumes all of
	 * the input from this tokenizer.
	 * 
	 * @param <E>
	 *            The type of the converted tokens
	 * 
	 * @param f
	 *            The function to use to convert tokens.
	 * @return A list containing all of the converted tokens.
	 */
	public <E> FunctionalList<E> toList(Function<String, E> f) {
		FunctionalList<E> r = new FunctionalList<>();

		forEachToken(tk -> r.add(f.apply(tk)));

		return r;
	}

	/**
	 * Check if this tokenizer has more tokens
	 * 
	 * @return Whether or not this tokenizer has more tokens
	 */
	public boolean hasMoreTokens() {
		return inp.hasMoreTokens();
	}
}
