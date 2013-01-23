package org.jboss.pressgang.ccms.contentspec.wrapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.pressgang.ccms.contentspec.provider.DataProviderFactory;
import org.jboss.pressgang.ccms.contentspec.wrapper.base.EntityWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public abstract class WrapperFactory {
    private final DataProviderFactory providerFactory;

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

    public static WrapperFactory getInstance(DataProviderFactory providerFactory) {
        try {
            // Find the defined wrapper factory implementation class.
            final Class<? extends WrapperFactory> wrapperClass = findWrapperFactoryImpl();
            final Constructor<? extends WrapperFactory> wrapperFactoryConstructor = wrapperClass.getConstructor(DataProviderFactory.class);
            return wrapperFactoryConstructor.newInstance(providerFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends WrapperFactory> findWrapperFactoryImpl() {
        ClassLoader classLoader = getContextClassLoader();

        String serviceId = "META-INF/services/" + WrapperFactory.class.getName();
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
                        return (Class<? extends WrapperFactory>) findClass(factoryClassName, classLoader);
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

    protected WrapperFactory(final DataProviderFactory providerFactory) {
        this.providerFactory = providerFactory;
    }

    protected DataProviderFactory getProviderFactory() {
        return providerFactory;
    }

    /**
     * Create a wrapper around a specific entity.
     *
     * @param entity     The entity to be wrapped.
     * @param isRevision Whether the entity is a revision or not.
     * @param <T>        The wrapper class that is returned.
     * @return The Wrapper around the entity.
     */
    public abstract <T extends EntityWrapper<T>> T create(final Object entity, boolean isRevision);

    /**
     * Create a list of wrapped entities.
     *
     * @param entities       The collection of entities to wrap.
     * @param isRevisionList Whether or not the collection is a collection of revision entities.
     * @param <T>            The wrapper class that is returned.
     * @return An ArrayList of wrapped entities.
     */
    public <T extends EntityWrapper<T>> List<T> createList(final Collection<?> entities, boolean isRevisionList) {
        final List<T> retValue = new ArrayList<T>();
        for (final Object object : entities) {
            retValue.add((T) create(object, isRevisionList));
        }

        return retValue;
    }

    /**
     * Create a wrapper around a collection of entities.
     *
     * @param collection           The collection to be wrapped.
     * @param entityClass          The class of the entity that the collection contains.
     * @param isRevisionCollection Whether or not the collection is a collection of revision entities.
     * @param <T>                  The wrapper class that is returned.
     * @return The Wrapper around the collection of entities.
     */
    public abstract <T extends EntityWrapper<T>> CollectionWrapper<T> createCollection(final Object entity, final Class<?> entityClass,
            boolean isRevision);
}
