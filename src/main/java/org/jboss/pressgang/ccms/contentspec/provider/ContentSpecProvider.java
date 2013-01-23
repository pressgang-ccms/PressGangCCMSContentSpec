package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.CSMetaDataInContentSpecWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.ContentSpecWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagInContentSpecWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface ContentSpecProvider {
    ContentSpecWrapper getContentSpec(int id);

    ContentSpecWrapper getContentSpec(int id, Integer revision);

    CollectionWrapper<ContentSpecWrapper> getContentSpecsWithQuery(String query);

    CollectionWrapper<TagWrapper> getContentSpecTags(int id, Integer revision);

    UpdateableCollectionWrapper<CSMetaDataInContentSpecWrapper> getContentSpecMetaData(int id, Integer revision);

    UpdateableCollectionWrapper<PropertyTagInContentSpecWrapper> getContentSpecProperties(int id, Integer revision);

    CollectionWrapper<CSNodeWrapper> getContentSpecNodes(int id, Integer revision);

    CollectionWrapper<ContentSpecWrapper> getContentSpecRevisions(int id, Integer revision);
}
