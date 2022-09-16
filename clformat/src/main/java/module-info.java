/**
 * Represents an implementation of string formatting which is inspired by Common
 * Lisps FORMAT.
 * 
 * I say inspired because there are a number of extensions to it, as well as a
 * few things that either aren't implemented, or are implemented in a different
 * way.
 * 
 * @author bjculkin
 *
 */
module clformat {
	exports bjc.utils.ioutils.format.directives;
	exports bjc.utils.ioutils.format;
	exports bjc.utils.ioutils.format.exceptions;

	requires transitive bjc.utils;
	requires transitive esodata;
	requires inflexion;
	requires junit;
}