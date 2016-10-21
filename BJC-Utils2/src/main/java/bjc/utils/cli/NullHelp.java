package bjc.utils.cli;

/**
 * Implementation of a help topic that doesn't exist
 * 
 * @author ben
 *
 */
public class NullHelp implements ICommandHelp {

	@Override
	public String getDescription() {
		return "No description provided";
	}

	@Override
	public String getSummary() {
		return "No summary provided";
	}
}