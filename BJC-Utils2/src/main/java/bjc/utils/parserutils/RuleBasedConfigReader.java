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
	// Function to execute when starting a rule
	// Takes the tokenizer, and a pair of the read token and application state
	private BiConsumer<FunctionalStringTokenizer, IPair<String,
			E>>														startRule;
	// Function to use when continuing a rule
	// Takes a tokenizer and application state
	private BiConsumer<FunctionalStringTokenizer,
			E>														continueRule;
	// Function to use when ending a rule
	// Takes an application state
	private Consumer<
			E>														endRule;

	// Map of pragma names to pragma actions
	// Pragma actions are functions taking a tokenizer and application state
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
		// Make sure our input is correct
		if (ruleOpen == false) {
			throw new InputMismatchException(
					"Can't continue rule with no rule currently open");
		} else if (continueRule == null) {
			throw new InputMismatchException(
					"Attempted to continue rule with rule continuation disabled."
							+ " Check for extraneous tabs");
		}

		// Accept the rule
		continueRule.accept(
				new FunctionalStringTokenizer(line.substring(1), " "),
				state);
		}

	private boolean endRule(E state, boolean ruleOpen) {
		// Ignore blank line without an open rule
		if (ruleOpen == false) {
			// Do nothing
			return false;
		} else {
			// Nothing happens on rule end
			if (endRule != null) {
				// Process the rule ending
				endRule.accept(state);
			}

			// Return a closed rule
			return false;
		}
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

		// Application state: We're giving this back later
		E state = initialState;

		// Prepare our input source
		try (Scanner inputSource = new Scanner(inputStream, "\n")) {
			// This is true when a rule's open
			IHolder<Boolean> ruleOpen = new Identity<>(false);

			// Do something for every line of the file
			inputSource.forEachRemaining((line) -> {
				// Skip comment lines
				if (line.startsWith("#") || line.startsWith("//")) {
					// It's a comment
					return;
				} else if (line.equals("")) {
					// End the rule
					ruleOpen.replace(endRule(state, ruleOpen.getValue()));
				} else if (line.startsWith("\t")) {
					// Continue the rule
					continueRule(state, ruleOpen.getValue(), line);
				} else {
					// Open a rule
					ruleOpen.replace(
							startRule(state, ruleOpen.getValue(), line));
				}
			});
		}

		// Return the state that the user has created
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
		// Create the line tokenizer
		FunctionalStringTokenizer tokenizer = new FunctionalStringTokenizer(
				line, " ");

		// Get the initial token
		String nextToken = tokenizer.nextToken();

		// Handle pragmas
		if (nextToken.equals("pragma")) {
			// Get the pragma name
			String token = tokenizer.nextToken();

			// Handle pragmas
			pragmas.getOrDefault(token, (tokenzer, stat) -> {
				throw new UnknownPragmaException(
						"Unknown pragma " + token);
			}).accept(tokenizer, state);
		} else {
			// Make sure input is correct
			if (ruleOpen == true) {
				throw new InputMismatchException("Attempted to open a"
						+ " rule with a rule already open. Make sure rules are"
						+ " seperated by blank lines");
			}

			// Start a rule
			startRule.accept(tokenizer, new Pair<>(nextToken, state));

			ruleOpen = true;
		}

		return ruleOpen;
	}
}
