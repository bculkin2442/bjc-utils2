package bjc.utils.examples.gen;

import bjc.funcdata.FunctionalList;
import bjc.funcdata.FunctionalStringTokenizer;
import bjc.funcdata.ListEx;
import bjc.utils.gen.RandomGrammar;

/**
 * Examples of random grammar
 *
 * @author ben
 *
 */
public class RandomStringExamples {
	private static RandomGrammar<String> rg;

	private static void addRule(final String rule, final String... cases) {
		final ListEx<ListEx<String>> cses = new FunctionalList<>();

		for (final String strang : cases) {
			final ListEx<String> lst
					= FunctionalStringTokenizer.fromString(strang).toList(s -> s);

			cses.add(lst);
		}

		rg.makeRule(rule, cses);
	}

	/**
	 * Main method
	 *
	 * @param args
	 *             Unused CLI args
	 */
	public static void main(final String[] args) {
		rg = new RandomGrammar<>();

		addRule("<sentance>", "<person> <opines> <something>",
				"<person> thinks that I am <property>", "I <opine> <something>",
				"You think that I am <property>");

		addRule("<activity>", "dancing", "eating", "sleeping");

		addRule("<object>", "<person>", "life", "my computer", "my friends");

		addRule("<opine>", "hate", "am jealous of", "love");

		addRule("<opines>", "hates", "loves");

		addRule("<person>", "my sister", "my father", "my girlfriend",
				"the man next door");

		addRule("<property>", "creative", "intelligent");

		addRule("<something>", "<activity>", "<activity> with <person>", "<object>");

		for (int i = 0; i < 10; i++) {
			final ListEx<String> ls = rg.generateListValues("<sentance>", " ");

			final StringBuilder sb = new StringBuilder();
			ls.forEach(sb::append);

			System.out.println(sb.toString().replaceAll("\\s+", " "));
		}
	}
}
