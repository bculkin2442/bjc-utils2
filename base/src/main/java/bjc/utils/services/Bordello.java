package bjc.utils.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A place to retrieve services from
 * 
 * @author bjcul
 *
 */
public class Bordello {
	private static final Map<Class<?>, Object> services = new ConcurrentHashMap<>();
	
	/**
	 * Retrieve the implementation of a given service.
	 * 
	 * @param <T> The type of the service.
	 * 
	 * @param interfaceClass The class of the service.
	 * 
	 * @return The default implementation of the service.
	 */
	public static <T> T get(Class<T> interfaceClass) {
		synchronized (interfaceClass) {
			Object service = services.get(interfaceClass);
			if (service == null) {
				try {
					Class<?> implementor = interfaceClass.getAnnotation(Implementor.class).value();
					service = implementor.getDeclaredConstructor().newInstance();
					services.put(interfaceClass, implementor);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
			return interfaceClass.cast(service);
		}
	}
	
	/**
	 * Set an implementation for a given service to be something other than the default.
	 * 
	 * @param <T> The type of the service
	 * 
	 * @param interfaceClass The class of the service
	 * @param implementor The alternate implementation for the service.
	 */
	public static <T> void set(Class<T> interfaceClass, T implementor) {
		synchronized (interfaceClass) {
			services.put(interfaceClass, implementor);
		}
	}
}
