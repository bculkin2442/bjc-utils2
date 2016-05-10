package bjc.utils.parserutils;

import java.util.function.BiConsumer;

import bjc.utils.exceptions.PragmaFormatException;
import bjc.utils.funcdata.FunctionalStringTokenizer;
import bjc.utils.funcutils.ListUtils;

/**
 * Contains factory methods for common pragma types
 * 
 * @author ben
 *
 */
public class RuleBasedReaderPragmas {

	/**
	 * Creates a pragma that takes any number of arguments and collapses
	 * them all into a single string
	 * 
	 * @param <StateType>
	 *            The type of state that goes along with this pragma
	 * @param name
	 *            The name of this pragma, for error message purpose
	 * @param consumer
	 *            The function to invoke with the parsed string
	 * @return A pragma that functions as described above.
	 */
	public static <StateType>
			BiConsumer<FunctionalStringTokenizer, StateType>
			buildStringCollapser(String name,
					BiConsumer<String, StateType> consumer) {
		return (tokenizer, state) -> {
			if (!tokenizer.hasMoreTokens()) {
				throw new PragmaFormatException("Pragma " + name
						+ " requires one string argument");
			}

			consumer.accept(ListUtils.collapseTokens(
					tokenizer.toList((strang) -> strang)), state);
		};
	}

	/**
	 * Creates a pragma that takes a single integer argument
	 * 
	 * @param <StateType>
	 *            The type of state that goes along with this pragma
	 * @param name
	 *            The name of this pragma, for error message purpose
	 * @param consumer
	 *            The function to invoke with the parsed integer
	 * @return A pragma that functions as described above.
	 */
	public static <StateType>
			BiConsumer<FunctionalStringTokenizer, StateType> buildInteger(
					String name, BiConsumer<Integer, StateType> consumer) {
		return (tokenizer, state) -> {
			if (!tokenizer.hasMoreTokens()) {
				throw new PragmaFormatException("Pragma " + name
						+ " requires one integer argument");
			}

			String token = tokenizer.nextToken();

			try {
				consumer.accept(Integer.parseInt(token), state);
			} catch (NumberFormatException nfex) {
				PragmaFormatException pfex =
						new PragmaFormatException("Argument " + token
								+ " to version pragma isn't a valid integer. "
								+ "This pragma requires a integer argument");

				pfex.initCause(nfex);

				throw pfex;
			}
		};
	}

}
