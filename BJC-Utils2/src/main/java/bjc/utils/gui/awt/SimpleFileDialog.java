package bjc.utils.gui.awt;

import bjc.utils.gui.SimpleDialogs;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;

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
	 * @param parent
	 *                The parent of the file picker
	 * @param title
	 *                The title of the file picker
	 * @return The file the user picked
	 */
	public static File getOpenFile(Frame parent, String title) {
		return getOpenFile(parent, title, (String[]) null);
	}

	/**
	 * Prompt the user to pick a file to open
	 *
	 * @param parent
	 *                The parent of the file picker
	 * @param title
	 *                The title of the file picker
	 * @param extensions
	 *                The extensions to accept as valid
	 * @return The file the user picked
	 */
	public static File getOpenFile(Frame parent, String title, String... extensions) {
		if(parent == null)
			throw new NullPointerException("Parent must not be null");
		else if(title == null) throw new NullPointerException("Title must not be null");

		FileDialog chooser = new FileDialog(parent, title, FileDialog.LOAD);

		if(extensions != null) {
			FilenameFilter filter = new ExtensionFileFilter(extensions);
			chooser.setFilenameFilter(filter);
		}

		chooser.setVisible(true);

		while(chooser.getFile() == null) {
			SimpleDialogs.showError(parent, "File I/O Error", "Please choose a file to open.");
			chooser.setVisible(true);
		}

		return chooser.getFiles()[0];
	}

	/**
	 * Prompt the user to pick a file to open
	 *
	 * @param parent
	 *                The parent of the file picker
	 * @param title
	 *                The title of the file picker
	 * @param extensions
	 *                The extensions to accept as valid
	 * @return The file the user picked
	 */
	public static File[] getOpenFiles(Frame parent, String title, String... extensions) {
		if(parent == null)
			throw new NullPointerException("Parent must not be null");
		else if(title == null) throw new NullPointerException("Title must not be null");

		FileDialog chooser = new FileDialog(parent, title, FileDialog.LOAD);

		if(extensions != null) {
			FilenameFilter filter = new ExtensionFileFilter(extensions);
			chooser.setFilenameFilter(filter);
		}

		chooser.setMultipleMode(true);
		chooser.setVisible(true);

		while(chooser.getFile() == null) {
			SimpleDialogs.showError(parent, "File I/O Error", "Please choose a file to open.");
			chooser.setVisible(true);
		}

		return chooser.getFiles();
	}

	/**
	 * Prompt the user to pick a file to save
	 *
	 * @param parent
	 *                The parent of the file picker
	 * @param title
	 *                The title of the file picker
	 * @return The file the user picked
	 */
	public static File getSaveFile(Frame parent, String title) {
		return getSaveFile(parent, title, (String[]) null);
	}

	/**
	 * Prompt the user to pick a file to save
	 *
	 * @param parent
	 *                The parent of the file picker
	 * @param title
	 *                The title of the file picker
	 * @param extensions
	 *                The extensions to accept as valid
	 * @return The file the user picked
	 */
	public static File getSaveFile(Frame parent, String title, String... extensions) {
		if(parent == null)
			throw new NullPointerException("Parent must not be null");
		else if(title == null) throw new NullPointerException("Title must not be null");

		FileDialog chooser = new FileDialog(parent, title, FileDialog.SAVE);

		if(extensions != null) {
			FilenameFilter filter = new ExtensionFileFilter(extensions);
			chooser.setFilenameFilter(filter);
		}

		chooser.setVisible(true);

		while(chooser.getFile() == null) {
			SimpleDialogs.showError(parent, "File I/O Error", "Please choose a file to save to.");
			chooser.setVisible(true);
		}

		return chooser.getFiles()[0];
	}
}
