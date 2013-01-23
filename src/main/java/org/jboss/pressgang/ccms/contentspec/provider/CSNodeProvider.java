package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.CSRelatedNodeWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface CSNodeProvider {
    CSNodeWrapper getCSNode(int id);

    CSNodeWrapper getCSNode(int id, Integer revision);

    CollectionWrapper<CSRelatedNodeWrapper> getCSRelatedToNodes(int id, Integer revision);

    CollectionWrapper<CSRelatedNodeWrapper> getCSRelatedFromNodes(int id, Integer revision);

    CollectionWrapper<CSNodeWrapper> getCSNodeChildren(int id, Integer revision);

    CollectionWrapper<CSNodeWrapper> getCSNodeRevisions(int id, Integer revision);
}
