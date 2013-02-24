package org.jboss.pressgang.ccms.contentspec.provider;

import java.util.List;

import org.jboss.pressgang.ccms.contentspec.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.CSRelatedNodeWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface CSNodeProvider {
    CSNodeWrapper getCSNode(int id);

    CSNodeWrapper getCSNode(int id, Integer revision);

    CollectionWrapper<CSRelatedNodeWrapper> getCSRelatedToNodes(int id, Integer revision);

    CollectionWrapper<CSRelatedNodeWrapper> getCSRelatedFromNodes(int id, Integer revision);

    UpdateableCollectionWrapper<CSNodeWrapper> getCSNodeChildren(int id, Integer revision);

    CollectionWrapper<CSNodeWrapper> getCSNodeRevisions(int id, Integer revision);

    CSNodeWrapper createCSNode(CSNodeWrapper node) throws Exception;

    CSNodeWrapper updateCSNode(CSNodeWrapper node) throws Exception;

    boolean deleteCSNode(Integer id) throws Exception;

    CollectionWrapper<CSNodeWrapper> createCSNodes(CollectionWrapper<CSNodeWrapper> nodes) throws Exception;

    CollectionWrapper<CSNodeWrapper> updateCSNodes(CollectionWrapper<CSNodeWrapper> nodes) throws Exception;

    boolean deleteCSNodes(final List<Integer> nodeIds) throws Exception;

    CSNodeWrapper newCSNode();

    UpdateableCollectionWrapper<CSNodeWrapper> newCSNodeCollection();

    CSRelatedNodeWrapper newCSRelatedNode();

    CSRelatedNodeWrapper newCSRelatedNode(final CSNodeWrapper node);

    UpdateableCollectionWrapper<CSRelatedNodeWrapper> newCSRelatedNodeCollection();
}
