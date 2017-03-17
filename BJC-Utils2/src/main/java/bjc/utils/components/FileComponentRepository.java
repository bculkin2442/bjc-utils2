package bjc.utils.components;

import bjc.utils.data.IHolder;
import bjc.utils.data.Identity;
import bjc.utils.funcdata.FunctionalMap;
import bjc.utils.funcdata.IList;
import bjc.utils.funcdata.IMap;
import bjc.utils.funcutils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A component repository that loads its components from files in a directory
 *
 * @author ben
 *
 * @param <ComponentType>
 *                The type of component being read in
 */
public class FileComponentRepository<ComponentType extends IDescribedComponent>
		implements IComponentRepository<ComponentType> {
	// The logger to use for storing data about this class
	private static final Logger CLASS_LOGGER = Logger.getLogger("FileComponentRepository");

	// The internal storage of components
	private IMap<String, ComponentType> components;

	// The path that all the components came from
	private Path sourceDirectory;

	/**
	 * Create a new component repository sourcing components from files in a
	 * directory
	 *
	 * An exception thrown during the loading of a component will only cause
	 * the loading of that component to fail, but a warning will be logged.
	 *
	 * @param directory
	 *                The directory to read component files from
	 * @param componentReader
	 *                The function to use to convert files to components
	 */
	public FileComponentRepository(File directory, Function<File, ? extends ComponentType> componentReader) {
		// Make sure we have valid arguments
		if(directory == null)
			throw new NullPointerException("Directory must not be null");
		else if(!directory.isDirectory())
			throw new IllegalArgumentException("File " + directory + " is not a directory.\n"
					+ "Components can only be read from a directory");
		else if(componentReader == null) throw new NullPointerException("Component reader must not be null");

		// Initialize our fields
		components = new FunctionalMap<>();
		sourceDirectory = directory.toPath().toAbsolutePath();

		// Marker for making sure we don't skip the parent
		IHolder<Boolean> isFirstDir = new Identity<>(true);

		// Predicate to use to traverse all the files in a directory,
		// but
		// not recurse into sub-directories
		BiPredicate<Path, BasicFileAttributes> firstLevelTraverser = (pth, attr) -> {
			if(attr.isDirectory() && !isFirstDir
					.getValue()) /*
							 * Skip directories,
							 * they probably have
							 * component support
							 * files.
							 */
				return false;

			/*
			 * Don't skip the first directory, that's the parent
			 * directory
			 */
			isFirstDir.replace(false);

			return true;
		};

		// Try reading components
		try {
			FileUtils.traverseDirectory(sourceDirectory, firstLevelTraverser, (pth, attr) -> {
				loadComponent(componentReader, pth);

				// Keep loading components, even if this one
				// failed
				return true;
			});
		} catch(IOException ioex) {
			CLASS_LOGGER.log(Level.WARNING, ioex, () -> "Error found reading component from file.");
		}
	}

	@Override
	public IMap<String, ComponentType> getAll() {
		return components;
	}

	@Override
	public ComponentType getByName(String name) {
		return components.get(name);
	}

	@Override
	public IList<ComponentType> getList() {
		return components.valueList();
	}

	@Override
	public String getSource() {
		return "Components read from directory " + sourceDirectory + ".";
	}

	/*
	 * Load a component from a file
	 */
	private void loadComponent(Function<File, ? extends ComponentType> componentReader, Path pth) {
		try {
			// Try to load the component
			ComponentType component = componentReader.apply(pth.toFile());

			if(component == null)
				throw new NullPointerException("Component reader read null component");
			else if(!components.containsKey(component.getName())) {
				// We only care about the latest version of a
				// component
				ComponentType oldComponent = components.put(component.getName(), component);

				if(oldComponent.getVersion() > component.getVersion()) {
					components.put(oldComponent.getName(), oldComponent);
				}
			} else {
				CLASS_LOGGER.warning("Found a duplicate component.\n"
						+ "Multiple versions of the same component are not currently supported.\n"
						+ "Only the latest version of the component" + component
						+ " will be registered .");
			}
		} catch(Exception ex) {
			CLASS_LOGGER.log(Level.WARNING, ex, () -> "Error found reading component from file "
					+ pth.toString() + ". This component will not be loaded");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileComponentRepository [");

		if(components != null) {
			builder.append("components=");
			builder.append(components);
			builder.append(", ");
		}

		if(sourceDirectory != null) {
			builder.append("sourceDirectory=");
			builder.append(sourceDirectory);
		}

		builder.append("]");

		return builder.toString();
	}
}