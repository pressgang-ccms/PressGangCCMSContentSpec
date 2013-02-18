package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagInContentSpecWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagInPropertyCategoryWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagInTagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagInTopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface PropertyTagProvider {
    PropertyTagWrapper getPropertyTag(int id);

    PropertyTagWrapper getPropertyTag(int id, Integer revision);

    //UpdateableCollectionWrapper<PropertyCategoryWrapper> getPropertyTagCategories(int id, Integer revision);
    CollectionWrapper<PropertyTagWrapper> getPropertyTagRevisions(int id, Integer revision);

    PropertyTagWrapper newPropertyTag();

    PropertyTagInTopicWrapper newPropertyTagInTopic();

    PropertyTagInTopicWrapper newPropertyTagInTopic(PropertyTagWrapper propertyTag);

    PropertyTagInTagWrapper newPropertyTagInTag();

    PropertyTagInTagWrapper newPropertyTagInTag(PropertyTagWrapper propertyTag);

    PropertyTagInContentSpecWrapper newPropertyTagInContentSpec();

    PropertyTagInContentSpecWrapper newPropertyTagInContentSpec(PropertyTagWrapper propertyTag);

    PropertyTagInPropertyCategoryWrapper newPropertyTagInPropertyCategory();

    CollectionWrapper<PropertyTagWrapper> newPropertyTagCollection();

    UpdateableCollectionWrapper<PropertyTagInTopicWrapper> newPropertyTagInTopicCollection();

    UpdateableCollectionWrapper<PropertyTagInTagWrapper> newPropertyTagInTagCollection();

    UpdateableCollectionWrapper<PropertyTagInContentSpecWrapper> newPropertyTagInContentSpecCollection();

    UpdateableCollectionWrapper<PropertyTagInPropertyCategoryWrapper> newPropertyTagInPropertyCategoryCollection();
}
