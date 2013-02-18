package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.CSTranslatedNodeStringWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.CSTranslatedNodeWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface CSTranslatedNodeStringProvider {
    CollectionWrapper<CSTranslatedNodeStringWrapper> getCSTranslatedNodeStringRevisions(int id, Integer revision);

    CSTranslatedNodeStringWrapper newCSTranslatedNodeString();

    CSTranslatedNodeStringWrapper newCSTranslatedNodeString(final CSTranslatedNodeWrapper parent);

    UpdateableCollectionWrapper<CSTranslatedNodeStringWrapper> newCSTranslatedNodeStringCollection();

    UpdateableCollectionWrapper<CSTranslatedNodeStringWrapper> newCSTranslatedNodeStringCollection(final CSTranslatedNodeWrapper parent);
}
