package org.jboss.pressgang.ccms.contentspec.wrapper;

import java.util.List;

import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface TagWrapper extends EntityWrapper<TagWrapper> {
    String getName();

    CollectionWrapper<TagWrapper> getParentTags();

    CollectionWrapper<TagWrapper> getChildTags();

    UpdateableCollectionWrapper<CategoryWrapper> getCategories();

    PropertyTagWrapper getProperty(final int propertyId);

    Integer getInCategorySort();

    boolean containedInCategory(int categoryId);

    boolean containedInCategories(List<Integer> categoryIds);
}
