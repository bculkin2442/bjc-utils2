package bjc.utils.funcutils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;

final class FunctionalFileVisitor extends SimpleFileVisitor<Path> {
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
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			throws IOException {
		if (traversalAction.test(file, attrs)) {
			return FileVisitResult.CONTINUE;
		}

		return FileVisitResult.TERMINATE;
	}
}