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
	private static File doOpenFile(Component par, String title,
			JFileChooser files) {
		files.setDialogTitle(title);

		boolean success = false;

		while (!success) {
			try {
				maybeDoOpenFile(par, files);

				success = true;
			} catch (FileNotChosenException e) {
				SimpleDialogs.showError(par, "I/O Error",
						"Please pick a file to open");
			}
		}

		return files.getSelectedFile();
	}

	private static File doSaveFile(Component par, String title,
			JFileChooser files) {
		files.setDialogTitle(title);

		boolean success = false;

		while (!success) {
			try {
				maybeDoSaveFile(par, files);

				return files.getSelectedFile();
			} catch (FileNotChosenException e) {
				SimpleDialogs.showError(par, "I/O Error",
						"Please pick a file to save to");
			}
		}

		return files.getSelectedFile();
	}

	/**
	 * Prompt the user with a "Open File..." dialog. Keeps prompting them
	 * until they pick a file.
	 * 
	 * @param par
	 *            The component to use as the parent for the dialog.
	 * @param title
	 *            The title of the dialog to prompt with.
	 * @return The file the user has chosen.
	 */
	public static File getOpenFile(Component par, String title) {
		JFileChooser files = new JFileChooser();

		return doOpenFile(par, title, files);
	}

	/**
	 * Prompt the user with a "Open File..." dialog. Keeps prompting them
	 * until they pick a file.
	 * 
	 * @param par
	 *            The component to use as the parent for the dialog.
	 * @param title
	 *            The title of the dialog to prompt with.
	 * @param extensions
	 *            The list of file extensions the file should have.
	 * @return The file the user has chosen.
	 */
	public static File getOpenFile(Component par, String title,
			String... extensions) {
		JFileChooser files = new JFileChooser();
		files.addChoosableFileFilter(new ExtensionFileFilter(extensions));

		return doOpenFile(par, title, files);
	}

	/**
	 * Prompt the user with a "Save File..." dialog.
	 * 
	 * @param par
	 *            The component to use as the parent for the dialog.
	 * @param title
	 *            The title of the dialog to prompt with.
	 * @return The file the user chose.
	 */
	public static File getSaveFile(Component par, String title) {
		JFileChooser files = new JFileChooser();

		return doSaveFile(par, title, files);
	}

	/**
	 * Prompt the user with a "Save File..." dialog.
	 * 
	 * @param par
	 *            The component to use as the parent for the dialog.
	 * @param title
	 *            The title of the dialog to prompt with.
	 * @param extensions
	 *            The extensions of the files the user can choose.
	 * @return The file the user chose.
	 */
	public static File getSaveFile(Component par, String title,
			String... extensions) {
		JFileChooser files = new JFileChooser();
		files.addChoosableFileFilter(new ExtensionFileFilter(extensions));

		return doSaveFile(par, title, files);
	}

	private static void maybeDoOpenFile(Component par, JFileChooser files)
			throws FileNotChosenException {
		int res = files.showSaveDialog(par);

		if (res != JFileChooser.APPROVE_OPTION) {
			throw new FileNotChosenException();
		}
	}

	private static void maybeDoSaveFile(Component par, JFileChooser files)
			throws FileNotChosenException {
		int res = files.showSaveDialog(par);

		System.out.println("Result: " + res);

		if (res != JFileChooser.APPROVE_OPTION) {
			throw new FileNotChosenException();
		}
	}

	/**
	 * Prompt the user with a "Open File..." dialog.
	 * 
	 * @param par
	 *            The component to use as the parent for the dialog.
	 * @param title
	 *            The title of the dialog to prompt with.
	 * @return The file if the user chose one or null if they didn't.
	 */
	public static File maybeOpenFile(Component par, String title) {
		JFileChooser files = new JFileChooser();
		files.setDialogTitle(title);

		try {
			maybeDoOpenFile(par, files);
		} catch (FileNotChosenException e) {
		}

		return files.getSelectedFile();
	}

	/**
	 * Prompt the user with a "Save File..." dialog.
	 * 
	 * @param par
	 *            The component to use as the parent for the dialog.
	 * @param title
	 *            The title of the dialog to prompt with.
	 * @return The file if the user chose one or null if they didn't.
	 */
	public static File maybeSaveFile(Component par, String title) {
		JFileChooser files = new JFileChooser();
		files.setDialogTitle(title);

		try {
			maybeDoSaveFile(par, files);
		} catch (FileNotChosenException e) {
		}

		return files.getSelectedFile();
	}
}
