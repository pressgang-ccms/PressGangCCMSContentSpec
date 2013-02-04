package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.CSMetaDataInContentSpecWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface CSMetaDataInContentSpecProvider extends CSMetaDataProvider {
    CollectionWrapper<CSMetaDataInContentSpecWrapper> getCSMetaDataInContentSpecRevisions(int id, Integer revision);
}
