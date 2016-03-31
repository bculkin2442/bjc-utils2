package bjc.utils.gui;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import bjc.utils.exceptions.FileNotChosenException;

/**
 * Utility class for easily prompting user for files.
 * 
 * Built for Swing.
 * 
 * @author ben
 *
 */
public class SimpleFileChooser {
	private static File doOpenFile(Component parent, String title,
			JFileChooser files) {
		files.setDialogTitle(title);

		boolean success = false;

		while (!success) {
			try {
				maybeDoOpenFile(parent, files);

				success = true;
			} catch (FileNotChosenException e) {
				SimpleDialogs.showError(parent, "I/O Error",
						"Please pick a file to open");
			}
		}

		return files.getSelectedFile();
	}

	private static File doSaveFile(Component parent, String title,
			JFileChooser files) {
		files.setDialogTitle(title);

		boolean success = false;

		while (!success) {
			try {
				maybeDoSaveFile(parent, files);

				return files.getSelectedFile();
			} catch (FileNotChosenException e) {
				SimpleDialogs.showError(parent, "I/O Error",
						"Please pick a file to save to");
			}
		}

		return files.getSelectedFile();
	}

	/**
	 * Prompt the user with a "Open File..." dialog. Keeps prompting them
	 * until they pick a file.
	 * 
	 * @param parent
	 *            The component to use as the parent for the dialog.
	 * @param title
	 *            The title of the dialog to prompt with.
	 * @return The file the user has chosen.
	 */
	public static File getOpenFile(Component parent, String title) {
		JFileChooser files = new JFileChooser();

		return doOpenFile(parent, title, files);
	}

	/**
	 * Prompt the user with a "Open File..." dialog. Keeps prompting them
	 * until they pick a file.
	 * 
	 * @param parent
	 *            The component to use as the parent for the dialog.
	 * @param title
	 *            The title of the dialog to prompt with.
	 * @param extensions
	 *            The list of file extensions the file should have.
	 * @return The file the user has chosen.
	 */
	public static File getOpenFile(Component parent, String title,
			String... extensions) {
		JFileChooser files = new JFileChooser();
		files.addChoosableFileFilter(new ExtensionFileFilter(extensions));

		return doOpenFile(parent, title, files);
	}

	/**
	 * Prompt the user with a "Save File..." dialog.
	 * 
	 * @param parent
	 *            The component to use as the parent for the dialog.
	 * @param title
	 *            The title of the dialog to prompt with.
	 * @return The file the user chose.
	 */
	public static File getSaveFile(Component parent, String title) {
		JFileChooser files = new JFileChooser();

		return doSaveFile(parent, title, files);
	}

	/**
	 * Prompt the user with a "Save File..." dialog.
	 * 
	 * @param parent
	 *            The component to use as the parent for the dialog.
	 * @param title
	 *            The title of the dialog to prompt with.
	 * @param extensions
	 *            The extensions of the files the user can choose.
	 * @return The file the user chose.
	 */
	public static File getSaveFile(Component parent, String title,
			String... extensions) {
		JFileChooser files = new JFileChooser();
		files.addChoosableFileFilter(new ExtensionFileFilter(extensions));

		return doSaveFile(parent, title, files);
	}

	private static void maybeDoOpenFile(Component parent,
			JFileChooser files) throws FileNotChosenException {
		int dialogResult = files.showSaveDialog(parent);

		if (dialogResult != JFileChooser.APPROVE_OPTION) {
			throw new FileNotChosenException();
		}
	}

	private static void maybeDoSaveFile(Component parent,
			JFileChooser files) throws FileNotChosenException {
		int dialogResult = files.showSaveDialog(parent);

		if (dialogResult != JFileChooser.APPROVE_OPTION) {
			throw new FileNotChosenException();
		}
	}

	/**
	 * Prompt the user with a "Open File..." dialog.
	 * 
	 * @param parent
	 *            The component to use as the parent for the dialog.
	 * @param title
	 *            The title of the dialog to prompt with.
	 * @return The file if the user chose one or null if they didn't.
	 */
	public static File maybeOpenFile(Component parent, String title) {
		JFileChooser files = new JFileChooser();
		files.setDialogTitle(title);

		try {
			maybeDoOpenFile(parent, files);
		} catch (FileNotChosenException e) {
		}

		return files.getSelectedFile();
	}

	/**
	 * Prompt the user with a "Save File..." dialog.
	 * 
	 * @param parent
	 *            The component to use as the parent for the dialog.
	 * @param title
	 *            The title of the dialog to prompt with.
	 * @return The file if the user chose one or null if they didn't.
	 */
	public static File maybeSaveFile(Component parent, String title) {
		JFileChooser files = new JFileChooser();
		files.setDialogTitle(title);

		try {
			maybeDoSaveFile(parent, files);
		} catch (FileNotChosenException e) {
		}

		return files.getSelectedFile();
	}
}
