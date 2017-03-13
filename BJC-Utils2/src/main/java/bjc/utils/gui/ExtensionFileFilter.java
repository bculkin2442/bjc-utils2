package bjc.utils.gui;

import java.io.File;
import java.util.List;

import javax.swing.filechooser.FileFilter;

import bjc.utils.funcdata.FunctionalList;
import bjc.utils.funcdata.IList;

/**
 * A file filter based on extensions.
 * 
 * Built for Swing.
 * 
 * @author ben
 *
 */
public class ExtensionFileFilter extends FileFilter {
	/**
	 * The list holding all filtered extensions
	 */
	private IList<String> extensions;

	/**
	 * Create a new filter only showing files with the specified extensions.
	 * 
	 * @param exts
	 *                The extensions to show in this filter.
	 */
	public ExtensionFileFilter(List<String> exts) {
		extensions = new FunctionalList<>(exts);
	}

	/**
	 * Create a new filter only showing files with the specified extensions.
	 * 
	 * @param exts
	 *                The extensions to show in this filter.
	 */
	public ExtensionFileFilter(String... exts) {
		extensions = new FunctionalList<>(exts);
	}

	@Override
	public boolean accept(File pathname) {
		if (pathname == null) {
			throw new NullPointerException("Pathname must not be null");
		}

		return extensions.anyMatch(pathname.getName()::endsWith);
	}

	@Override
	public String getDescription() {
		return extensions.toString();
	}
}