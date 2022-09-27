package bjc.utils.services;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

/**
 * Indicates the default implementation for a given service.
 * 
 * @author bjcul
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Implementor {
	/**
	 * The default implementation for the service this annotates.
	 * 
	 * @return The default impl. for the service this annotates
	 */
	Class<?> value();
}
