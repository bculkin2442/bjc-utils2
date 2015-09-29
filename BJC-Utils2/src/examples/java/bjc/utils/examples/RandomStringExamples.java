package bjc.utils.examples;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.FunctionalStringTokenizer;
import bjc.utils.gen.RandomGrammar;

public class RandomStringExamples {
	private static RandomGrammar<String> rg;

	public static void main(String[] args) {
		rg = new RandomGrammar<>();

		addRule("<sentance>", "<person> <opines> <something>",
				"<person> thinks that I am <property>",
				"I <opine> <something>", "You think that I am <property>");

		addRule("<activity>", "dancing", "eating", "sleeping");

		addRule("<object>", "<person>", "life", "my computer",
				"my friends");

		addRule("<opine>", "hate", "am jealous of", "love");

		addRule("<opines>", "hates", "loves");

		addRule("<person>", "my sister", "my father", "my girlfriend",
				"the man next door");

		addRule("<property>", "creative", "intelligent");

		addRule("<something>", "<activity>", "<activity> with <person>",
				"<object>");

		for (int i = 0; i < 10; i++) {
			FunctionalList<String> ls = rg.genList("<sentance>", " ");

			StringBuilder sb = new StringBuilder();

			ls.forEach(sp -> sb.append(sp));

			System.out.println(sb.toString().replaceAll("\\s+", " "));
		}
	}

	private static void addRule(String rule, String... cases) {
		FunctionalList<FunctionalList<String>> cses = new FunctionalList<>();

		for (String strang : cases) {
			cses.add(FunctionalStringTokenizer.fromString(strang)
					.toList(s -> s));
		}

		rg.makeRule(rule, cses);
	}
}
