package bjc.utils.funcutils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;

/**
 * Utilities for doing things with files
 * 
 * @author ben
 *
 */
public class FileUtils {
	/**
	 * Traverse a directory recursively. This is a depth-first traversal
	 * 
	 * 
	 * @param root
	 *                The directory to start the traversal at
	 * @param predicate
	 *                The predicate to determine whether or not to traverse
	 *                a directory
	 * @param action
	 *                The action to invoke upon each file in the directory.
	 *                Returning true means to continue the traversal,
	 *                returning false stops it
	 * @throws IOException
	 *                 if the walk throws an exception
	 * 
	 *                 TODO If it becomes necessary, write another overload
	 *                 for this with all the buttons and knobs from
	 *                 walkFileTree
	 */
	public static void traverseDirectory(Path root, BiPredicate<Path, BasicFileAttributes> predicate,
			BiPredicate<Path, BasicFileAttributes> action) throws IOException {
		Files.walkFileTree(root, new FunctionalFileVisitor(predicate, action));
	}
}
