package bjc.utils.configuration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container interface for field type markers
 * 
 * There can't be a String field type, so for fields of a single string,
 * use FieldType itself
 * 
 * @author ben
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface FieldType {
	/**
	 * Marker to indicate a field as a single boolean flag
	 * 
	 * @author Benjamin
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Flag {
		/**
		 * The default value of the flag
		 * 
		 * @return The default value of the flag
		 */
		boolean value() default false;
	}

	/**
	 * Marker to indicate a fields as an array of boolean flags
	 * 
	 * @author Benjamin
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Flags {
		/**
		 * The default value of the flags
		 * 
		 * @return The default value of the flags
		 */
		boolean[] value() default { false };
	}

	/**
	 * Marker to indicate a field as a single floating point value
	 * 
	 * @author Benjamin
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Float {
		/**
		 * The default value of the number
		 * 
		 * @return The default value of the number
		 */
		double value() default 0.0;
	}

	/**
	 * Marker to indicate a fields as an array of floating point values
	 * 
	 * @author Benjamin
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Floats {
		/**
		 * The default value of the numbers
		 * 
		 * @return The default value of the numbers
		 */
		double[] value() default { 0.0 };
	}

	/**
	 * Marker to indicate a field as a single integral value
	 * 
	 * @author Benjamin
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Integer {
		/**
		 * The default value of the integer
		 * 
		 * @return The default value of the integer
		 */
		int value() default 0;
	}

	/**
	 * Marker to indicate a fields as an array of integral values
	 * 
	 * @author Benjamin
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Integers {
		/**
		 * The default value of the integers
		 * 
		 * @return The default value of the integers
		 */
		int[] value() default { 0 };
	}

	/**
	 * Marker to indicate a fields as an array of strings
	 * 
	 * @author Benjamin
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Strings {
		/**
		 * The default value of each of the strings
		 * 
		 * @return The default value of each of the strings
		 */
		String[] value() default { "" };
	}

	/**
	 * The default value of the string
	 * 
	 * @return The default value of the strings
	 */
	public String value() default "";
}
