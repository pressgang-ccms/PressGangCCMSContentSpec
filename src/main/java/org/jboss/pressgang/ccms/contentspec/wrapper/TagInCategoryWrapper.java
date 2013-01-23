package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.base.BaseTagWrapper;

public interface TagInCategoryWrapper extends BaseTagWrapper<TagInCategoryWrapper> {
    Integer getInCategorySort();

    Integer getRelationshipId();
}
