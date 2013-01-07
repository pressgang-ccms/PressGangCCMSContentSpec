package org.jboss.pressgang.ccms.contentspec.provider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import org.jboss.pressgang.ccms.contentspec.wrapper.WrapperFactory;

/**
 *
 */
public abstract class DataProviderFactory {
    private WrapperFactory wrapperFactory = null;

    private static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread().getContextClassLoader();
                } catch (SecurityException ex) {
                }
                return cl;
            }
        });
    }

    /**
     * Initialise the Data Provider Factory with the required data.
     *
     * @param args The arguments required to create the Data Provider Factory.
     */
    public static DataProviderFactory create(final Object... args) {
        // Find the implementation for the DataProviderFactory.
        final Class<? extends DataProviderFactory> dataProviderClass = findDataProviderImpl();

        // Get the classes of the arguments passed.
        final List<Class<?>> argClasses = new ArrayList<Class<?>>();
        for (final Object arg : args) {
            argClasses.add(arg.getClass());
        }

        // Find the constructor that matches the arguments passed.
        final DataProviderFactory factory;
        try {
            final Constructor<? extends DataProviderFactory> dataProviderConstructor = dataProviderClass.getConstructor(
                    argClasses.toArray(new Class<?>[argClasses.size()]));

            factory = (DataProviderFactory) dataProviderConstructor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return factory;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends DataProviderFactory> findDataProviderImpl() {
        // Get the class loader to be used to load the factory.
        final ClassLoader classLoader = getContextClassLoader();

        final String serviceId = "META-INF/services/" + DataProviderFactory.class.getName();
        // try to find services in CLASSPATH
        try {
            InputStream is;
            if (classLoader == null) {
                is = ClassLoader.getSystemResourceAsStream(serviceId);
            } else {
                is = classLoader.getResourceAsStream(serviceId);
            }

            if (is != null) {
                // Read the class name from the services meta-inf
                final BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                final String factoryClassName = rd.readLine();
                rd.close();

                // Find the class for the specified class name
                if (factoryClassName != null && !"".equals(factoryClassName)) {
                    try {
                        return (Class<? extends DataProviderFactory>) findClass(factoryClassName, classLoader);
                    } catch (ClassNotFoundException e) {
                        final URL url = classLoader.getResource(serviceId);
                        throw new ClassNotFoundException("Could not find from factory file" + url, e);
                    }
                } else {
                    final URL url = classLoader.getResource(serviceId);
                    throw new ClassNotFoundException("Could not find from factory file" + url);
                }
            } else {
                final URL url = classLoader.getResource(serviceId);
                throw new ClassNotFoundException("Could not find from factory file" + url);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates an instance of the specified class using the specified <code>ClassLoader</code> object.
     *
     * @throws ClassNotFoundException if the given class could not be found or could not be instantiated
     */
    private static Class<?> findClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
        try {
            Class<?> spiClass;
            if (classLoader == null) {
                spiClass = Class.forName(className);
            } else {
                try {
                    spiClass = Class.forName(className, false, classLoader);
                } catch (ClassNotFoundException ex) {
                    spiClass = Class.forName(className);
                }
            }
            return spiClass;
        } catch (ClassNotFoundException x) {
            throw x;
        } catch (Exception x) {
            throw new ClassNotFoundException("Factory " + className + " could not be instantiated: " + x, x);
        }
    }

    protected DataProviderFactory() {
        // Get the wrapper factory to be used by the data providers.
        wrapperFactory = WrapperFactory.getInstance();
    }

    /**
     * Get a Data Provider instance associated with the Application for the specified class.
     *
     * @param clazz The class of the Data Provider to get an instance of.
     * @param <T>   A DataProvider interface or implementation.
     * @return The created data provider instance if one was able to be found.
     */
    public <T> T getProvider(final Class<T> clazz) {
        return loadProvider(clazz);
    }

    /**
     * Gets the WrapperFactory associated with the factory.
     *
     * @return The wrapper factory associated with the ProviderFactory.
     */
    protected WrapperFactory getWrapperFactory() {
        return wrapperFactory;
    }

    /**
     * Load a provider for a specified class.
     *
     * @param clazz The class of the Data Provider to load.
     * @param <T>   A DataProvider interface or implementation.
     * @return The created data provider instance if one was able to be found.
     */
    protected abstract <T> T loadProvider(final Class<T> clazz);
}
