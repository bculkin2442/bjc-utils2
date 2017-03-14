package bjc.utils.examples.rangen;

import bjc.utils.funcdata.FunctionalStringTokenizer;
import bjc.utils.funcdata.IList;
import bjc.utils.gen.WeightedGrammar;

/**
 * Example showing how to use the weighted random number generator.
 *
 * @author ben
 *
 */
public class DiabloItemGen {
	private static WeightedGrammar<String> rules = new WeightedGrammar<>();

	private static void addCase(String ruleName, int probability, String ruleParts) {
		IList<String> parts = FunctionalStringTokenizer.fromString(ruleParts)
			.toList(strang -> strang);

		rules.addCase(ruleName, probability, parts);
	}

	private static void addInfixRules() {
		String rn = "<infix>";

		addCase(rn, 60, "sword");
		addCase(rn, 50, "armor");
		addCase(rn, 40, "rune");
		addCase(rn, 30, "scroll");
		addCase(rn, 20, "potion");
		addCase(rn, 10, "helm");
	}

	private static void addItemRules() {
		String rn = "<item>";

		addCase(rn, 10, "<infix>");
		addCase(rn, 20, "<prefix> <infix>");
		addCase(rn, 30, "<infix> <suffix>");
		addCase(rn, 40, "<prefix> <infix>  <suffix>");
		addCase(rn, 50, "<prefix> <prefix> <infix>");
		addCase(rn, 60, "<prefix> <prefix> <infix> <suffix>");
	}

	private static void addPrefixRules() {
		String rn = "<prefix>";

		addCase(rn, 60, "sturdy");
		addCase(rn, 50, "fine");
		addCase(rn, 40, "strong");
		addCase(rn, 30, "azure");
		addCase(rn, 20, "crimson");
		addCase(rn, 10, "phasing");
	}

	private static void addSuffixRules() {
		String rn = "<suffix>";

		addCase(rn, 60, "of Health");
		addCase(rn, 50, "of Wealth");
		addCase(rn, 40, "of Life");
		addCase(rn, 30, "of the Jackal");
		addCase(rn, 20, "of Vitality");
		addCase(rn, 10, "of Ability");
	}

	/**
	 * Main Method
	 *
	 * @param args
	 *                Unused CLI args
	 */
	public static void main(String[] args) {
		rules.addRule("<item>");
		addItemRules();

		rules.addRule("<suffix>");
		addSuffixRules();

		rules.addRule("<prefix>");
		addPrefixRules();

		rules.addRule("<infix>");
		addInfixRules();

		for(int i = 0; i < 100; i++) {
			IList<String> ls = rules.generateListValues("<item>", " ");

			StringBuilder sb = new StringBuilder();
			ls.forEach(sb::append);

			System.out.println(sb.toString().replaceAll("\\s+", " "));
		}
	}
}
