package bjc.utils.components;

import bjc.funcdata.ListEx;
import bjc.funcdata.MapEx;

/**
 * A collection of implementations of a particular type of
 * {@link IDescribedComponent}.
 *
 * @author ben
 *
 * @param <ComponentType>
 *                        The type of components contained in this repository.
 */
public interface IComponentRepository<ComponentType extends IDescribedComponent> {
	/**
	 * Get all of the components this repository knows about.
	 *
	 * @return A map from component name to component, containing all of the
	 *         components in the repositories.
	 */
	public MapEx<String, ComponentType> getAll();

	/**
	 * Get a component with a specific name.
	 *
	 * @param name
	 *             The name of the component to retrieve.
	 *
	 * @return The named component, or null if no component with that name exists.
	 */
	public ComponentType getByName(String name);

	/**
	 * Get a list of all the registered components.
	 *
	 * @return A list of all the registered components.
	 */
	public default ListEx<ComponentType> getList() {
		return getAll().valueList();
	}

	/**
	 * Get the source from which these components came.
	 *
	 * @return The source from which these components came.
	 */
	public String getSource();
}
