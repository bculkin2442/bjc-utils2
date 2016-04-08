package bjc.utils.components;

import bjc.utils.funcdata.IFunctionalList;
import bjc.utils.funcdata.IFunctionalMap;

/**
 * A collection of implementations of {@link IDescribedComponent}
 * 
 * @author ben
 *
 * @param <E>
 *            The type of components contained in this repository
 */
public interface IComponentRepository<E extends IDescribedComponent> {
	/**
	 * Get a list of all the registered componets
	 * 
	 * @return A list of all the registered components
	 */
	public IFunctionalList<E> getComponentList();

	/**
	 * Get all of the components this repository knows about
	 * 
	 * @return A map from component name to component, containing all of
	 *         the components in the repositories
	 */
	public IFunctionalMap<String, E> getComponents();

	/**
	 * Get a component with a specific name
	 * 
	 * @param name
	 *            The name of the component to retrieve
	 * @return The named component, or null if no component with that name
	 *         exists
	 */
	public E getComponentByName(String name);

	/**
	 * Get the source from which these components came
	 * 
	 * @return The source from which these components came
	 */
	public String getSource();
}