package bjc.utils.components;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger	CLASS_LOGGER	=
			LoggerFactory.getLogger(FileComponentRepository.class);

	/**
	 * The internal storage of components
	 */
	private Map<String, E>		components;

	/**
	 * The path that all the components came from
	 */
	private Path				sourceDirectory;

	/**
	 * Create a new component repository sourcing components from files in
	 * a directory
	 * 
	 * An exception thrown during the loading of a component will only
	 * cause the loading of that component to fail, but a warning will be
	 * logged.
	 * 
	 * @param directory
	 *            The directory to read component files from
	 * @param componentReader
	 *            The function to use to convert files to components
	 */
	public FileComponentRepository(File directory,
			Function<File, E> componentReader) {
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("File " + directory
					+ " is not a directory.\n"
					+ "Components can only be read from a directory");
		}

		components = new HashMap<>();
		sourceDirectory = directory.toPath().toAbsolutePath();

		File[] listFiles = directory.listFiles();

		if (listFiles == null) {
			throw new IllegalArgumentException("File " + directory
					+ " is not a valid directory.\n"
					+ "Components can only be read from a directory");
		}

		for (File componentFile : listFiles) {
			if (componentFile.isDirectory()) {
				// Do nothing with directories. They probably contain
				// support files for components
			} else {
				try {
					E component = componentReader.apply(componentFile);

					if (component == null) {
						throw new NullPointerException(
								"Component reader read null component");
					} else if (!components
							.containsKey(component.getName())) {
						components.put(component.getName(), component);
					} else {
						CLASS_LOGGER.warn("Found a duplicate component.\n"
								+ "Multiple versions of the same component are not currently supported.\n"
								+ "The component" + component
								+ " will not be registered .");
					}
				} catch (Exception ex) {
					CLASS_LOGGER
							.warn("Error found reading component from file "
									+ componentFile.toString()
									+ ". This component will not be loaded");
				}

			}
		}
	}

	@Override
	public FunctionalList<E> getComponentList() {
		FunctionalList<E> returnedList = new FunctionalList<>();

		components
				.forEach((name, component) -> returnedList.add(component));

		return returnedList;
	}

	@Override
	public Map<String, E> getComponents() {
		return components;
	}

	@Override
	public String getSource() {
		return "Components read from directory " + sourceDirectory + ".";
	}

	@Override
	public E getComponentByName(String name) {
		return components.get(name);
	}
}