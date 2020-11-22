package bjc.utils.ioutils;

import java.io.InputStream;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import bjc.data.IHolder;
import bjc.data.IPair;
import bjc.data.Identity;
import bjc.data.Pair;
import bjc.utils.exceptions.UnknownPragma;
import bjc.funcdata.FunctionalMap;
import bjc.funcdata.FunctionalStringTokenizer;
import bjc.funcdata.IMap;

/**
 * This class parses a rules based config file, and uses it to drive a provided
 * set of actions
 *
 * @author ben
 *
 * @param <E>
 *            The type of the state object to use
 *
 */
public class RuleBasedConfigReader<E> {
	/*
	 * Function to execute when starting a rule.
	 *
	 * Takes the tokenizer, and a pair of the read token and application state
	 */
	private BiConsumer<FunctionalStringTokenizer, IPair<String, E>> start;

	/*
	 * Function to use when continuing a rule.
	 *
	 * Takes a tokenizer and application state
	 */
	private BiConsumer<FunctionalStringTokenizer, E> continueRule;

	/*
	 * Function to use when ending a rule.
	 *
	 * Takes an application state
	 */
	private Consumer<E> end;

	/*
	 * Map of pragma names to pragma actions.
	 *
	 * Pragma actions are functions taking a tokenizer and application state
	 */
	private final IMap<String, BiConsumer<FunctionalStringTokenizer, E>> pragmas;

	/**
	 * Create a new rule-based config reader
	 *
	 * @param start
	 *                     The action to fire when starting a rule
	 * @param continueRule
	 *                     The action to fire when continuing a rule
	 * @param end
	 *                     The action to fire when ending a rule
	 */
	public RuleBasedConfigReader(
			final BiConsumer<FunctionalStringTokenizer, IPair<String, E>> start,
			final BiConsumer<FunctionalStringTokenizer, E> continueRule,
			final Consumer<E> end) {
		this.start = start;
		this.continueRule = continueRule;
		this.end = end;

		this.pragmas = new FunctionalMap<>();
	}

	/**
	 * Add a pragma to this reader
	 *
	 * @param name
	 *               The name of the pragma to add
	 * @param action
	 *               The function to execute when this pragma is read
	 */
	public void addPragma(final String name,
			final BiConsumer<FunctionalStringTokenizer, E> action) {
		if (name == null)
			throw new NullPointerException("Pragma name must not be null");
		else if (action == null)
			throw new NullPointerException("Pragma action must not be null");

		pragmas.put(name, action);
	}

	private void continueRule(final E state, final boolean isRuleOpen,
			final String line) {
		// Make sure our input is correct
		if (isRuleOpen == false)
			throw new InputMismatchException("Cannot continue rule with no rule open");
		else if (continueRule == null)
			throw new InputMismatchException(
					"Rule continuation not supported for current grammar");

		/*
		 * Accept the rule
		 */
		continueRule.accept(new FunctionalStringTokenizer(line.substring(1), " "), state);
	}

	private boolean endRule(final E state, final boolean isRuleOpen) {
		/*
		 * Ignore blank line without an open rule
		 */
		if (isRuleOpen == false) {
			/*
			 * Do nothing
			 */
			return false;
		}

		/*
		 * Nothing happens on rule end
		 */
		if (end != null) {
			/*
			 * Process the rule ending
			 */
			end.accept(state);
		}

		/*
		 * Return a closed rule
		 */
		return false;
	}

	/**
	 * Run a stream through this reader
	 *
	 * @param input
	 *                     The stream to get input
	 * @param initialState
	 *                     The initial state of the reader
	 * @return The final state of the reader
	 */
	public E fromStream(final InputStream input, final E initialState) {
		if (input == null)
			throw new NullPointerException("Input stream must not be null");

		/*
		 * Application state: We're giving this back later
		 */
		final E state = initialState;

		/*
		 * Prepare our input source
		 */
		try (Scanner source = new Scanner(input)) {
			source.useDelimiter("\n");
			/*
			 * This is true when a rule's open
			 */
			final IHolder<Boolean> isRuleOpen = new Identity<>(false);

			/*
			 * Do something for every line of the file
			 */
			source.forEachRemaining(line -> {
				/*
				 * Skip comment lines
				 */
				if (line.startsWith("#") || line.startsWith("//"))
					/*
					 * It's a comment
					 */
					return;
				else if (line.equals("")) {
					/*
					 * End the rule
					 */
					isRuleOpen.replace(endRule(state, isRuleOpen.getValue()));
				} else if (line.startsWith("\t")) {
					/*
					 * Skip comment lines.
					 */
					if (line.startsWith("#") || line.startsWith("//"))
						/*
						 * It's a comment.
						 */
						return;

					/*
					 * Continue the rule
					 */
					continueRule(state, isRuleOpen.getValue(), line.substring(1));
				} else {
					/*
					 * Open a rule
					 */
					isRuleOpen.replace(startRule(state, isRuleOpen.getValue(), line));
				}
			});
		}

		/*
		 * Return the state that the user has created
		 */
		return state;
	}

	/**
	 * Set the action to execute when continuing a rule
	 *
	 * @param continueRule
	 *                     The action to execute on continuation of a rule
	 */
	public void
			setContinueRule(final BiConsumer<FunctionalStringTokenizer, E> continueRule) {
		this.continueRule = continueRule;
	}

	/**
	 * Set the action to execute when ending a rule
	 *
	 * @param end
	 *            The action to execute on ending of a rule
	 */
	public void setEndRule(final Consumer<E> end) {
		this.end = end;
	}

	/**
	 * Set the action to execute when starting a rule
	 *
	 * @param start
	 *              The action to execute on starting of a rule
	 */
	public void setStartRule(
			final BiConsumer<FunctionalStringTokenizer, IPair<String, E>> start) {
		if (start == null)
			throw new NullPointerException("Action on rule start must be non-null");

		this.start = start;
	}

	private boolean startRule(final E state, boolean isRulOpen, final String line) {
		boolean isRuleOpen = isRulOpen;
		
		/*
		 * Create the line tokenizer
		 */
		final FunctionalStringTokenizer tokenizer
				= new FunctionalStringTokenizer(line, " ");

		/*
		 * Get the initial token
		 */
		final String nextToken = tokenizer.nextToken();

		/*
		 * Handle pragmas
		 */
		if (nextToken.equals("pragma")) {
			/*
			 * Get the pragma name
			 */
			final String token = tokenizer.nextToken();

			/*
			 * Handle pragmas
			 */
			pragmas.get(token).orElse((tokenzer, stat) -> {
				throw new UnknownPragma("Unknown pragma " + token);
			}).accept(tokenizer, state);
		} else {
			/*
			 * Make sure input is correct
			 */
			if (isRuleOpen == true)
				throw new InputMismatchException(
						"Nested rules are currently not supported");

			/*
			 * Start a rule
			 */
			start.accept(tokenizer, new Pair<>(nextToken, state));

			isRuleOpen = true;
		}

		return isRuleOpen;
	}
}
