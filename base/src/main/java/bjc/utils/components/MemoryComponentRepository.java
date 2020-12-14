package bjc.utils.components;

import bjc.funcdata.MapEx;

/**
 * A repository of components stored in memory.
 *
 * @author bjculkin
 *
 * @param <ComponentType>
 *                        The type of component stored in the repository.
 */
public class MemoryComponentRepository<ComponentType extends DescribedComponent>
		implements ComponentRepository<ComponentType> {
	private final MapEx<String, ComponentType> repo;

	private final String source;

	/**
	 * Create a new memory component repository.
	 *
	 * @param repo
	 *             The set of components to use.
	 */
	public MemoryComponentRepository(MapEx<String, ComponentType> repo) {
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
	public MemoryComponentRepository(MapEx<String, ComponentType> repo, String source) {
		this.repo = repo;

		this.source = source;
	}

	@Override
	public MapEx<String, ComponentType> getAll() {
		return repo;
	}

	@Override
	public ComponentType getByName(String name) {
		return repo.get(name).get();
	}

	@Override
	public String getSource() {
		return source;
	}
}
