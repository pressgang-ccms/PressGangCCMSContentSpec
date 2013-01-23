package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.CSMetaDataWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface CSMetaDataProvider {
    CSMetaDataWrapper getCSMetaData(int id);

    CSMetaDataWrapper getCSMetaData(int id, Integer revision);

    CollectionWrapper<CSMetaDataWrapper> getCSMetaDataRevisions(int id, Integer revision);
}
