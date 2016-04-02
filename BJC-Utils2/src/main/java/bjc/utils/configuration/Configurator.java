package bjc.utils.configuration;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Bind the values in a prepared class to a config file
 * 
 * @author ben
 *
 */
public class Configurator {
	/**
	 * Bind the values in a config file to the values in a class,
	 * substituting default values if none are appropriate
	 * 
	 * @param <E>
	 *            The type of the object to bind
	 * @param clasz
	 *            The class of the object to bind
	 * @param inputSource
	 *            The source to get input from
	 * @return A instance of the provided class, with values filled in from
	 *         a config file
	 */
	public static <E> E readConfig(Class<E> clasz,
			InputStream inputSource) {
		try {
			Constructor<E> noArgConstructor = clasz.getConstructor();

			E backingStore = noArgConstructor.newInstance();

			return backingStore;
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return null;
		}
	}
}
