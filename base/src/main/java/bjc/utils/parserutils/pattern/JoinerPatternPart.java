package bjc.utils.parserutils.pattern;

import java.util.StringJoiner;

final class JoinerPatternPart implements PatternPart {
	private final PatternPart[] parts;
	private final String joiner;

	JoinerPatternPart(PatternPart[] parts, String joiner) {
		this.parts = parts;
		this.joiner = joiner;
	}

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
}