package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.base.EntityWrapper;

public interface BlobConstantWrapper extends EntityWrapper<BlobConstantWrapper> {
    String getName();

    void setName(String name);

    byte[] getValue();

    void setValue(byte[] value);
}
