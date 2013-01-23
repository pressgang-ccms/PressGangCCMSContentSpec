package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.base.BaseCategoryWrapper;

public interface CategoryInTagWrapper extends BaseCategoryWrapper<CategoryInTagWrapper> {
    Integer getRelationshipId();

    Integer getRelationshipSort();
}
