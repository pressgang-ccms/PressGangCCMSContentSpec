package org.jboss.pressgang.ccms.contentspec.wrapper;

public interface BlobConstantWrapper extends EntityWrapper<BlobConstantWrapper> {
    String getName();

    void tempSetName(String name);

    void setName(String name);

    byte[] getValue();

    void tempSetValue(byte[] value);

    void setValue(byte[] value);
}
