package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.base.BaseCSMetaDataWrapper;

public interface CSMetaDataInContentSpecWrapper extends BaseCSMetaDataWrapper<CSMetaDataInContentSpecWrapper> {
    String getValue();

    void setValue(String value);
}
