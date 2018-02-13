package bjc.utils.ioutils;

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
	 * Creates a pragma that takes a single integer argument
	 *
	 * @param <StateType>
	 *        The type of state that goes along with this pragma
	 * @param name
	 *        The name of this pragma, for error message purpose
	 * @param consumer
	 *        The function to invoke with the parsed integer
	 * @return A pragma that functions as described above.
	 */
	public static <StateType> BiConsumer<FunctionalStringTokenizer, StateType> buildInteger(final String name,
			final BiConsumer<Integer, StateType> consumer) {
		return (tokenizer, state) -> {
			/*
			 * Check our input is correct
			 */
			if(!tokenizer.hasMoreTokens()) {
				String fmt = "Pragma %s requires one integer argument";

				throw new PragmaFormatException(String.format(fmt, name));
			}

			/*
			 * Read the argument
			 */
			final String token = tokenizer.nextToken();

			try {
				/*
				 * Run the pragma
				 */
				consumer.accept(Integer.parseInt(token), state);
			} catch(final NumberFormatException nfex) {
				/*
				 * Tell the user their argument isn't correct
				 */
				String fmt = "Argument %s to %s pragma isn't a valid integer, and this pragma requires an integer argument.";

				final PragmaFormatException pfex = new PragmaFormatException(
						String.format(fmt, token, name));

				pfex.initCause(nfex);

				throw pfex;
			}
		};
	}

	/**
	 * Creates a pragma that takes any number of arguments and collapses
	 * them all into a single string
	 *
	 * @param <StateType>
	 *        The type of state that goes along with this pragma
	 * @param name
	 *        The name of this pragma, for error message purpose
	 * @param consumer
	 *        The function to invoke with the parsed string
	 * @return A pragma that functions as described above.
	 */
	public static <StateType> BiConsumer<FunctionalStringTokenizer, StateType> buildStringCollapser(
			final String name, final BiConsumer<String, StateType> consumer) {
		return (tokenizer, state) -> {
			/*
			 * Check our input
			 */
			if(!tokenizer.hasMoreTokens()) {
				String fmt = "Pragma %s requires one or more string arguments.";

				throw new PragmaFormatException(String.format(fmt, name));
			}

			/*
			 * Build our argument
			 */
			final String collapsed = ListUtils.collapseTokens(tokenizer.toList());

			/*
			 * Run the pragma
			 */
			consumer.accept(collapsed, state);
		};
	}
}
