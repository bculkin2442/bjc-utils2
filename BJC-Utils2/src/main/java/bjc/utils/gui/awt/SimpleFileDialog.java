package bjc.utils.gui.awt;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;

import bjc.utils.gui.SimpleDialogs;

/**
 * A simple way to get the user to pick a file
 * 
 * Built for AWT.
 * 
 * @author ben
 *
 */
public class SimpleFileDialog {
	/**
	 * Prompt the user to pick a file to open
	 * 
	 * @param par
	 *            The parent of the file picker
	 * @param title
	 *            The title of the file picker
	 * @param extensions
	 *            The extensions to accept as valid
	 * @return The file the user picked
	 */
	public static File getOpenFile(Frame par, String title,
			String... extensions) {
		FileDialog fd = new FileDialog(par, title, FileDialog.LOAD);

		if (extensions != null) {
			FilenameFilter filter = new ExtensionFileFilter(extensions);
			fd.setFilenameFilter(filter);
		}

		fd.setVisible(true);

		while (fd.getFile() == null) {
			SimpleDialogs.showError(par, "File I/O Error",
					"Please choose a file to open.");
			fd.setVisible(true);
		}

		return fd.getFiles()[0];
	}

	/**
	 * Prompt the user to pick a file to open
	 * 
	 * @param par
	 *            The parent of the file picker
	 * @param title
	 *            The title of the file picker
	 * @return The file the user picked
	 */
	public static File getOpenFile(Frame par, String title) {
		return getOpenFile(par, title, (String[]) null);
	}

	/**
	 * Prompt the user to pick a file to save
	 * 
	 * @param par
	 *            The parent of the file picker
	 * @param title
	 *            The title of the file picker
	 * @param extensions
	 *            The extensions to accept as valid
	 * @return The file the user picked
	 */
	public static File getSaveFile(Frame par, String title,
			String... extensions) {
		FileDialog fd = new FileDialog(par, title, FileDialog.SAVE);

		if (extensions != null) {
			FilenameFilter filter = new ExtensionFileFilter(extensions);
			fd.setFilenameFilter(filter);
		}

		fd.setVisible(true);

		while (fd.getFile() == null) {
			SimpleDialogs.showError(par, "File I/O Error",
					"Please choose a file to save to.");
			fd.setVisible(true);
		}

		return fd.getFiles()[0];
	}

	/**
	 * Prompt the user to pick a file to save
	 * 
	 * @param par
	 *            The parent of the file picker
	 * @param title
	 *            The title of the file picker
	 * @return The file the user picked
	 */
	public static File getSaveFile(Frame par, String title) {
		return getSaveFile(par, title, (String[]) null);
	}
}
