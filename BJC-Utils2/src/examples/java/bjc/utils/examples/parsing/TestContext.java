package bjc.utils.examples.parsing;

import bjc.utils.esodata.Directory;
import bjc.utils.esodata.SimpleDirectory;
import bjc.utils.esodata.SimpleStack;
import bjc.utils.esodata.Stack;

/**
 * Simple context for the parser.
 * 
 * @author EVE
 *
 */
public class TestContext {
	/**
	 * The variable scoping information.
	 */
	public Stack<Directory<String, String>> scopes;

	/**
	 * The current number of scopes inside this scope.
	 */
	public Stack<Integer> blockCount;

	/**
	 * Create a new test context.
	 */
	public TestContext() {
		scopes = new SimpleStack<>();
		blockCount = new SimpleStack<>();

		scopes.push(new SimpleDirectory<>());
		blockCount.push(0);
	}

	@Override
	public String toString() {
		return String.format("TestContext [scopes=%s, blockCount=%s]", scopes, blockCount);
	}
}
