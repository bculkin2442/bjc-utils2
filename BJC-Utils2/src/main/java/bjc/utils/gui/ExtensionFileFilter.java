package bjc.utils.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import java.util.ArrayList;
import java.util.List;

import bjc.utils.funcdata.FunctionalList;

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
	private FunctionalList<String> extensions;

	/**
	 * Create a new filter only showing files with the specified
	 * extensions.
	 * 
	 * @param exts
	 *            The extensions to show in this filter.
	 */
	public ExtensionFileFilter(List<String> exts) {
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
		extensions = new FunctionalList<>(new ArrayList<>(exts.length));

		for (String ext : exts) {
			extensions.add(ext);
		}
	}

	@Override
	public boolean accept(File pathname) {
		return extensions.anyMatch(pathname.getName()::endsWith);
	}

	@Override
	public String getDescription() {
		return extensions.toString();
	}

}
