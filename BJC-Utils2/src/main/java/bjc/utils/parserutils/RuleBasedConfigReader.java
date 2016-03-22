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
	 * @param pragName
	 *            The name of the pragma to add
	 * @param pragAct
	 *            The function to execute when this pragma is read
	 */
	public void addPragma(String pragName,
			BiConsumer<FunctionalStringTokenizer, E> pragAct) {
		pragmas.put(pragName, pragAct);
	}

	/**
	 * Run a stream through this reader
	 * 
	 * @param is
	 *            The stream to get input
	 * @param initState
	 *            The initial state of the reader
	 * @return The final state of the reader
	 */
	public E fromStream(InputStream is, E initState) {
		Scanner scn = new Scanner(is);

		E stat = initState;

		while (scn.hasNextLine()) {
			String ln = scn.nextLine();

			if (ln.equals("")) {
				endRule.accept(stat);
				continue;
			} else if (ln.startsWith("\t")) {
				continueRule.accept(new FunctionalStringTokenizer(
						ln.substring(1), " "), stat);
			} else {
				FunctionalStringTokenizer stk =
						new FunctionalStringTokenizer(ln, " ");

				String nxtToken = stk.nextToken();
				if (nxtToken.equals("#")) {
					// Do nothing, this is a comment
				} else if (nxtToken.equals("pragma")) {
					String tk = stk.nextToken();

					pragmas.getOrDefault(tk, (strk, ras) -> {
						throw new UnknownPragmaException(
								"Unknown pragma " + tk);
					}).accept(stk, stat);
				} else {
					startRule.accept(stk, new Pair<>(nxtToken, stat));
				}
			}
		}

		scn.close();

		return stat;
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
