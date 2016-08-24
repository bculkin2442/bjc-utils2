package bjc.utils.parserutils;

import java.io.InputStream;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
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
 * provided set of actions. It differs from {@link RuleBasedConfigReader}
 * in that it has support for subrules
 * 
 * @author ben
 *
 * @param <State>
 *            The type of the state object to use
 * @param <SubruleState>
 *            The type of state to use for subrules
 * 
 */
public class RuleStackBasedConfigReader<State, SubruleState> {
	private BiConsumer<FunctionalStringTokenizer, IPair<String, State>>	startRule;
	private BiConsumer<FunctionalStringTokenizer, State>				continueRule;
	private Consumer<State>												endRule;

	private Stack<SubruleState>											subruleStack;

	private BiFunction<FunctionalStringTokenizer, State, SubruleState>	startSubrule;
	private BiConsumer<FunctionalStringTokenizer, SubruleState>			continueSubrule;
	private BiConsumer<State, SubruleState>								endSubrule;

	private IMap<String, BiConsumer<FunctionalStringTokenizer, State>>	pragmas;

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
	public RuleStackBasedConfigReader(
			BiConsumer<FunctionalStringTokenizer, IPair<String, State>> startRule,
			BiConsumer<FunctionalStringTokenizer, State> continueRule,
			Consumer<State> endRule) {
		this.startRule = startRule;
		this.continueRule = continueRule;
		this.endRule = endRule;

		this.pragmas = new FunctionalMap<>();
	}

	public RuleStackBasedConfigReader() {
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
			BiConsumer<FunctionalStringTokenizer, State> pragmaAction) {
		if (pragmaName == null) {
			throw new NullPointerException("Pragma name must not be null");
		} else if (pragmaAction == null) {
			throw new NullPointerException(
					"Pragma action must not be null");
		}

		pragmas.put(pragmaName, pragmaAction);
	}

	private void continueRule(State state, boolean ruleOpen, String line) {
		if (ruleOpen == false) {
			throw new InputMismatchException(
					"Can't continue rule with no rule currently open");
		}

		if (continueRule == null) {
			throw new InputMismatchException(
					"Attempted to continue rule with rule continuation disabled."
							+ " Check for extraneous tabs");
		}

		String lineSansInitTab = line.substring(1);

		if (lineSansInitTab.startsWith("\t")) {
			// Do subrule stuff
			int subruleLevel;

			for (subruleLevel = 1; lineSansInitTab
					.charAt(subruleLevel) != '\t'; subruleLevel++) {
				// Count up subrule levels
			}

			if (subruleStack.size() + 1 < subruleLevel) {
				throw new InputMismatchException(
						"Attempted to start a nested subrule without starting its parent."
								+ " Check indentation, as subrules can only be started one at a time");
			} else if (subruleStack.size() + 1 == subruleLevel) {
				SubruleState subruleState = startSubrule.apply(
						new FunctionalStringTokenizer(lineSansInitTab
								.substring(subruleLevel - 1), " "),
						state);

				subruleStack.push(subruleState);
			} else if (subruleStack.size() == subruleLevel) {
				continueSubrule.accept(
						new FunctionalStringTokenizer(lineSansInitTab
								.substring(subruleLevel - 1), " "),
						subruleStack.peek());
			} else {
				while (subruleStack.size() != subruleLevel) {
					endSubrule.accept(state, subruleStack.pop());
				}

				continueSubrule.accept(
						new FunctionalStringTokenizer(lineSansInitTab
								.substring(subruleLevel - 1), " "),
						subruleStack.peek());
			}
		} else {
			if (!subruleStack.empty()) {
				while (!subruleStack.empty()) {
					endSubrule.accept(state, subruleStack.pop());
				}
			}

			continueRule.accept(
					new FunctionalStringTokenizer(lineSansInitTab, " "),
					state);
		}
	}

	private boolean endRule(State state, boolean ruleOpen) {
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
	public State fromStream(InputStream inputStream, State initialState) {
		if (inputStream == null) {
			throw new NullPointerException(
					"Input stream must not be null");
		}

		State state;

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
			BiConsumer<FunctionalStringTokenizer, State> continueRule) {
		this.continueRule = continueRule;
	}

	/**
	 * Set the action to execute when ending a rule
	 * 
	 * @param endRule
	 *            The action to execute on ending of a rule
	 */
	public void setEndRule(Consumer<State> endRule) {
		this.endRule = endRule;
	}

	/**
	 * Set the action to execute when starting a rule
	 * 
	 * @param startRule
	 *            The action to execute on starting of a rule
	 */
	public void setStartRule(
			BiConsumer<FunctionalStringTokenizer, IPair<String, State>> startRule) {
		if (startRule == null) {
			throw new NullPointerException(
					"Action on rule start must be non-null");
		}

		this.startRule = startRule;
	}

	private boolean startRule(State state, boolean ruleOpen, String line) {
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

	/**
	 * Set the function to use when a subrule starts
	 * 
	 * @param startSubrule
	 *            the function to use for starting a subrule
	 */
	public void setStartSubrule(
			BiFunction<FunctionalStringTokenizer, State, SubruleState> startSubrule) {
		this.startSubrule = startSubrule;
	}

	/**
	 * Set the function to use when a subrule continues
	 * 
	 * @param continueSubrule
	 *            the function to use for continuing a subrule
	 */
	public void setContinueSubrule(
			BiConsumer<FunctionalStringTokenizer, SubruleState> continueSubrule) {
		this.continueSubrule = continueSubrule;
	}

	/**
	 * Set the function to use when a subrule ends
	 * 
	 * @param endSubrule
	 *            the function to use for ending a subrule
	 */
	public void setEndSubrule(BiConsumer<State, SubruleState> endSubrule) {
		this.endSubrule = endSubrule;
	}
}
