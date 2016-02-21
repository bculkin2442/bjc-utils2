package bjc.utils.parserutils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import bjc.utils.data.Pair;
import bjc.utils.funcdata.FunctionalStringTokenizer;

/**
 * This class parses a rules based config file, and uses it to drive a
 * provided set of actions
 * 
 * @author ben
 *
 * @param <E>The
 *            type of the state object to use
 */
public class RuleBasedConfigReader<E> {
	private Map<String, BiConsumer<FunctionalStringTokenizer, E>>	pragmas;

	private BiConsumer<FunctionalStringTokenizer, Pair<String, E>>	startRule;
	private BiConsumer<FunctionalStringTokenizer, E>				continueRule;
	private Consumer<E>												endRule;

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
				FunctionalStringTokenizer stk = new FunctionalStringTokenizer(
						ln, " ");

				String nxtToken = stk.nextToken();
				if (nxtToken.equals("#")) {

				} else if (nxtToken.equals("pragma")) {
					String tk = stk.nextToken();

					pragmas.getOrDefault(tk, (strk, ras) -> {
						throw new UnknownPragmaException(
								"Unknown pragma " + tk);
					}).accept(stk, stat);
				} else {
					startRule.accept(stk,
							new Pair<String, E>(nxtToken, stat));
				}
			}
		}

		scn.close();

		return stat;
	}

	public void addPragma(String pragName,
			BiConsumer<FunctionalStringTokenizer, E> pragAct) {
		pragmas.put(pragName, pragAct);
	}

	public void setStartRule(
			BiConsumer<FunctionalStringTokenizer, Pair<String, E>> startRule) {
		this.startRule = startRule;
	}

	public void setContinueRule(
			BiConsumer<FunctionalStringTokenizer, E> continueRule) {
		this.continueRule = continueRule;
	}

	public void setEndRule(Consumer<E> endRule) {
		this.endRule = endRule;
	}
}
