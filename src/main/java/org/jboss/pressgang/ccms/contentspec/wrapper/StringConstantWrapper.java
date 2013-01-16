package org.jboss.pressgang.ccms.contentspec.wrapper;

public interface StringConstantWrapper extends EntityWrapper<StringConstantWrapper> {
    String getName();

    void setName(String name);

    String getValue();

    void setValue(String value);
}
