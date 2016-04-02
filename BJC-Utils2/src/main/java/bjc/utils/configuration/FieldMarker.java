package bjc.utils.configuration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to bind a field to a field in a config file
 * 
 * @author ben
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface FieldMarker {
	/**
	 * The type of config field being represented
	 * 
	 * @return The type of config field being represented
	 */
	public FieldType value();
}
