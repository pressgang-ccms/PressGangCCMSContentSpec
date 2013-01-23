package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.base.BasePropertyTagWrapper;

public interface PropertyTagInContentSpecWrapper extends BasePropertyTagWrapper<PropertyTagInContentSpecWrapper> {
    String getValue();

    void setValue(String value);

    Integer getRelationshipId();

    Boolean isValid();
}
