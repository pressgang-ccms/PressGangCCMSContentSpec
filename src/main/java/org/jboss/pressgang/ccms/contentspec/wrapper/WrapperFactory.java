package org.jboss.pressgang.ccms.contentspec.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.jboss.pressgang.ccms.contentspec.interfaces.DataProvider;

public abstract class WrapperFactory {
    private static WrapperFactory wrapperFactory;
    
    private final DataProvider dataProvider;
    
    public static WrapperFactory getInstance() {
        // TODO
        return null;
    }
    
    public static <T extends WrapperFactory> T getInstance(final Class<T> clazz) {
        return null;
    }
    
    protected WrapperFactory(final DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }
    
    public abstract TopicWrapper createTopic(final Object entity);
    public List<TopicWrapper> createTopicList(final List<?> entities) {
        final List<TopicWrapper> retValue = new ArrayList<TopicWrapper>();
        for (final Object object : entities) {
            retValue.add(createTopic(object));
        }
        
        return retValue;
    }
    
    public abstract TagWrapper createTag(final Object entity);
    public List<TagWrapper> createTagList(final List<?> entities) {
        final List<TagWrapper> retValue = new ArrayList<TagWrapper>();
        for (final Object object : entities) {
            retValue.add(createTag(object));
        }
        
        return retValue;
    }
    
    public abstract CategoryWrapper createCategory(final Object entity);
    public List<CategoryWrapper> createCategoryList(final List<?> entities) {
        final List<CategoryWrapper> retValue = new ArrayList<CategoryWrapper>();
        for (final Object object : entities) {
            retValue.add(createCategory(object));
        }
        
        return retValue;
    }
    
    public abstract PropertyTagWrapper createPropertyTag(final Object entity);
    public List<PropertyTagWrapper> createPropertyTagList(final List<?> entities) {
        final List<PropertyTagWrapper> retValue = new ArrayList<PropertyTagWrapper>();
        for (final Object object : entities) {
            retValue.add(createPropertyTag(object));
        }
        
        return retValue;
    }
    
    public abstract BlobConstantWrapper createBlobConstant(final Object entity);
    public List<BlobConstantWrapper> createBlobConstantList(final List<?> entities) {
        final List<BlobConstantWrapper> retValue = new ArrayList<BlobConstantWrapper>();
        for (final Object object : entities) {
            retValue.add(createBlobConstant(object));
        }
        
        return retValue;
    }
    
    public abstract StringConstantWrapper createStringConstant(final Object entity);
    public List<StringConstantWrapper> createStringConstantList(final List<?> entities) {
        final List<StringConstantWrapper> retValue = new ArrayList<StringConstantWrapper>();
        for (final Object object : entities) {
            retValue.add(createStringConstant(object));
        }
        
        return retValue;
    }

    protected DataProvider getDataProvider() {
        return dataProvider;
    }
}
