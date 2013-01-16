package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface CategoryWrapper extends EntityWrapper<CategoryWrapper> {
    CollectionWrapper<TagWrapper> getTags();

    void setTags(CollectionWrapper<TagWrapper> tags);

    boolean isMutuallyExclusive();

    String getName();

    void setName(String name);

    Integer getRelationshipId();

    Integer getRelationshipSort();
}
