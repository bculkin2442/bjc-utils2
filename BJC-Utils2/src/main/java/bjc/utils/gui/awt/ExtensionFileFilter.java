package bjc.utils.gui.awt;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IFunctionalList;

/**
 * Filter a set of filenames by extension.
 * 
 * Built for AWT
 * 
 * @author ben
 *
 */
public class ExtensionFileFilter implements FilenameFilter {
	/**
	 * The list of extensions to filter
	 */
	private IFunctionalList<String> extensions;

	/**
	 * Create a new filter only showing files with the specified
	 * extensions.
	 * 
	 * @param exts
	 *            The extensions to show in this filter.
	 */
	public ExtensionFileFilter(List<String> exts) {
		if (exts == null) {
			throw new NullPointerException("Extensions must not be null");
		}

		extensions = new FunctionalList<>(exts);
	}

	/**
	 * Create a new filter only showing files with the specified
	 * extensions.
	 * 
	 * @param exts
	 *            The extensions to show in this filter.
	 */
	public ExtensionFileFilter(String... exts) {
		extensions = new FunctionalList<>(exts);
	}

	@Override
	public boolean accept(File directory, String name) {
		return extensions.anyMatch(name::endsWith);
	}
}