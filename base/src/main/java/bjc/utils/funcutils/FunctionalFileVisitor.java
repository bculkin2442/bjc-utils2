package bjc.utils.funcutils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;

/*
 * Functional implementation of a file visitor.
 */
final class FunctionalFileVisitor extends SimpleFileVisitor<Path> {
	private final BiPredicate<Path, BasicFileAttributes>	predicate;
	private final BiPredicate<Path, BasicFileAttributes>	action;

	public FunctionalFileVisitor(final BiPredicate<Path, BasicFileAttributes> predicate,
			final BiPredicate<Path, BasicFileAttributes> action) {
		this.predicate = predicate;
		this.action = action;
	}

	@Override
	public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
		if (predicate.test(dir, attrs)) return FileVisitResult.CONTINUE;

		return FileVisitResult.SKIP_SUBTREE;
	}

	@Override
	public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
		if (action.test(file, attrs)) return FileVisitResult.CONTINUE;

		return FileVisitResult.TERMINATE;
	}
}
