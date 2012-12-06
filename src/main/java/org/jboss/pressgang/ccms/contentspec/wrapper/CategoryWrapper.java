package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface CategoryWrapper extends EntityWrapper<CategoryWrapper> {
    CollectionWrapper<TagWrapper> getTags();
    boolean isMutuallyExclusive();
    String getName();
}
