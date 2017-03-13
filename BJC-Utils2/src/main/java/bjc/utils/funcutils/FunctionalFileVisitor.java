package bjc.utils.funcutils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;

final class FunctionalFileVisitor extends SimpleFileVisitor<Path> {
	private BiPredicate<Path, BasicFileAttributes> predicate;
	private BiPredicate<Path, BasicFileAttributes> action;

	public FunctionalFileVisitor(BiPredicate<Path, BasicFileAttributes> predicate,
			BiPredicate<Path, BasicFileAttributes> action) {
		this.predicate = predicate;
		this.action = action;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		if (predicate.test(dir, attrs)) {
			return FileVisitResult.CONTINUE;
		}

		return FileVisitResult.SKIP_SUBTREE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		if (action.test(file, attrs)) {
			return FileVisitResult.CONTINUE;
		}

		return FileVisitResult.TERMINATE;
	}
}
