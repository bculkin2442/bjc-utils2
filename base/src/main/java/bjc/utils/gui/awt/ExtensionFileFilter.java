package bjc.utils.gui.awt;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import bjc.funcdata.FunctionalList;
import bjc.funcdata.IList;

/**
 * Filter a set of filenames by extension.
 *
 * Built for AWT
 *
 * @author ben
 */
public class ExtensionFileFilter implements FilenameFilter {
	/* The list of extensions to filter */
	private final IList<String> extensions;

	/**
	 * Create a new filter only showing files with the specified extensions.
	 *
	 * @param exts
	 *        The extensions to show in this filter.
	 */
	public ExtensionFileFilter(final List<String> exts) {
		if(exts == null) throw new NullPointerException("Extensions must not be null");

		extensions = new FunctionalList<>(exts);
	}

	/**
	 * Create a new filter only showing files with the specified extensions.
	 *
	 * @param exts
	 *        The extensions to show in this filter.
	 */
	public ExtensionFileFilter(final String... exts) {
		extensions = new FunctionalList<>(exts);
	}

	@Override
	public boolean accept(final File directory, final String name) {
		return extensions.anyMatch(name::endsWith);
	}
}
