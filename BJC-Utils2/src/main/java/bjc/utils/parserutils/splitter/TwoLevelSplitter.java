package bjc.utils.parserutils.splitter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Implementation of a splitter that runs in two passes.
 * 
 * This is useful because {@link SimpleTokenSplitter} doesn't like handling both
 * <= and = without mangling them.
 * 
 * The first pass splits on compound operators, which are built up from simple
 * operators.
 * 
 * The second pass removes simple operators.
 * 
 * @author EVE
 *
 */
@Deprecated
public class TwoLevelSplitter implements TokenSplitter {
	private SimpleTokenSplitter	high;
	private SimpleTokenSplitter	low;

	/**
	 * Create a new two level splitter.
	 */
	public TwoLevelSplitter() {
		high = new SimpleTokenSplitter();
		low = new SimpleTokenSplitter();
	}

	@Override
	public String[] split(String inp) {
		List<String> ret = new ArrayList<>();

		String[] partials = high.split(inp);

		for(String partial : partials) {
			String[] finals = low.split(partial);

			for(String fin : finals) {
				ret.add(fin);
			}
		}

		return ret.toArray(new String[ret.size()]);
	}

	/**
	 * Adds compound operators to split on.
	 * 
	 * @param delims
	 *                The compound operators to split on.
	 */
	public void addCompoundDelim(String... delims) {
		for(String delim : delims) {
			high.addDelimiter(delim);

			low.addNonMatcher(Pattern.quote(delim));
		}
	}

	/**
	 * Adds simple operators to split on.
	 * 
	 * @param delims
	 *                The simple operators to split on.
	 */
	public void addSimpleDelim(String... delims) {
		for(String delim : delims) {
			low.addDelimiter(delim);
		}
	}

	/**
	 * Adds repeated compound operators to split on.
	 * 
	 * @param delims
	 *                The repeated compound operators to split on.
	 */
	public void addCompoundMulti(String... delims) {
		for(String delim : delims) {
			high.addMultiDelimiter(delim);

			low.addNonMatcher("(?:" + delim + ")+");
		}
	}

	/**
	 * Adds simple compound operators to split on.
	 * 
	 * @param delims
	 *                The repeated simple operators to split on.
	 */
	public void addSimpleMulti(String... delims) {
		for(String delim : delims) {
			low.addMultiDelimiter(delim);
		}
	}

	/**
	 * Exclude strings matching a regex from both splits.
	 *
	 * @param exclusions
	 *                The regexes to exclude matches for.
	 */
	public void exclude(String... exclusions) {
		for(String exclusion : exclusions) {
			high.addNonMatcher(exclusion);

			low.addNonMatcher(exclusion);
		}
	}

	/**
	 * Ready the splitter for use.
	 */
	public void compile() {
		high.compile();

		low.compile();
	}
}
