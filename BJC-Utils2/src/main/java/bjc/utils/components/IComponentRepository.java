package bjc.utils.components;

import java.util.Map;

import bjc.utils.funcdata.FunctionalList;

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
	public FunctionalList<E> getComponentList();

	/**
	 * Get all of the components this repository knows about
	 * 
	 * @return A map from component name to component, containing all of
	 *         the components in the repositories
	 */
	public Map<String, E> getComponents();

	/**
	 * Get the source from which these components came
	 * 
	 * @return The source from which these components came
	 */
	public String getSource();
}
