package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.TagInCategoryWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface TagInCategoryProvider extends TagProvider {
    CollectionWrapper<TagInCategoryWrapper> getTagInCategoryRevisions(int id, Integer revision);
}