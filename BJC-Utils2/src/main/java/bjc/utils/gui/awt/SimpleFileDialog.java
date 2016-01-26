package bjc.utils.gui.awt;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;

import bjc.utils.gui.SimpleDialogs;

public class SimpleFileDialog {
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

	public static File getOpenFile(Frame par, String title) {
		return getOpenFile(par, title, (String[]) null);
	}
	
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

	public static File getSaveFile(Frame par, String title) {
		return getSaveFile(par, title, (String[]) null);
	}
}
