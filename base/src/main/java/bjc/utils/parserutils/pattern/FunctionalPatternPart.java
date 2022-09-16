package bjc.utils.parserutils.pattern;

import java.util.function.Supplier;

final class FunctionalPatternPart implements PatternPart {
	private final Supplier<String> func;
	private final boolean canOptimize;

	FunctionalPatternPart(Supplier<String> func, boolean canOptimize) {
		this.func = func;
		this.canOptimize = canOptimize;
	}

	@Override
	public String toRegex() {
		return func.get();
	}

	@Override
	public boolean canOptimize() {
		return canOptimize;
	}
}