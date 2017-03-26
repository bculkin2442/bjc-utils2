package bjc.utils.cli.fds;

import bjc.utils.cli.CommandHelp;
import bjc.utils.cli.NullHelp;

/**
 * A collection of related FDS commands.
 * 
 * @author bjculkin
 *
 * @param <S>
 *                The FDS state type.
 */
public interface FDSMode<S> {
	/**
	 * Get all the characters that are registered to something in this mode.
	 * 
	 * In this context, something means a command or submode.
	 * 
	 * @return All of the characters registered to something in this mode.
	 */
	char[] registeredChars();

	/*
	 * Check for the existence of commands/submodes.
	 */

	/**
	 * Check if there is a command registered to the given character.
	 * 
	 * @param c
	 *                The character to check
	 * 
	 * @return Whether or not there is a command bound to that character.
	 */
	boolean hasCommand(char c);

	/**
	 * Check if there is a submode registered to the given character.
	 * 
	 * @param c
	 *                The character to check
	 * 
	 * @return Whether or not there is a submode bound to that character.
	 */
	boolean hasSubmode(char c);

	/*
	 * Get commands and submodes.
	 */

	/**
	 * Get the command attached to a given character.
	 * 
	 * @param c
	 *                The character to get the command for.
	 * 
	 * @return The command bound to that character.
	 * 
	 * @throws FDSException
	 *                 If there is no command bound to that character.
	 */
	FDSCommand<S> getCommand(char c) throws FDSException;

	/**
	 * Get the command attached to a given character.
	 * 
	 * @param c
	 *                The character to get the command for.
	 * 
	 * @return The command bound to that character.
	 * 
	 * @throws FDSException
	 *                 If there is no command bound to that character.
	 */
	FDSMode<S> getSubmode(char c) throws FDSException;

	/*
	 * Help utilities
	 */
	/**
	 * Get the help for what's bound to a character.
	 * 
	 * This should be one line.
	 * 
	 * @param c
	 *                The character to look at the help for.
	 * 
	 * @return The help for what's bound to the character.
	 */
	default CommandHelp getHelp(char c) {
		return new NullHelp();
	}
}
