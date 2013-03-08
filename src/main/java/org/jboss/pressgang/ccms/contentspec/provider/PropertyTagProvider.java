package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.ContentSpecWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagInContentSpecWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagInPropertyCategoryWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagInTagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagInTopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.base.BaseTopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface PropertyTagProvider {
    PropertyTagWrapper getPropertyTag(int id);

    PropertyTagWrapper getPropertyTag(int id, Integer revision);

    //UpdateableCollectionWrapper<PropertyCategoryWrapper> getPropertyTagCategories(int id, Integer revision);
    CollectionWrapper<PropertyTagWrapper> getPropertyTagRevisions(int id, Integer revision);

    PropertyTagWrapper newPropertyTag();

    PropertyTagInTopicWrapper newPropertyTagInTopic(BaseTopicWrapper<?> topic);

    PropertyTagInTopicWrapper newPropertyTagInTopic(PropertyTagWrapper propertyTag, BaseTopicWrapper<?> topic);

    PropertyTagInTagWrapper newPropertyTagInTag(TagWrapper tag);

    PropertyTagInTagWrapper newPropertyTagInTag(PropertyTagWrapper propertyTag, TagWrapper tag);

    PropertyTagInContentSpecWrapper newPropertyTagInContentSpec(ContentSpecWrapper contentSpec);

    PropertyTagInContentSpecWrapper newPropertyTagInContentSpec(PropertyTagWrapper propertyTag, ContentSpecWrapper contentSpec);

    PropertyTagInPropertyCategoryWrapper newPropertyTagInPropertyCategory();

    CollectionWrapper<PropertyTagWrapper> newPropertyTagCollection();

    UpdateableCollectionWrapper<PropertyTagInTopicWrapper> newPropertyTagInTopicCollection(BaseTopicWrapper<?> topic);

    UpdateableCollectionWrapper<PropertyTagInTagWrapper> newPropertyTagInTagCollection(TagWrapper tag);

    UpdateableCollectionWrapper<PropertyTagInContentSpecWrapper> newPropertyTagInContentSpecCollection(ContentSpecWrapper contentSpec);

    UpdateableCollectionWrapper<PropertyTagInPropertyCategoryWrapper> newPropertyTagInPropertyCategoryCollection();
}
