package bjc.utils.cli.fds;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import bjc.utils.cli.CommandHelp;

import static java.lang.String.format;

/**
 * Simple implementation of {@link FDSMode}.
 * 
 * @author bjculkin
 *
 * @param <S>
 *                The FDS state type.
 */
public class SimpleFDSMode<S> implements FDSMode<S> {
	private Map<Character, FDSCommand<S>>	commands;
	private Map<Character, FDSMode<S>>	modes;
	private Map<Character, CommandHelp>	help;

	private Set<Character>	registered;
	private char[]		registeredArray;
	private boolean		changed;

	/**
	 * Create a new empty FDS mode.
	 */
	public SimpleFDSMode() {
		commands = new HashMap<>();
		modes = new HashMap<>();
		help = new HashMap<>();

		registered = new HashSet<>();
		changed = true;
	}

	/**
	 * Add a command to the mode.
	 * 
	 * @param c
	 *                The character to bind to the command.
	 * 
	 * @param comm
	 *                The command to add.
	 * 
	 * @param hlp
	 *                The help for the command.
	 * 
	 * @throws FDSException
	 *                 If the character is already bound to a command.
	 */
	public void addCommand(char c, FDSCommand<S> comm, CommandHelp hlp) throws FDSException {
		if (comm == null)
			throw new NullPointerException("Command must not be null");
		else if (commands.containsKey(c) || modes.containsKey(c))
			throw new FDSException(format("Character '%s' is already bound"));

		commands.put(c, comm);

		registered.add(c);
		if (!changed) changed = true;
	}

	/**
	 * Add a submode to the mode.
	 * 
	 * @param c
	 *                The character to bind to the submode.
	 * 
	 * @param mode
	 *                The submode to add.
	 * 
	 * @throws FDSException
	 *                 If the character is already bound to a submode.
	 */
	public void addSubmode(char c, FDSMode<S> mode) throws FDSException {
		if (mode == null)
			throw new NullPointerException("Mode must not be null");
		else if (modes.containsKey(c) || commands.containsKey(c))
			throw new FDSException(format("Character '%s' is already bound"));

		modes.put(c, mode);

		registered.add(c);
		if (!changed) changed = true;
	}

	@Override
	public char[] registeredChars() {
		if (!changed) return registeredArray;

		registeredArray = new char[registered.size()];

		int i = 0;
		for (char c : registered) {
			registeredArray[i] = c;

			i += 1;
		}

		changed = false;

		return registeredArray;
	}

	@Override
	public boolean hasCommand(char c) {
		return commands.containsKey(c);
	}

	@Override
	public boolean hasSubmode(char c) {
		return modes.containsKey(c);
	}

	@Override
	public FDSCommand<S> getCommand(char c) throws FDSException {
		if (!commands.containsKey(c)) {
			throw new FDSException(String.format("No command bound to '%s'", c));
		}

		return commands.get(c);
	}

	@Override
	public FDSMode<S> getSubmode(char c) throws FDSException {
		if (!modes.containsKey(c)) {
			throw new FDSException(String.format("No mode bound to '%s'", c));
		}

		return modes.get(c);
	}

	@Override
	public CommandHelp getHelp(char c) {
		return help.get(c);
	}
}