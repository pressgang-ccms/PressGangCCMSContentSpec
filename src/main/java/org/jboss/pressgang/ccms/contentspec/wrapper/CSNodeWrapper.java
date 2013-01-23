package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.base.BaseCSNodeWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface CSNodeWrapper extends BaseCSNodeWrapper<CSNodeWrapper> {
    UpdateableCollectionWrapper<CSRelatedNodeWrapper> getRelatedToNodes();

    void setRelatedToNodes(UpdateableCollectionWrapper<CSRelatedNodeWrapper> relatedToNodes);

    UpdateableCollectionWrapper<CSRelatedNodeWrapper> getRelatedFromNodes();

    void setRelatedFromNodes(UpdateableCollectionWrapper<CSRelatedNodeWrapper> relatedFromNodes);
}
