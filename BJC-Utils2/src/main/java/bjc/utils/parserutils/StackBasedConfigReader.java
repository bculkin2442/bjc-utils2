package bjc.utils.parserutils;

/**
 * This class parses a config file written in RPN and uses it to construct
 * data items
 * 
 * @author ben
 *
 *         TODO implement me
 */
public class StackBasedConfigReader {
	public static interface IItem {
		public ItemType getType();
	}

	/**
	 * Represents the types of item that can be found on stacks
	 * 
	 * @author ben
	 *
	 */
	public static enum ItemType {
		/**
		 * Represents an integral number
		 */
		INTEGER,
		/**
		 * Represents a string of characters
		 */
		STRING,
		/**
		 * Represents an arbitrary object
		 */
		OBJECT
	}

}
