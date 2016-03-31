package bjc.utils.parserutils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import bjc.utils.data.Pair;
import bjc.utils.exceptions.UnknownPragmaException;
import bjc.utils.funcdata.FunctionalStringTokenizer;

/**
 * This class parses a rules based config file, and uses it to drive a
 * provided set of actions
 * 
 * @author ben
 *
 * @param <E>
 *            The type of the state object to use
 */
public class RuleBasedConfigReader<E> {
	private BiConsumer<FunctionalStringTokenizer, Pair<String, E>>	startRule;
	private BiConsumer<FunctionalStringTokenizer, E>				continueRule;
	private Consumer<E>												endRule;

	private Map<String, BiConsumer<FunctionalStringTokenizer, E>>	pragmas;

	/**
	 * Create a new rule-based config reader
	 * 
	 * @param startRule
	 *            The action to fire when starting a rule
	 * @param continueRule
	 *            The action to fire when continuing a rule
	 * @param endRule
	 *            The action to fire when ending a rule
	 */
	public RuleBasedConfigReader(
			BiConsumer<FunctionalStringTokenizer, Pair<String, E>> startRule,
			BiConsumer<FunctionalStringTokenizer, E> continueRule,
			Consumer<E> endRule) {
		this.startRule = startRule;
		this.continueRule = continueRule;
		this.endRule = endRule;

		this.pragmas = new HashMap<>();
	}

	/**
	 * Add a pragma to this reader
	 * 
	 * @param pragmaName
	 *            The name of the pragma to add
	 * @param pragmaAction
	 *            The function to execute when this pragma is read
	 */
	public void addPragma(String pragmaName,
			BiConsumer<FunctionalStringTokenizer, E> pragmaAction) {
		pragmas.put(pragmaName, pragmaAction);
	}

	/**
	 * Run a stream through this reader
	 * 
	 * @param inputStream
	 *            The stream to get input
	 * @param initialState
	 *            The initial state of the reader
	 * @return The final state of the reader
	 */
	public E fromStream(InputStream inputStream, E initialState) {
		Scanner inputSource = new Scanner(inputStream);

		E state = initialState;

		while (inputSource.hasNextLine()) {
			String line = inputSource.nextLine();

			if (line.equals("")) {
				endRule.accept(state);
				continue;
			} else if (line.startsWith("\t")) {
				continueRule.accept(new FunctionalStringTokenizer(
						line.substring(1), " "), state);
			} else {
				FunctionalStringTokenizer tokenizer =
						new FunctionalStringTokenizer(line, " ");

				String nextToken = tokenizer.nextToken();
				if (nextToken.equals("#")) {
					// Do nothing, this is a comment
				} else if (nextToken.equals("pragma")) {
					String token = tokenizer.nextToken();

					pragmas.getOrDefault(token, (tokenzer, stat) -> {
						throw new UnknownPragmaException(
								"Unknown pragma " + token);
					}).accept(tokenizer, state);
				} else {
					startRule.accept(tokenizer,
							new Pair<>(nextToken, state));
				}
			}
		}

		inputSource.close();

		return state;
	}

	/**
	 * Set the action to execute when continuing a rule
	 * 
	 * @param continueRule
	 *            The action to execute on continuation of a rule
	 */
	public void setContinueRule(
			BiConsumer<FunctionalStringTokenizer, E> continueRule) {
		this.continueRule = continueRule;
	}

	/**
	 * Set the action to execute when ending a rule
	 * 
	 * @param endRule
	 *            The action to execute on ending of a rule
	 */
	public void setEndRule(Consumer<E> endRule) {
		this.endRule = endRule;
	}

	/**
	 * Set the action to execute when starting a rule
	 * 
	 * @param startRule
	 *            The action to execute on starting of a rule
	 */
	public void setStartRule(
			BiConsumer<FunctionalStringTokenizer, Pair<String, E>> startRule) {
		this.startRule = startRule;
	}
}
