package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.CSMetaDataWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.ContentSpecWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface ContentSpecProvider {
    ContentSpecWrapper getContentSpec(int id);

    ContentSpecWrapper getContentSpec(int id, Integer revision);

    UpdateableCollectionWrapper<CSMetaDataWrapper> getContentSpecMetaData(int id, Integer revision);

    CollectionWrapper<CSNodeWrapper> getContentSpecNodes(int id, Integer revision);

    CollectionWrapper<ContentSpecWrapper> getContentSpecRevisions(int id, Integer revision);
}
