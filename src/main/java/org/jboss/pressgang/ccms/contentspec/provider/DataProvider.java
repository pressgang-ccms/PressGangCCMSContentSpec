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

import org.jboss.pressgang.ccms.contentspec.wrapper.BlobConstantWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.CategoryWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.StringConstantWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TranslatedTopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.WrapperFactory;

public abstract class DataProvider {
    private static DataProvider dataProvider = null;
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
        final Class<? extends DataProvider> dataProviderClass = findDataProviderImpl();
        
        final List<Class<?>> argClasses = new ArrayList<Class<?>>();
        for (final Object arg : args) {
            argClasses.add(arg.getClass());
        }
        
        try {
            final Constructor<? extends DataProvider> dataProviderConstructor = dataProviderClass.getConstructor(argClasses.toArray(new Class<?>[argClasses.size()]));
            
            dataProvider = (DataProvider) dataProviderConstructor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        setWrapperFactory(WrapperFactory.getInstance());
    }
    
    public static DataProvider getInstance() {
        if (dataProvider == null) {
            throw new IllegalStateException("The Data Provider hasn't been initialised.");
        }
        
        return dataProvider;
    }
    
    public static WrapperFactory getWrapperFactory() {
        return wrapperFactory;
    }

    public static void setWrapperFactory(WrapperFactory wrapperFactory) {
        DataProvider.wrapperFactory = wrapperFactory;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends DataProvider> findDataProviderImpl() {
        ClassLoader classLoader = getContextClassLoader();

        String serviceId = "META-INF/services/" + DataProvider.class.getName();
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
                        return (Class<? extends DataProvider>) findClass(factoryClassName, classLoader);
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
    
    /*
     * TOPIC METHODS
     */
    public abstract TopicWrapper getTopic(int id);
    public abstract TopicWrapper getTopic(int id, final Integer revision);
    public abstract List<TopicWrapper> getTopics(final List<Integer> ids);
    public abstract List<TagWrapper> getTopicTags(int id, final Integer revision);
    public abstract List<TranslatedTopicWrapper> getTopicTranslations(int id, final Integer revision);
    
    /*
     * TRANSLATED TOPIC METHODS
     */
    public abstract TranslatedTopicWrapper getTranslatedTopic(final int id);
    public abstract TranslatedTopicWrapper getTranslatedTopic(final int id, final Integer revision);
    
    /*
     * TAG METHODS
     */
    public abstract TagWrapper getTag(final int id);
    public abstract List<TagWrapper> getTagsByName(final String name);
    public abstract List<CategoryWrapper> getTagCategories(final int id);
    
    /*
     * CATEGORY METHODS
     */
    public abstract CategoryWrapper getCategory(final int id);
    
    /*
     * BLOB CONSTANT METHODS
     */
    public abstract BlobConstantWrapper getBlobConstant(final int id);
    
    /*
     * STRING CONSTANT PROVIDER
     */
    public abstract StringConstantWrapper getStringConstant(final int id);
}
