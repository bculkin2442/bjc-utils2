package bjc.utils.funcutils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;

/**
 * Utilities for doing things with files.
 *
 * @author ben
 */
public class FileUtils {
	/*
	 * @NOTE If it becomes necessary, write another overload for this with
	 * all the buttons and knobs from walkFileTree.
	 */
	/**
	 * Traverse a directory recursively. This is a depth-first traversal.
	 *
	 * @param root
	 *        The directory to start the traversal at.
	 *
	 * @param predicate
	 *        The predicate to determine whether or not to traverse a
	 *        directory.
	 *
	 * @param action
	 *        The action to invoke upon each file in the directory.
	 *        Returning true means to continue the traversal, returning
	 *        false stops it.
	 *
	 * @throws IOException
	 *         If the walk throws an exception.
	 *
	 */
	public static void traverseDirectory(final Path root, final BiPredicate<Path, BasicFileAttributes> predicate,
			final BiPredicate<Path, BasicFileAttributes> action) throws IOException {
		Files.walkFileTree(root, new FunctionalFileVisitor(predicate, action));
	}
}
