package bjc.utils.parserutils.splitter;

import static bjc.utils.misc.PropertyDB.applyFormat;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

import bjc.funcdata.IList;

/**
 * Split a string into pieces around a regular expression, and offer an easy way
 * to configure the regular expression.
 *
 * @author EVE
 *
 */
public class ConfigurableTokenSplitter extends SimpleTokenSplitter {
	private final Set<String>	simpleDelimiters;
	private final Set<String>	multipleDelimiters;
	private final Set<String>	rRawDelimiters;

	/**
	 * Create a new token splitter with blank configuration.
	 *
	 * @param keepDelims
	 *                Whether or not to keep delimiters.
	 */
	public ConfigurableTokenSplitter(final boolean keepDelims) {
		super(null, keepDelims);

		/* Use linked hash-sets to keep items in insertion order. */
		simpleDelimiters = new LinkedHashSet<>();
		multipleDelimiters = new LinkedHashSet<>();
		rRawDelimiters = new LinkedHashSet<>();
	}

	private ConfigurableTokenSplitter(boolean keepDelims, Set<String> simpleDelimiters,
			Set<String> multipleDelimiters, Set<String> rRawDelimiters) {
		super(null, keepDelims);

		this.simpleDelimiters = simpleDelimiters;
		this.multipleDelimiters = multipleDelimiters;
		this.rRawDelimiters = rRawDelimiters;
	}

	/**
	 * Add a set of simple delimiters to this splitter.
	 *
	 * Simple delimiters match one occurrence of themselves as literals.
	 *
	 * @param simpleDelims
	 *                The simple delimiters to add.
	 */
	public void addSimpleDelimiters(final String... simpleDelims) {
		for (final String simpleDelim : simpleDelims) {
			simpleDelimiters.add(simpleDelim);
		}
	}

	/**
	 * Add a set of multiple delimiters to this splitter.
	 *
	 * Multiple delimiters match one or more occurrences of themselves as
	 * literals.
	 *
	 * @param multiDelims
	 *                The multiple delimiters to add.
	 */
	public void addMultiDelimiters(final String... multiDelims) {
		for (final String multiDelim : multiDelims) {
			multipleDelimiters.add(multiDelim);
		}
	}

	/**
	 * Add a set of raw delimiters to this splitter.
	 *
	 * Raw delimiters match one occurrence of themselves as regular
	 * expressions.
	 *
	 * @param rRawDelims
	 *                The raw delimiters to add.
	 */
	public void addRawDelimiters(final String... rRawDelims) {
		for (final String rRawDelim : rRawDelims) {
			rRawDelimiters.add(rRawDelim);
		}
	}

	/**
	 * Take the configuration and compile it into a regular expression to
	 * use when splitting.
	 */
	public void compile() {
		final StringBuilder rPattern = new StringBuilder();

		for (final String rRawDelimiter : rRawDelimiters) {
			rPattern.append(applyFormat("rawDelim", rRawDelimiter));
		}

		for (final String multipleDelimiter : multipleDelimiters) {
			rPattern.append(applyFormat("multipleDelim", multipleDelimiter));
		}

		for (final String simpleDelimiter : simpleDelimiters) {
			rPattern.append(applyFormat("simpleDelim", simpleDelimiter));
		}

		rPattern.deleteCharAt(rPattern.length() - 1);

		spliter = Pattern.compile(rPattern.toString());
	}

	@Override
	public IList<String> split(final String input) {
		if (spliter == null) throw new IllegalStateException("Must compile splitter before use");

		return super.split(input);
	}

	@Override
	public String toString() {
		final String fmt = "ConfigurableTokenSplitter [simpleDelimiters=%s, multipleDelimiters=%s,"
				+ " rRawDelimiters=%s, spliter=%s]";

		return String.format(fmt, simpleDelimiters, multipleDelimiters, rRawDelimiters, spliter);
	}

	/**
	 * Builder class for the configurable token splitter.
	 * 
	 * @author bjculkin
	 *
	 */
	public static class Builder {
		private ConfigurableTokenSplitter cts;

		/**
		 * Create a new splitter builder.
		 * 
		 * @param keepDelims
		 *                Whether or not to keep the delimited splitter.
		 */
		public Builder(boolean keepDelims) {
			cts = new ConfigurableTokenSplitter(keepDelims);
		}

		/**
		 * Add a set of simple delimiters.
		 * 
		 * @param strings
		 *                The simple delimiters to use.
		 * @return The builder, for chaining.
		 */
		public Builder simple(String... strings) {
			cts.addSimpleDelimiters(strings);

			return this;
		}

		/**
		 * Add a set of multiple delimiters.
		 * 
		 * @param strings
		 *                The multiple delimiters to use.
		 * @return The builder, for chaining.
		 */
		public Builder multiple(String... strings) {
			cts.addMultiDelimiters(strings);

			return this;
		}

		/**
		 * Add a set of raw delimiters.
		 * 
		 * @param strings
		 *                The raw delimiters to use.
		 * @return The builder, for chaining.
		 */
		public Builder raw(String... strings) {
			cts.addRawDelimiters(strings);

			return this;
		}

		/**
		 * Build the splitter.
		 * 
		 * @return The built splitter.
		 */
		public ConfigurableTokenSplitter build() {
			ConfigurableTokenSplitter ret = new ConfigurableTokenSplitter(cts.keepDelim,
					cts.simpleDelimiters, cts.multipleDelimiters, cts.rRawDelimiters);
			ret.compile();
			
			return ret;
		}
	}
}
