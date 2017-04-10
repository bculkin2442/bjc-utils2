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
	private static File doOpenFile(final Component parent, final String title, final JFileChooser files) {
		if (title == null) throw new NullPointerException("Title must not be null");

		files.setDialogTitle(title);

		boolean success = false;

		while (!success) {
			try {
				maybeDoOpenFile(parent, files);

				success = true;
			} catch (final FileNotChosenException fncx) {
				// We don't care about specifics
				SimpleDialogs.showError(parent, "I/O Error", "Please pick a file to open");
			}
		}

		return files.getSelectedFile();
	}

	private static File doSaveFile(final Component parent, final String title, final JFileChooser files) {
		if (title == null) throw new NullPointerException("Title must not be null");

		files.setDialogTitle(title);

		final boolean success = false;

		while (!success) {
			try {
				maybeDoSaveFile(parent, files);

				return files.getSelectedFile();
			} catch (final FileNotChosenException fncex) {
				// We don't care about specifics
				SimpleDialogs.showError(parent, "I/O Error", "Please pick a file to save to");
			}
		}
	}

	/**
	 * Prompt the user with a "Open File..." dialog. Keeps prompting them
	 * until they pick a file.
	 *
	 * @param parent
	 *                The component to use as the parent for the dialog.
	 * @param title
	 *                The title of the dialog to prompt with.
	 * @return The file the user has chosen.
	 */
	public static File getOpenFile(final Component parent, final String title) {
		final JFileChooser files = new JFileChooser();

		return doOpenFile(parent, title, files);
	}

	/**
	 * Prompt the user with a "Open File..." dialog. Keeps prompting them
	 * until they pick a file.
	 *
	 * @param parent
	 *                The component to use as the parent for the dialog.
	 * @param title
	 *                The title of the dialog to prompt with.
	 * @param extensions
	 *                The list of file extensions the file should have.
	 * @return The file the user has chosen.
	 */
	public static File getOpenFile(final Component parent, final String title, final String... extensions) {
		final JFileChooser files = new JFileChooser();

		files.addChoosableFileFilter(new ExtensionFileFilter(extensions));

		return doOpenFile(parent, title, files);
	}

	/**
	 * Prompt the user with a "Save File..." dialog.
	 *
	 * @param parent
	 *                The component to use as the parent for the dialog.
	 * @param title
	 *                The title of the dialog to prompt with.
	 * @return The file the user chose.
	 */
	public static File getSaveFile(final Component parent, final String title) {
		final JFileChooser files = new JFileChooser();

		return doSaveFile(parent, title, files);
	}

	/**
	 * Prompt the user with a "Save File..." dialog.
	 *
	 * @param parent
	 *                The component to use as the parent for the dialog.
	 * @param title
	 *                The title of the dialog to prompt with.
	 * @param extensions
	 *                The extensions of the files the user can choose.
	 * @return The file the user chose.
	 */
	public static File getSaveFile(final Component parent, final String title, final String... extensions) {
		final JFileChooser files = new JFileChooser();

		files.addChoosableFileFilter(new ExtensionFileFilter(extensions));

		return doSaveFile(parent, title, files);
	}

	private static void maybeDoOpenFile(final Component parent, final JFileChooser files)
			throws FileNotChosenException {
		if (parent == null)
			throw new NullPointerException("Parent must not be null");
		else if (files == null) throw new NullPointerException("File chooser must not be null");

		final int result = files.showSaveDialog(parent);

		if (result != JFileChooser.APPROVE_OPTION) throw new FileNotChosenException();
	}

	private static void maybeDoSaveFile(final Component parent, final JFileChooser files)
			throws FileNotChosenException {
		if (parent == null)
			throw new NullPointerException("Parent must not be null");
		else if (files == null) throw new NullPointerException("File chooser must not be null");

		final int result = files.showSaveDialog(parent);

		if (result != JFileChooser.APPROVE_OPTION) throw new FileNotChosenException();
	}

	/**
	 * Prompt the user with a "Open File..." dialog.
	 *
	 * @param parent
	 *                The component to use as the parent for the dialog.
	 * @param title
	 *                The title of the dialog to prompt with.
	 * @return The file if the user chose one or null if they didn't.
	 */
	public static File maybeOpenFile(final Component parent, final String title) {
		if (title == null) throw new NullPointerException("Title must not be null");

		final JFileChooser files = new JFileChooser();
		files.setDialogTitle(title);

		try {
			maybeDoOpenFile(parent, files);
		} catch (final FileNotChosenException fncex) {
			// We don't care about specifics
		}

		return files.getSelectedFile();
	}

	/**
	 * Prompt the user with a "Save File..." dialog.
	 *
	 * @param parent
	 *                The component to use as the parent for the dialog.
	 * @param title
	 *                The title of the dialog to prompt with.
	 * @return The file if the user chose one or null if they didn't.
	 */
	public static File maybeSaveFile(final Component parent, final String title) {
		if (title == null) throw new NullPointerException("Title must not be null");

		final JFileChooser files = new JFileChooser();
		files.setDialogTitle(title);

		try {
			maybeDoSaveFile(parent, files);
		} catch (final FileNotChosenException fncex) {
			// We don't care about specifics
		}

		return files.getSelectedFile();
	}
}
