package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface PropertyTagProvider {
    PropertyTagWrapper getPropertyTag(int id);

    PropertyTagWrapper getPropertyTag(int id, Integer revision);

    UpdateableCollectionWrapper<PropertyTagWrapper> getPropertyTagCategories(int id, Integer revision);

    PropertyTagWrapper newPropertyTag();

    PropertyTagWrapper newAssignedPropertyTag();

    PropertyTagWrapper newPropertyTagInPropertyCategory();

    CollectionWrapper<PropertyTagWrapper> newPropertyTagCollection();

    UpdateableCollectionWrapper<PropertyTagWrapper> newAssignedPropertyTagCollection();

    UpdateableCollectionWrapper<PropertyTagWrapper> newPropertyTagInPropertyCategoryCollection();
}
