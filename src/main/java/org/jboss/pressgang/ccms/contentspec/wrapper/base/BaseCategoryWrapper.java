package org.jboss.pressgang.ccms.contentspec.wrapper.base;

import org.jboss.pressgang.ccms.contentspec.wrapper.TagInCategoryWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface BaseCategoryWrapper<T extends BaseCategoryWrapper<T>> extends EntityWrapper<T> {
    UpdateableCollectionWrapper<TagInCategoryWrapper> getTags();

    void setTags(UpdateableCollectionWrapper<TagInCategoryWrapper> tags);

    boolean isMutuallyExclusive();

    String getName();

    void setName(String name);
}
