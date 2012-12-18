package org.jboss.pressgang.ccms.contentspec.wrapper;

public interface StringConstantWrapper extends EntityWrapper<StringConstantWrapper> {
    String getName();

    void tempSetName(String name);

    void setName(String name);

    String getValue();

    void tempSetValue(String value);

    void setValue(String value);
}
