package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.CategoryWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface CategoryProvider {
    CategoryWrapper getCategory(int id);

    CategoryWrapper getCategory(int id, Integer revision);

    UpdateableCollectionWrapper<TagWrapper> getCategoryTags(int id, Integer revision);

    CollectionWrapper<CategoryWrapper> getCategoryRevisions(int id, Integer revision);
}
