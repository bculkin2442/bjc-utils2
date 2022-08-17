package bjc.utils.parserutils.pattern;

import java.util.StringJoiner;
import java.util.function.Supplier;
import java.util.regex.*;

/**
 * Builder interface for regex patterns.
 * 
 * @author bjculkin
 *
 */
public interface PatternPart {
	/**
	 * Convert this pattern part into a regex.
	 * 
	 * @return The regex this part represents.
	 */
	public String toRegex();
	
	public boolean canOptimize();
	
	static PatternPart part(boolean canOptimize, Supplier<String> func) {
		return new PatternPart() {
			
			@Override
			public String toRegex() {
				return func.get();
			}
			
			@Override
			public boolean canOptimize() {
				return canOptimize;
			}
		};
	}
	
	static PatternPart var(Supplier<String> source) {
		return part(false, source);
	}
	/**
	 * Create a 'raw' pattern part, which just echoes the given string.
	 * 
	 * @param str The regex to include
	 * 
	 * @return A pattern part which converts to the given string.
	 */
	static PatternPart raw(String str) {
		return part(true, () -> str);
	}
	
	static PatternPart joining(String joiner, PatternPart... parts) {
		return new PatternPart() {
			
			@Override
			public String toRegex() {
				StringJoiner sj = new StringJoiner(joiner);
				for (PatternPart part : parts) sj.add(part.toRegex());
				return sj.toString();
			}
			
			@Override
			public boolean canOptimize() {
				for (PatternPart part : parts)
					if (!part.canOptimize()) return false;
				
				return true;
			}
		};
	}
	/**
	 * Create a pattern part which matches the given string.
	 * 
	 * @param str The string to match
	 * 
	 * @return A pattern which matches the given string.
	 */
	static PatternPart literal(String str) {
		return part(true, () -> Pattern.quote(str));
	}
	
	/**
	 * Create a pattern part which matches a single digit.
	 * 
	 * @return A pattern that matches a digit.
	 */
	static PatternPart digit() {
		return raw("\\d");
	}
	
	static PatternPart cclass(char... chars) {
		return part(true, () -> {
			StringBuilder sb = new StringBuilder("[");
			for (char ch : chars) sb.append(ch);
			sb.append("]");
			return sb.toString();
		});
	}
	
	static PatternPart notCClass(char... chars) {
		return part(true, () -> {
			StringBuilder sb = new StringBuilder("[^");
			for (char ch : chars) sb.append(ch);
			sb.append("]");
			return sb.toString();
		});
	}
	
	static PatternPart nonspace() {
		return raw("\\S");
	}
	
	static PatternPart concat(PatternPart... parts) {
		return joining(" ", parts);
	}
	
	static PatternPart alternate(PatternPart... parts) {
		return joining("|", parts);
	}
	
	static PatternPart repeat(PatternPart part) {
		return part(part.canOptimize(), () -> part.toRegex() + "*");
	}
	
	static PatternPart optional(PatternPart part) {
		return part(part.canOptimize(), () -> part.toRegex() + "?");
	}
	
	static PatternPart repeatAtLeastOnce(PatternPart part) {
		return part(part.canOptimize(), () -> part.toRegex() + "*");
	}
	
	static PatternPart surround(String lhs, String rhs, PatternPart part) {
		return part(part.canOptimize(), () -> lhs + part.toRegex() + rhs);
	}
	static PatternPart group(PatternPart part) {
		return surround("(", ")", part);
	}

	static PatternPart namedGroup(String groupName, PatternPart part) {
		return surround("(<" + groupName + ">", ")", part);
	}
	
	static PatternPart nonCaptureGroup(PatternPart part) {
		return surround("(?:", ")", part);
	}
}
