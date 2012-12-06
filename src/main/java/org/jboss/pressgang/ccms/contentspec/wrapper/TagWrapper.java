package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface TagWrapper extends EntityWrapper<TagWrapper> {
    String getTagName();
    CollectionWrapper<TagWrapper> getParentTags();
    CollectionWrapper<TagWrapper> getChildTags();
    CollectionWrapper<CategoryWrapper> getCategories();
    PropertyTagWrapper getProperty(final int propertyId);
    Integer getInCategorySort();
    boolean containedInCategory(int categoryId);
}
