package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.CategoryWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagInCategoryWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface CategoryProvider {
    CategoryWrapper getCategory(int id);

    CategoryWrapper getCategory(int id, Integer revision);

    UpdateableCollectionWrapper<TagInCategoryWrapper> getCategoryTags(int id, Integer revision);

    CollectionWrapper<CategoryWrapper> getCategoryRevisions(int id, Integer revision);
}
