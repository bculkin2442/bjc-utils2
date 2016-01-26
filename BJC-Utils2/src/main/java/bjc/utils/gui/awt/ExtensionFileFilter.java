package bjc.utils.gui.awt;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import bjc.utils.funcdata.FunctionalList;

public class ExtensionFileFilter implements FilenameFilter {
	private FunctionalList<String> extensions;

	/**
	 * Create a new filter only showing files with the specified extensions.
	 * @param exts The extensions to show in this filter.
	 */
	public ExtensionFileFilter(String... exts) {
		extensions = new FunctionalList<>(new ArrayList<>(exts.length));

		for (String ext : exts) {
			extensions.add(ext);
		}
	}

	/**
	 * Create a new filter only showing files with the specified extensions.
	 * @param exts The extensions to show in this filter.
	 */
	public ExtensionFileFilter(List<String> exts) {
		extensions = new FunctionalList<>(exts);
	}

	@Override
	public boolean accept(File dir, String name) {
		return extensions.anyMatch(name::endsWith);
	}
}