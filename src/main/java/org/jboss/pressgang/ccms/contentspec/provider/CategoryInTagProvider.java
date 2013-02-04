package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.CategoryInTagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface CategoryInTagProvider extends CategoryProvider {
    CollectionWrapper<CategoryInTagWrapper> getCategoryInTagRevisions(int id, Integer revision);
}
