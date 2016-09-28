package bjc.utils.parserutils;

import java.io.InputStream;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import bjc.utils.data.IHolder;
import bjc.utils.data.IPair;
import bjc.utils.data.Identity;
import bjc.utils.data.Pair;
import bjc.utils.exceptions.UnknownPragmaException;
import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.FunctionalStringTokenizer;
import bjc.utils.funcdata.IMap;

/**
 * This class parses a rules based config file, and uses it to drive a
 * provided set of actions
 * 
 * @author ben
 *
 * @param <E>
 *            The type of the state object to use
 * 
 */
public class RuleBasedConfigReader<E> {
	private BiConsumer<FunctionalStringTokenizer, IPair<String,
			E>>														startRule;
	private BiConsumer<FunctionalStringTokenizer,
			E>														continueRule;
	private Consumer<
			E>														endRule;

	private IMap<String, BiConsumer<FunctionalStringTokenizer,
			E>>														pragmas;

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
			BiConsumer<FunctionalStringTokenizer,
					IPair<String, E>> startRule,
			BiConsumer<FunctionalStringTokenizer, E> continueRule,
			Consumer<E> endRule) {
		this.startRule = startRule;
		this.continueRule = continueRule;
		this.endRule = endRule;

		this.pragmas = new FunctionalMap<>();
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
		if (pragmaName == null) {
			throw new NullPointerException("Pragma name must not be null");
		} else if (pragmaAction == null) {
			throw new NullPointerException(
					"Pragma action must not be null");
		}

		pragmas.put(pragmaName, pragmaAction);
	}

	private void continueRule(E state, boolean ruleOpen, String line) {
		if (ruleOpen == false) {
			throw new InputMismatchException(
					"Can't continue rule with no rule currently open");
		}

		if (continueRule == null) {
			throw new InputMismatchException(
					"Attempted to continue rule with rule continuation disabled."
							+ " Check for extraneous tabs");
		}

		continueRule.accept(
				new FunctionalStringTokenizer(line.substring(1), " "),
				state);
	}

	private boolean endRule(E state, boolean ruleOpen) {
		if (ruleOpen == false) {
			// Ignore blank line without an open rule
		} else {
			if (endRule == null) {
				// Nothing happens on rule end
				ruleOpen = false;
			} else {
				endRule.accept(state);
			}

			ruleOpen = false;
		}
		return ruleOpen;
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
		if (inputStream == null) {
			throw new NullPointerException(
					"Input stream must not be null");
		}

		E state;

		try (Scanner inputSource = new Scanner(inputStream, "\n")) {

			state = initialState;
			IHolder<Boolean> ruleOpen = new Identity<>(false);

			inputSource.forEachRemaining((line) -> {
				if (line.startsWith("#") || line.startsWith("//")) {
					// It's a comment
					return;
				} else if (line.equals("")) {
					ruleOpen.replace(endRule(state, ruleOpen.getValue()));

					return;
				} else if (line.startsWith("\t")) {
					continueRule(state, ruleOpen.getValue(), line);
				} else {
					ruleOpen.replace(
							startRule(state, ruleOpen.getValue(), line));
				}
			});
		}

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
	public void setStartRule(BiConsumer<FunctionalStringTokenizer,
			IPair<String, E>> startRule) {
		if (startRule == null) {
			throw new NullPointerException(
					"Action on rule start must be non-null");
		}

		this.startRule = startRule;
	}

	private boolean startRule(E state, boolean ruleOpen, String line) {
		FunctionalStringTokenizer tokenizer = new FunctionalStringTokenizer(
				line, " ");

		String nextToken = tokenizer.nextToken();

		if (nextToken.equals("pragma")) {
			String token = tokenizer.nextToken();

			pragmas.getOrDefault(token, (tokenzer, stat) -> {
				throw new UnknownPragmaException(
						"Unknown pragma " + token);
			}).accept(tokenizer, state);
		} else {
			if (ruleOpen == true) {
				throw new InputMismatchException("Attempted to open a"
						+ " rule with a rule already open. Make sure rules are"
						+ " seperated by blank lines");
			}

			startRule.accept(tokenizer, new Pair<>(nextToken, state));
			ruleOpen = true;
		}
		return ruleOpen;
	}
}
