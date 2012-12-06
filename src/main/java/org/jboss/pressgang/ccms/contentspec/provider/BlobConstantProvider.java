package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.BlobConstantWrapper;

public interface BlobConstantProvider {
    BlobConstantWrapper getBlobConstant(final int id);
}
