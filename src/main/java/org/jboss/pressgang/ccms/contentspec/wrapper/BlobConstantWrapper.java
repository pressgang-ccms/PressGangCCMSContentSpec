package org.jboss.pressgang.ccms.contentspec.wrapper;

public interface BlobConstantWrapper extends EntityWrapper<BlobConstantWrapper> {
    String getName();
    byte[] getValue();
}
