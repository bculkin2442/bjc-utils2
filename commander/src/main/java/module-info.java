/**
 * CLI interface for various other libraries.
 * 
 * @author bjcul
 *
 */
module commander {
	exports bjc.commander;

	requires transitive bjc.utils;
	requires java.logging;
}