package org.jboss.pressgang.ccms.contentspec.wrapper.base;

import java.util.List;

import org.jboss.pressgang.ccms.contentspec.wrapper.CategoryInTagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagInTagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface BaseTagWrapper<T extends BaseTagWrapper<T>> extends EntityWrapper<T> {
    String getName();

    CollectionWrapper<TagWrapper> getParentTags();

    CollectionWrapper<TagWrapper> getChildTags();

    UpdateableCollectionWrapper<CategoryInTagWrapper> getCategories();

    PropertyTagInTagWrapper getProperty(final int propertyId);

    boolean containedInCategory(int categoryId);

    boolean containedInCategories(List<Integer> categoryIds);
}
