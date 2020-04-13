package bjc.utils.components;

import bjc.funcdata.IMap;

/**
 * A repository of components stored in memory.
 *
 * @author bjculkin
 *
 * @param <ComponentType>
 *                        The type of component stored in the repository.
 */
public class MemoryComponentRepository<ComponentType extends IDescribedComponent>
		implements IComponentRepository<ComponentType> {
	private final IMap<String, ComponentType> repo;

	private final String source;

	/**
	 * Create a new memory component repository.
	 *
	 * @param repo
	 *             The set of components to use.
	 */
	public MemoryComponentRepository(IMap<String, ComponentType> repo) {
		this(repo, "memory");
	}

	/**
	 * Create a new memory component repository.
	 *
	 * @param repo
	 *               The set of components to use.
	 * @param source
	 *               Where the components came from.
	 */
	public MemoryComponentRepository(IMap<String, ComponentType> repo, String source) {
		this.repo = repo;

		this.source = source;
	}

	@Override
	public IMap<String, ComponentType> getAll() {
		return repo;
	}

	@Override
	public ComponentType getByName(String name) {
		return repo.get(name);
	}

	@Override
	public String getSource() {
		return source;
	}
}
