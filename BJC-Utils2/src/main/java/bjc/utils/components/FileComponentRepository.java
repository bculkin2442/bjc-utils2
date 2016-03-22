package bjc.utils.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import bjc.utils.funcdata.FunctionalList;

/**
 * A component repository that loads its components from files in a
 * directory
 * 
 * @author ben
 *
 * @param <E>
 *            The type of component being read in
 */
public class FileComponentRepository<E extends IDescribedComponent>
		implements IComponentRepository<E> {

	/**
	 * The internal storage of components
	 */
	private Map<String, E>	comps;

	/**
	 * The path that all the components came from
	 */
	private String			sourcePath;

	/**
	 * Create a new component repository sourcing components from files in
	 * a directory
	 * 
	 * @param dir
	 *            The directory to read component files from
	 * @param reader
	 *            The function to use to convert files to components
	 */
	public FileComponentRepository(File dir,
			Function<InputStream, E> reader) {
		comps = new HashMap<>();
		sourcePath = dir.getAbsolutePath();

		for (File fle : dir.listFiles()) {
			if (fle.isDirectory()) {
				// Do nothing with directories. They probably contain
				// support files for components
			} else {
				try {
					E comp = reader.apply(new FileInputStream(fle));

					comps.put(comp.getName(), comp);
				} catch (FileNotFoundException fnfx) {
					System.err.println("Couldn't read component file: "
							+ fle.getAbsolutePath() + "\nReason: "
							+ fnfx.getMessage());

					fnfx.printStackTrace(System.err);
				}
			}
		}
	}

	@Override
	public FunctionalList<E> getComponentList() {
		FunctionalList<E> ret = new FunctionalList<>();

		comps.forEach((name, comp) -> {
			ret.add(comp);
		});

		return ret;
	}

	@Override
	public Map<String, E> getComponents() {
		return comps;
	}

	@Override
	public String getSource() {
		return "Read from directory " + sourcePath + ".";
	}
}
