package bjc.utils.funcutils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;

/**
 * Utilities for doing things with files
 * 
 * @author ben
 *
 */
public class FileUtils {
	private static final class FunctionalFileVisitor
			extends SimpleFileVisitor<Path> {
		private BiPredicate<Path, BasicFileAttributes>	traversalPredicate;
		private BiPredicate<Path, BasicFileAttributes>	traversalAction;

		public FunctionalFileVisitor(
				BiPredicate<Path, BasicFileAttributes> traversalPredicate,
				BiPredicate<Path, BasicFileAttributes> traversalAction) {
			this.traversalPredicate = traversalPredicate;
			this.traversalAction = traversalAction;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir,
				BasicFileAttributes attrs) throws IOException {
			if (traversalPredicate.test(dir, attrs)) {
				return FileVisitResult.CONTINUE;
			}

			return FileVisitResult.SKIP_SUBTREE;
		}

		@Override
		public FileVisitResult visitFile(Path file,
				BasicFileAttributes attrs) throws IOException {
			if (traversalAction.test(file, attrs)) {
				return FileVisitResult.CONTINUE;
			}

			return FileVisitResult.TERMINATE;
		}
	}

	/**
	 * Traverse a directory recursively. This is a depth-first traversal
	 * 
	 * 
	 * @param root
	 *            The directory to start the traversal at
	 * @param traversalPredicate
	 *            The predicate to determine whether or not to traverse a
	 *            directory
	 * @param traversalAction
	 *            The action to invoke upon each file in the directory.
	 *            Returning true means to continue the traversal, returning
	 *            false stops it
	 * @throws IOException
	 *             if the walk throws an exception
	 * 
	 *             TODO If it becomes necessary, write another overload for
	 *             this with all the buttons and knobs from walkFileTree
	 */
	public static void traverseDirectory(Path root,
			BiPredicate<Path, BasicFileAttributes> traversalPredicate,
			BiPredicate<Path, BasicFileAttributes> traversalAction)
			throws IOException {
		Files.walkFileTree(root, new FunctionalFileVisitor(
				traversalPredicate, traversalAction));
	}
}
