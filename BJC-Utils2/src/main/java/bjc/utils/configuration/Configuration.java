package bjc.utils.configuration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a class as having a config file bound to it
 * 
 * For this annotation to be valid to apply to the class, the class must
 * meet two qualities
 * 
 * 1. Have a public no-args constructor
 * 
 * 2. Have one or more fields annoted with a {@link FieldMarker} annotation
 * 
 * @author ben
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Configuration {
}