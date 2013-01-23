package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.base.BasePropertyTagWrapper;

public interface PropertyTagInPropertyCategoryWrapper extends BasePropertyTagWrapper<PropertyTagInPropertyCategoryWrapper> {
    Integer getSort();

    void setSort(Integer sort);

    Integer getRelationshipId();
}
