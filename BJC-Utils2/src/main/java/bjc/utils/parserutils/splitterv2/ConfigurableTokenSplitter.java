package bjc.utils.parserutils.splitterv2;

import bjc.utils.funcdata.IList;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static bjc.utils.PropertyDB.applyFormat;

/**
 * Split a string into pieces around a regular expression, and offer an easy way
 * to configure the regular expression.
 * 
 * @author EVE
 *
 */
public class ConfigurableTokenSplitter extends SimpleTokenSplitter {
	private Set<String>	simpleDelimiters;
	private Set<String>	multipleDelimiters;
	private Set<String>	rRawDelimiters;

	/**
	 * Create a new token splitter with blank configuration.
	 * 
	 * @param keepDelims
	 *                Whether or not to keep delimiters.
	 */
	public ConfigurableTokenSplitter(boolean keepDelims) {
		super(null, keepDelims);

		/*
		 * Use linked hash-sets to keep items in insertion order.
		 */
		simpleDelimiters = new LinkedHashSet<>();
		multipleDelimiters = new LinkedHashSet<>();
		rRawDelimiters = new LinkedHashSet<>();
	}

	/**
	 * Add a set of simple delimiters to this splitter.
	 * 
	 * Simple delimiters match one occurrence of themselves as literals.
	 * 
	 * @param simpleDelims
	 *                The simple delimiters to add.
	 */
	public void addSimpleDelimiters(String... simpleDelims) {
		for(String simpleDelim : simpleDelims) {
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
	public void addMultiDelimiters(String... multiDelims) {
		for(String multiDelim : multiDelims) {
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
	public void addRawDelimiters(String... rRawDelims) {
		for(String rRawDelim : rRawDelims) {
			rRawDelimiters.add(rRawDelim);
		}
	}

	/**
	 * Take the configuration and compile it into a regular expression to
	 * use when splitting.
	 */
	public void compile() {
		StringBuilder rPattern = new StringBuilder();

		for(String rRawDelimiter : rRawDelimiters) {
			rPattern.append(applyFormat("rawDelim", rRawDelimiter));
		}

		for(String multipleDelimiter : multipleDelimiters) {
			rPattern.append(applyFormat("multipleDelim", multipleDelimiter));
		}

		for(String simpleDelimiter : simpleDelimiters) {
			rPattern.append(applyFormat("simpleDelim", simpleDelimiter));
		}

		rPattern.deleteCharAt(rPattern.length() - 1);

		spliter = Pattern.compile(rPattern.toString());
	}

	@Override
	public IList<String> split(String input) {
		if(spliter == null) {
			throw new IllegalStateException("Must compile splitter before use");
		}

		return super.split(input);
	}

	@Override
	public String toString() {
		String fmt = "ConfigurableTokenSplitter [simpleDelimiters=%s, multipleDelimiters=%s,"
				+ " rRawDelimiters=%s, spliter=%s]";

		return String.format(fmt, simpleDelimiters, multipleDelimiters, rRawDelimiters, spliter);
	}
}
