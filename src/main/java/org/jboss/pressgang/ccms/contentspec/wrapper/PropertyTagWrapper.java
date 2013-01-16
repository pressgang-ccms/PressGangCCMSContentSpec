package org.jboss.pressgang.ccms.contentspec.wrapper;

public interface PropertyTagWrapper extends EntityWrapper<PropertyTagWrapper> {
    String getValue();

    void setValue(String value);

    Boolean isValid();

    Integer getRelationshipId();
}
