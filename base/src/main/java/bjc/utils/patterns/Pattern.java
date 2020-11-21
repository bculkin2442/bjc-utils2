package bjc.utils.patterns;

import java.util.*;
import java.util.function.*;
import java.util.regex.*;

import bjc.data.*;

/**
 * A simpler version of ComplexPattern, which always applies against Object
 * 
 * @author Ben Culkin
 *
 * @param <ReturnType> The type returned by the pattern.
 * @param <PredType> The state type returned by the predicate.
 */
public interface Pattern<ReturnType, PredType>
	extends ComplexPattern<ReturnType, PredType, Object> {	
	/* Pattern factory methods */
}