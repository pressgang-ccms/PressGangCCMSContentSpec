package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.base.BasePropertyTagWrapper;

public interface PropertyTagInTopicWrapper extends BasePropertyTagWrapper<PropertyTagInTopicWrapper> {
    String getValue();

    void setValue(String value);

    Integer getRelationshipId();

    Boolean isValid();
}
