package bjc.utils.components;

import bjc.utils.funcdata.IMap;

public class MemoryComponentRepository<ComponentType extends IDescribedComponent> implements IComponentRepository<ComponentType> {
	private final IMap<String, ComponentType> repo;

	private final String source;

	public MemoryComponentRepository(IMap<String, ComponentType> repo) {
		this(repo, "memory");
	}

	public MemoryComponentRepository(IMap<String, ComponentType> repo, String source) {
		this.repo = repo;

		this.source = source;
	}

	public IMap<String, ComponentType> getAll() {
		return repo;
	}

	public ComponentType getByName(String name) {
		return repo.get(name);
	}

	public String getSource() {
		return source;
	}
}
