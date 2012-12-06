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

public abstract class DataProviderFactory {
    private static DataProviderFactory factory = null;
    private static WrapperFactory wrapperFactory = null;

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

    public static void initialise(final Object... args) {
        final Class<? extends DataProviderFactory> dataProviderClass = findDataProviderImpl();

        final List<Class<?>> argClasses = new ArrayList<Class<?>>();
        for (final Object arg : args) {
            argClasses.add(arg.getClass());
        }

        try {
            final Constructor<? extends DataProviderFactory> dataProviderConstructor = dataProviderClass.getConstructor(argClasses
                    .toArray(new Class<?>[argClasses.size()]));

            factory = (DataProviderFactory) dataProviderConstructor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        setWrapperFactory(WrapperFactory.getInstance());
    }

    public static <T> T getInstance(final Class<T> clazz) {
        if (factory == null) {
            throw new IllegalStateException("The Data Provider Factory hasn't been initialised.");
        }

        return factory.loadProvider(clazz);
    }

    public static WrapperFactory getWrapperFactory() {
        return wrapperFactory;
    }

    public static void setWrapperFactory(WrapperFactory wrapperFactory) {
        DataProviderFactory.wrapperFactory = wrapperFactory;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends DataProviderFactory> findDataProviderImpl() {
        ClassLoader classLoader = getContextClassLoader();

        String serviceId = "META-INF/services/" + DataProviderFactory.class.getName();
        // try to find services in CLASSPATH
        try {
            InputStream is;
            if (classLoader == null) {
                is = ClassLoader.getSystemResourceAsStream(serviceId);
            } else {
                is = classLoader.getResourceAsStream(serviceId);
            }

            if (is != null) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                String factoryClassName = rd.readLine();
                rd.close();

                if (factoryClassName != null && !"".equals(factoryClassName)) {
                    try {
                        return (Class<? extends DataProviderFactory>) findClass(factoryClassName, classLoader);
                    } catch (ClassNotFoundException e) {
                        URL url = classLoader.getResource(serviceId);

                        throw new ClassNotFoundException("Could not find from factory file" + url, e);
                    }
                } else {
                    URL url = classLoader.getResource(serviceId);

                    throw new ClassNotFoundException("Could not find from factory file" + url);
                }
            } else {
                URL url = classLoader.getResource(serviceId);

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
    
    protected abstract <T> T loadProvider(final Class<T> clazz);
}
