package bjc.utils.patterns;

import java.util.*;
import java.util.function.*;

import bjc.data.*;

class FunctionalPattern<ReturnType, PredType, InputType>
	implements ComplexPattern<ReturnType, PredType, InputType> {
	private final Function<InputType, IPair<Boolean, PredType>> matcher;
	private final BiFunction<InputType, PredType, ReturnType> accepter;

	FunctionalPattern(
			Function<InputType, IPair<Boolean, PredType>> matcher,
			BiFunction<InputType, PredType, ReturnType> accepter) {
		super();
		this.matcher = matcher;
		this.accepter = accepter;
	}

	@Override
	public IPair<Boolean, PredType> matches(InputType input) {
		return matcher.apply(input);
	}
	
	@Override
	public ReturnType apply(InputType input, PredType state) {
		return accepter.apply(input, state);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accepter, matcher);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)                  return true;
		if (obj == null)                  return false;
		if (getClass() != obj.getClass()) return false;
		
		FunctionalPattern<?, ?, ?> other = (FunctionalPattern<?, ?, ?>) obj;
		
		return Objects.equals(accepter, other.accepter)
				&& Objects.equals(matcher, other.matcher);
	}
}