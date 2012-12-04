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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.pressgang.ccms.contentspec.provider.DataProvider;

public abstract class WrapperFactory {
    private static Map<Class<?>, WrapperFactory> wrapperFactories = new HashMap<Class<?>, WrapperFactory>();

    private final DataProvider dataProvider;

    static ClassLoader getContextClassLoader() {
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
    
    @SuppressWarnings("unchecked")
    public static <T extends WrapperFactory> T getInstance(final Class<T> clazz) {
        if (!wrapperFactories.containsKey(clazz)) {
            try {
                // Find the defined wrapper factory implementation class.
                final Constructor<?> constructor = clazz.getConstructors()[0];
                wrapperFactories.put(clazz, (WrapperFactory) constructor.newInstance(DataProvider.getInstance()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        return (T) wrapperFactories.get(clazz);
    }

    public static WrapperFactory getInstance() {
        if (wrapperFactories.isEmpty()) {
            /*if (dataProviders.isEmpty()) {
                throw new IllegalStateException("No Data Providers have been registered.");
            }

            // Find the defined wrapper factory implementation class.
            final Class<?> wrapperClass = findWrapperFactoryImpl();

            try {
                // Find the constructor for the class.
                final Constructor<?> constructor = wrapperClass.getConstructors()[0];
                final Class<?> dataProviderClass = constructor.getParameterTypes()[0];

                // Find a data provider that works with the WrapperFactory
                DataProvider dataProvider = null;
                for (final DataProvider provider : dataProviders) {
                    if (provider.getClass().equals(dataProviderClass)) {
                        dataProvider = provider;
                    }
                }

                // Check to make sure we have a data provider registered.
                if (dataProvider == null) {
                    throw new IllegalStateException("No Data Providers have been registered that are usable by the \""
                            + wrapperClass.getName() + "\" WrapperFactory.");
                }

                wrapperFactory = (WrapperFactory) constructor.newInstance(dataProvider);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }*/
            
            try {
                // Find the defined wrapper factory implementation class.
                final Class<? extends WrapperFactory> wrapperClass = findWrapperFactoryImpl();
                final Constructor<?> constructor = wrapperClass.getConstructors()[0];
                final WrapperFactory wrapperFactory = (WrapperFactory) constructor.newInstance(DataProvider.getInstance());
                wrapperFactories.put(wrapperClass, wrapperFactory);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return wrapperFactories.values().iterator().next();
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

    protected WrapperFactory(final DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    /*
     * TOPIC METHODS
     */
    
    public abstract TopicWrapper createNewTopic();
    public abstract TopicWrapper createTopic(final Object entity);

    public List<TopicWrapper> createTopicList(final Collection<?> entities) {
        final List<TopicWrapper> retValue = new ArrayList<TopicWrapper>();
        for (final Object object : entities) {
            retValue.add(createTopic(object));
        }

        return retValue;
    }
    
    /*
     * TOPIC SOURCE URL METHODS
     */
    
    public abstract TopicSourceURLWrapper createNewTopicSourceURL();
    public abstract TopicSourceURLWrapper createTopicSourceURL(final Object entity);

    public List<TopicSourceURLWrapper> createTopicSourceURLList(final Collection<?> entities) {
        final List<TopicSourceURLWrapper> retValue = new ArrayList<TopicSourceURLWrapper>();
        for (final Object object : entities) {
            retValue.add(createTopicSourceURL(object));
        }

        return retValue;
    }
    
    /*
     * TRANSLATED TOPIC METHODS
     */
    
    public abstract TranslatedTopicWrapper createNewTranslatedTopic();
    public abstract TranslatedTopicWrapper createTranslatedTopic(final Object entity);

    public List<TranslatedTopicWrapper> createTranslatedTopicList(final Collection<?> entities) {
        final List<TranslatedTopicWrapper> retValue = new ArrayList<TranslatedTopicWrapper>();
        for (final Object object : entities) {
            retValue.add(createTranslatedTopic(object));
        }

        return retValue;
    }
    
    /*
     * TRANSLATED TOPIC STRING METHODS
     */
    
    public abstract TranslatedTopicStringWrapper createNewTranslatedTopicString();
    public abstract TranslatedTopicStringWrapper createTranslatedTopicString(final Object entity);

    public List<TranslatedTopicStringWrapper> createTranslatedTopicStringList(final Collection<?> entities) {
        final List<TranslatedTopicStringWrapper> retValue = new ArrayList<TranslatedTopicStringWrapper>();
        for (final Object object : entities) {
            retValue.add(createTranslatedTopicString(object));
        }

        return retValue;
    }

    /*
     * TAG METHODS
     */

    public abstract TagWrapper createNewTag();
    public abstract TagWrapper createTag(final Object entity);

    public List<TagWrapper> createTagList(final Collection<?> entities) {
        final List<TagWrapper> retValue = new ArrayList<TagWrapper>();
        for (final Object object : entities) {
            retValue.add(createTag(object));
        }

        return retValue;
    }
    
    /*
     * CATEGORY METHODS
     */

    public abstract CategoryWrapper createNewCategory();
    public abstract CategoryWrapper createCategory(final Object entity);

    public List<CategoryWrapper> createCategoryList(final Collection<?> entities) {
        final List<CategoryWrapper> retValue = new ArrayList<CategoryWrapper>();
        for (final Object object : entities) {
            retValue.add(createCategory(object));
        }

        return retValue;
    }

    /*
     * PROPERTY TAG METHODS
     */
    
    public abstract PropertyTagWrapper createNewPropertyTag();
    public abstract PropertyTagWrapper createPropertyTag(final Object entity);

    public List<PropertyTagWrapper> createPropertyTagList(final Collection<?> entities) {
        final List<PropertyTagWrapper> retValue = new ArrayList<PropertyTagWrapper>();
        for (final Object object : entities) {
            retValue.add(createPropertyTag(object));
        }

        return retValue;
    }
    
    /*
     * BLOB CONSTANT METHODS
     */

    public abstract BlobConstantWrapper createNewBlobConstant();
    public abstract BlobConstantWrapper createBlobConstant(final Object entity);

    public List<BlobConstantWrapper> createBlobConstantList(final Collection<?> entities) {
        final List<BlobConstantWrapper> retValue = new ArrayList<BlobConstantWrapper>();
        for (final Object object : entities) {
            retValue.add(createBlobConstant(object));
        }

        return retValue;
    }

    /*
     * STRING CONSTANT METHODS
     */
    
    public abstract StringConstantWrapper createNewStringConstant();
    public abstract StringConstantWrapper createStringConstant(final Object entity);

    public List<StringConstantWrapper> createStringConstantList(final Collection<?> entities) {
        final List<StringConstantWrapper> retValue = new ArrayList<StringConstantWrapper>();
        for (final Object object : entities) {
            retValue.add(createStringConstant(object));
        }

        return retValue;
    }
    
    /*
     * IMAGE METHODS
     */
    
    public abstract ImageWrapper createNewImage();
    public abstract ImageWrapper createImage(final Object entity);

    public List<ImageWrapper> createImageList(final Collection<?> entities) {
        final List<ImageWrapper> retValue = new ArrayList<ImageWrapper>();
        for (final Object object : entities) {
            retValue.add(createImage(object));
        }

        return retValue;
    }
    
    /*
     * LANGUAGE IMAGE METHODS
     */
    
    public abstract LanguageImageWrapper createNewLanguageImage(final ImageWrapper parent);
    public abstract LanguageImageWrapper createLanguageImage(final Object entity, final ImageWrapper parent);

    public List<LanguageImageWrapper> createLanguageImageList(final Collection<?> entities, final ImageWrapper parent) {
        final List<LanguageImageWrapper> retValue = new ArrayList<LanguageImageWrapper>();
        for (final Object object : entities) {
            retValue.add(createLanguageImage(object, parent));
        }

        return retValue;
    }

    protected DataProvider getDataProvider() {
        return dataProvider;
    }
}
