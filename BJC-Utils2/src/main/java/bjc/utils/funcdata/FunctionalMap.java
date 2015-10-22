package bjc.utils.funcdata;

import java.util.HashMap;
import java.util.Map;

public class FunctionalMap<K, T> {
	private Map<K, T> wrap;
	
	public FunctionalMap() {
		wrap = new HashMap<K, T>();
	}
	
	public FunctionalMap(Map<K, T> wrap) {
		
	}
	
	
	public T get(K key) {
		return wrap.get(key);
	}
	
	public void put(K key, T val) {
		wrap.put(key, val);
	}
	
	
}
