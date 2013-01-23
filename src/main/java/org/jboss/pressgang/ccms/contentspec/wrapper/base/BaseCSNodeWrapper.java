package org.jboss.pressgang.ccms.contentspec.wrapper.base;

import org.jboss.pressgang.ccms.contentspec.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface BaseCSNodeWrapper<T extends BaseCSNodeWrapper<T>> extends EntityWrapper<T> {
    CollectionWrapper<CSNodeWrapper> getChildren();

    void setChildren(CollectionWrapper<CSNodeWrapper> nodes);

    String getTitle();

    void setTitle(String title);

    String getAlternateTitle();

    void setAlternateTitle(String alternateTitle);

    String getCondition();

    void setCondition(String condition);

    Integer getTopicId();

    void setTopicId(Integer id);

    Integer getTopicRevision();

    void setTopicRevision(Integer revision);

    Integer getNextNodeId();

    void setNextNodeId(Integer id);

    Integer getPreviousNodeId();

    void setPreviousNodeId(Integer id);

    Integer getNodeType();

    void setNodeType(Integer typeId);
}
