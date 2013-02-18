package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.CSTranslatedNodeStringWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.CSTranslatedNodeWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface CSTranslatedNodeProvider {
    CSTranslatedNodeWrapper getCSTranslatedNode(int id);

    CSTranslatedNodeWrapper getCSTranslatedNode(int id, Integer revision);

    UpdateableCollectionWrapper<CSTranslatedNodeStringWrapper> getCSTranslatedNodeStrings(int id, Integer revision);

    CollectionWrapper<CSTranslatedNodeWrapper> getCSTranslatedNodeRevisions(int id, Integer revision);

    CollectionWrapper<CSTranslatedNodeWrapper> createCSTranslatedNodes(
            CollectionWrapper<CSTranslatedNodeWrapper> translatedNodes) throws Exception;

    CSTranslatedNodeWrapper newCSTranslatedNode();

    CollectionWrapper<CSTranslatedNodeWrapper> newCSTranslatedNodeCollection();
}
