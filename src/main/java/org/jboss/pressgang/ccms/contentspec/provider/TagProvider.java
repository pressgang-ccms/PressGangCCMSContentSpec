package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.CategoryWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface TagProvider {
    TagWrapper getTag(final int id);
    CollectionWrapper<TagWrapper> getTagsByName(final String name);
    UpdateableCollectionWrapper<CategoryWrapper> getTagCategories(int id);
    UpdateableCollectionWrapper<CategoryWrapper> getTagCategories(int id, Integer revision);
    CollectionWrapper<TagWrapper> getTagChildTags(int id, Integer revision);
    CollectionWrapper<TagWrapper> getTagParentTags(int id, Integer revision);
    CollectionWrapper<PropertyTagWrapper> getTagProperties(int id, Integer revision);
}
